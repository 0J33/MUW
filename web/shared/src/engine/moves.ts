import { applyEffect, hasEffect, makeEffect, removeEffect, tickCooldowns, tickEffects } from './effects.js';
import { sortTurnOrder } from './priorityQueue.js';
import { rand } from './rng.js';
import { checkGameOver, currentChampion } from './setup.js';
import {
  BOARD_HEIGHT,
  BOARD_WIDTH,
  otherIndex,
  type AbilityInstance,
  type Cell,
  type ChampionInstance,
  type Direction,
  type GameState,
} from './state.js';

// All action reducers mutate the passed state in place and return a structured
// result (either { ok: true, events } or { ok: false, error }). Callers either
// wrap in a snapshot/rollback or throw on error. No throws from here.

export type ActionResult =
  | { ok: true; events: string[] }
  | { ok: false; error: string; code: ActionErrorCode };

export type ActionErrorCode =
  | 'NOT_YOUR_TURN'
  | 'GAME_OVER'
  | 'NO_CHAMPION'
  | 'ROOTED'
  | 'DISARMED'
  | 'SILENCED'
  | 'STUNNED'
  | 'NOT_ENOUGH_AP'
  | 'NOT_ENOUGH_MANA'
  | 'COOLDOWN'
  | 'OUT_OF_BOUNDS'
  | 'OCCUPIED'
  | 'INVALID_TARGET'
  | 'LEADER_NOT_CURRENT'
  | 'LEADER_ALREADY_USED'
  | 'UNKNOWN_ABILITY';

function ok(events: string[] = []): ActionResult {
  return { ok: true, events };
}
function err(code: ActionErrorCode, error: string): ActionResult {
  return { ok: false, code, error };
}

export function actingPlayer(state: GameState): 0 | 1 | null {
  const c = currentChampion(state);
  return c ? c.ownerIndex : null;
}

function cellAt(state: GameState, x: number, y: number): Cell {
  if (x < 0 || x >= BOARD_WIDTH || y < 0 || y >= BOARD_HEIGHT) return null;
  return state.board[x]![y]!;
}

function setCell(state: GameState, x: number, y: number, cell: Cell): void {
  state.board[x]![y] = cell;
}

function applyDirection(x: number, y: number, d: Direction): { x: number; y: number } {
  // Mirrors Game.java:243-251 — UP/DOWN change x (row), LEFT/RIGHT change y (col).
  if (d === 'UP') return { x: x + 1, y };
  if (d === 'DOWN') return { x: x - 1, y };
  if (d === 'LEFT') return { x, y: y - 1 };
  return { x, y: y + 1 };
}

function sameTeam(state: GameState, a: ChampionInstance, b: ChampionInstance): boolean {
  return a.ownerIndex === b.ownerIndex;
}

// Remove KO'd champions from the board, teams, and turn order. Mirrors
// Game.cleanup (Game.java:592-614).
function cleanup(state: GameState, targets: ChampionInstance[]): void {
  for (const t of targets) {
    if (t.currentHP > 0) continue;
    setCell(state, t.x, t.y, null);
    delete state.champions[t.id];
    for (const p of state.players) {
      const idx = p.teamIds.indexOf(t.id);
      if (idx >= 0) p.teamIds.splice(idx, 1);
      if (p.leaderId === t.id) p.leaderId = null;
    }
    const toIdx = state.turnOrder.indexOf(t.id);
    if (toIdx >= 0) state.turnOrder.splice(toIdx, 1);
  }
}

// Run after any reducer that could KO a champion. As soon as one team is
// empty, the match ends — no need to wait for an explicit endTurn.
function checkAndFinish(state: GameState): void {
  if (state.phase !== 'active') return;
  const winner = checkGameOver(state);
  if (winner !== null) {
    state.phase = 'ended';
    state.winnerIndex = winner;
    state.endReason = 'killed';
  }
}

function validateSameActor(state: GameState, userId: string): 0 | 1 | null {
  const c = currentChampion(state);
  if (!c) return null;
  const p = state.players[c.ownerIndex];
  return p.userId === userId ? c.ownerIndex : null;
}

function preActionChecks(state: GameState, userId: string): ActionResult | null {
  if (state.phase !== 'active') return err('GAME_OVER', 'game is not active');
  const idx = validateSameActor(state, userId);
  if (idx === null) return err('NOT_YOUR_TURN', 'not your turn');
  return null;
}

// ─── move ─────────────────────────────────────────────────────────────
export function applyMove(state: GameState, userId: string, d: Direction): ActionResult {
  const pre = preActionChecks(state, userId);
  if (pre) return pre;
  const c = currentChampion(state);
  if (!c) return err('NO_CHAMPION', 'no current champion');
  if (hasEffect(c, 'Root')) return err('ROOTED', 'cannot move while rooted');
  if (c.currentActionPoints < 1) return err('NOT_ENOUGH_AP', 'need 1 action point to move');
  const { x: nx, y: ny } = applyDirection(c.x, c.y, d);
  if (nx < 0 || nx >= BOARD_HEIGHT || ny < 0 || ny >= BOARD_WIDTH) {
    return err('OUT_OF_BOUNDS', 'cannot move out of the board');
  }
  if (cellAt(state, nx, ny) !== null) return err('OCCUPIED', 'target cell is not empty');
  setCell(state, c.x, c.y, null);
  c.x = nx;
  c.y = ny;
  setCell(state, nx, ny, { type: 'champion', id: c.id });
  c.currentActionPoints -= 1;
  return ok(['move']);
}

// ─── attack ───────────────────────────────────────────────────────────
export function applyAttack(state: GameState, userId: string, d: Direction): ActionResult {
  const pre = preActionChecks(state, userId);
  if (pre) return pre;
  const c = currentChampion(state);
  if (!c) return err('NO_CHAMPION', 'no current champion');
  if (hasEffect(c, 'Disarm')) return err('DISARMED', 'cannot attack while disarmed');
  if (c.currentActionPoints < 2) return err('NOT_ENOUGH_AP', 'need 2 action points to attack');
  let cx = c.x;
  let cy = c.y;
  for (let step = 0; step < c.attackRange; step++) {
    const next = applyDirection(cx, cy, d);
    cx = next.x; cy = next.y;
    if (cx < 0 || cx >= BOARD_HEIGHT || cy < 0 || cy >= BOARD_WIDTH) return ok(['attackMiss']);
    const cell = cellAt(state, cx, cy);
    if (cell === null) continue;
    if (cell.type === 'cover') {
      cell.hp -= c.attackDamage;
      c.currentActionPoints -= 2;
      if (cell.hp <= 0) setCell(state, cx, cy, null);
      return ok(['attackCover']);
    }
    const target = state.champions[cell.id];
    if (!target) continue;
    if (sameTeam(state, c, target)) continue; // pass through friendly
    // Dodge roll
    if (hasEffect(target, 'Dodge')) {
      const r = Math.floor(rand(state) * 100) + 1;
      if (r <= 50) {
        c.currentActionPoints -= 2;
        return ok(['attackDodged']);
      }
    }
    // Shield consumes the hit (matches Java: Shield short-circuits before type bonus)
    if (hasEffect(target, 'Shield')) {
      const idx = target.appliedEffects.findIndex(e => e.name === 'Shield');
      if (idx >= 0) {
        const e = target.appliedEffects[idx]!;
        target.appliedEffects.splice(idx, 1);
        removeEffect(target, e);
        c.currentActionPoints -= 2;
        return ok(['attackShielded']);
      }
    }
    let damage = c.attackDamage;
    const crossType =
      (c.kind === 'H' && target.kind !== 'H') ||
      (c.kind === 'V' && target.kind !== 'V') ||
      (c.kind === 'A' && target.kind !== 'A');
    if (crossType) damage = Math.trunc(damage * 1.5);
    setHp(target, target.currentHP - damage);
    c.currentActionPoints -= 2;
    cleanup(state, [target]);
    checkAndFinish(state);
    return ok(['attack']);
  }
  return ok(['attackMiss']);
}

function setHp(t: ChampionInstance, hp: number): void {
  if (hp <= 0) {
    t.currentHP = 0;
    t.condition = 'KNOCKEDOUT';
  } else if (hp > t.maxHP) {
    t.currentHP = t.maxHP;
  } else {
    t.currentHP = hp;
  }
}

// ─── castAbility ───────────────────────────────────────────────────────
export type AbilityTarget =
  | { kind: 'self' }
  | { kind: 'direction'; dir: Direction }
  | { kind: 'cell'; x: number; y: number }
  | { kind: 'team' } // TEAMTARGET / SURROUND don't take a manual target
  | { kind: 'surround' };

function validateCast(c: ChampionInstance, a: AbilityInstance): ActionResult | null {
  if (c.mana < a.manaCost) return err('NOT_ENOUGH_MANA', `need ${a.manaCost} mana`);
  if (c.currentActionPoints < a.requiredActionPoints) return err('NOT_ENOUGH_AP', `need ${a.requiredActionPoints} AP`);
  if (hasEffect(c, 'Silence')) return err('SILENCED', 'silenced — cannot cast');
  if (a.currentCooldown > 0) return err('COOLDOWN', 'ability on cooldown');
  return null;
}

function gatherTargets(
  state: GameState,
  c: ChampionInstance,
  a: AbilityInstance,
  target: AbilityTarget,
): { targets: (ChampionInstance | { cover: { cx: number; cy: number } })[]; error?: ActionResult } {
  const results: (ChampionInstance | { cover: { cx: number; cy: number } })[] = [];
  const isDamaging = a.kind === 'DMG';
  const isHealing = a.kind === 'HEL';
  const isCC = a.kind === 'CC';
  // For CC we need BUFF vs DEBUFF behavior (see Game.java:360-368).
  const isDebuff = isCC && (a.effectName !== null) && ['Stun', 'Root', 'Silence', 'Shock', 'Disarm'].includes(a.effectName);
  const isBuff = isCC && !isDebuff;

  const addIfEnemy = (x: number, y: number): void => {
    const cell = cellAt(state, x, y);
    if (!cell) return;
    if (cell.type === 'cover') {
      if (isDamaging) results.push({ cover: { cx: x, cy: y } });
      return;
    }
    const t = state.champions[cell.id];
    if (!t) return;
    const friendly = sameTeam(state, c, t);
    if (isDamaging && !friendly) {
      if (hasEffect(t, 'Shield')) {
        // Shield consumes the ability hit — matches Game.prepareTargetsFromPoints.
        const idx = t.appliedEffects.findIndex(e => e.name === 'Shield');
        if (idx >= 0) {
          const e = t.appliedEffects[idx]!;
          t.appliedEffects.splice(idx, 1);
          removeEffect(t, e);
        }
      } else {
        results.push(t);
      }
    } else if (isHealing && friendly) {
      results.push(t);
    } else if (isCC && isDebuff && !friendly) {
      results.push(t);
    } else if (isCC && isBuff && friendly) {
      results.push(t);
    }
  };

  if (a.castArea === 'SELFTARGET') {
    results.push(c);
    return { targets: results };
  }

  if (a.castArea === 'TEAMTARGET') {
    // Team/enemy team within castRange of caster (Manhattan distance).
    const wantFriendly = isHealing || isBuff;
    const targetTeam = (wantFriendly ? state.players[c.ownerIndex] : state.players[otherIndex(c.ownerIndex)]).teamIds;
    for (const id of targetTeam) {
      const t = state.champions[id];
      if (!t) continue;
      const dist = Math.abs(c.x - t.x) + Math.abs(c.y - t.y);
      if (dist <= a.castRange) results.push(t);
    }
    return { targets: results };
  }

  if (a.castArea === 'SURROUND') {
    for (const [dx, dy] of [[1, 0], [-1, 0], [0, 1], [0, -1], [1, -1], [1, 1], [-1, -1], [-1, 1]] as const) {
      addIfEnemy(c.x + dx, c.y + dy);
    }
    return { targets: results };
  }

  if (a.castArea === 'DIRECTIONAL') {
    if (target.kind !== 'direction') {
      return { targets: [], error: err('INVALID_TARGET', 'directional ability needs a direction') };
    }
    let cx = c.x; let cy = c.y;
    for (let i = 0; i < a.castRange; i++) {
      const next = applyDirection(cx, cy, target.dir);
      cx = next.x; cy = next.y;
      if (cx < 0 || cx >= BOARD_HEIGHT || cy < 0 || cy >= BOARD_WIDTH) break;
      addIfEnemy(cx, cy);
    }
    return { targets: results };
  }

  // SINGLETARGET
  if (target.kind !== 'cell') {
    return { targets: [], error: err('INVALID_TARGET', 'single-target ability needs a cell') };
  }
  const { x, y } = target;
  if (x < 0 || x >= BOARD_HEIGHT || y < 0 || y >= BOARD_WIDTH) {
    return { targets: [], error: err('OUT_OF_BOUNDS', 'target out of board') };
  }
  const dist = Math.abs(c.x - x) + Math.abs(c.y - y);
  if (dist > a.castRange) {
    return { targets: [], error: err('INVALID_TARGET', 'target out of cast range') };
  }
  const cell = cellAt(state, x, y);
  if (!cell) return { targets: [], error: err('INVALID_TARGET', 'empty cell') };
  if (cell.type === 'cover') {
    if (!isDamaging) return { targets: [], error: err('INVALID_TARGET', 'covers only take damage') };
    results.push({ cover: { cx: x, cy: y } });
    return { targets: results };
  }
  const t = state.champions[cell.id];
  if (!t) return { targets: [], error: err('INVALID_TARGET', 'empty cell') };
  const friendly = sameTeam(state, c, t);
  if (friendly && isDamaging) return { targets: [], error: err('INVALID_TARGET', 'friendly fire forbidden') };
  if (friendly && isCC && isDebuff) return { targets: [], error: err('INVALID_TARGET', 'no debuffing friends') };
  if (!friendly && isHealing) return { targets: [], error: err('INVALID_TARGET', 'no healing enemies') };
  if (!friendly && isCC && isBuff) return { targets: [], error: err('INVALID_TARGET', 'no buffing enemies') };
  if (isDamaging && hasEffect(t, 'Shield')) {
    const idx = t.appliedEffects.findIndex(e => e.name === 'Shield');
    if (idx >= 0) {
      const e = t.appliedEffects[idx]!;
      t.appliedEffects.splice(idx, 1);
      removeEffect(t, e);
    }
  } else {
    results.push(t);
  }
  return { targets: results };
}

export function applyCastAbility(
  state: GameState,
  userId: string,
  abilityName: string,
  target: AbilityTarget,
): ActionResult {
  const pre = preActionChecks(state, userId);
  if (pre) return pre;
  const c = currentChampion(state);
  if (!c) return err('NO_CHAMPION', 'no current champion');
  const a = c.abilities.find(x => x.name === abilityName);
  if (!a) return err('UNKNOWN_ABILITY', `no such ability: ${abilityName}`);
  const v = validateCast(c, a);
  if (v) return v;
  const { targets, error } = gatherTargets(state, c, a, target);
  if (error) return error;
  const championsTouched: ChampionInstance[] = [];
  for (const item of targets) {
    if ('cover' in item) {
      const { cx, cy } = item.cover;
      const cell = cellAt(state, cx, cy);
      if (cell && cell.type === 'cover') {
        cell.hp -= a.damageAmount;
        if (cell.hp <= 0) setCell(state, cx, cy, null);
      }
    } else {
      if (a.kind === 'DMG') setHp(item, item.currentHP - a.damageAmount);
      else if (a.kind === 'HEL') setHp(item, item.currentHP + a.healAmount);
      else if (a.kind === 'CC' && a.effectName) {
        const e = makeEffect(a.effectName, a.effectDuration);
        item.appliedEffects.push(e);
        applyEffect(item, e);
      }
      championsTouched.push(item);
    }
  }
  c.mana -= a.manaCost;
  c.currentActionPoints -= a.requiredActionPoints;
  a.currentCooldown = a.baseCooldown;
  cleanup(state, championsTouched);
  checkAndFinish(state);
  return ok([`cast:${a.name}`]);
}

// ─── leader ability ────────────────────────────────────────────────────
export function applyLeaderAbility(state: GameState, userId: string): ActionResult {
  const pre = preActionChecks(state, userId);
  if (pre) return pre;
  const c = currentChampion(state);
  if (!c) return err('NO_CHAMPION', 'no current champion');
  const p = state.players[c.ownerIndex];
  if (p.leaderId !== c.id) return err('LEADER_NOT_CURRENT', 'current champion is not your leader');
  const used = c.ownerIndex === 0 ? state.firstLeaderAbilityUsed : state.secondLeaderAbilityUsed;
  if (used) return err('LEADER_ALREADY_USED', 'leader ability already used');

  if (c.kind === 'H') {
    // Hero: remove all debuffs + apply Embrace(2) to self and teammates.
    const team = p.teamIds.map(id => state.champions[id]).filter(Boolean) as ChampionInstance[];
    for (const t of team) {
      for (let i = 0; i < t.appliedEffects.length; ) {
        const e = t.appliedEffects[i]!;
        if (e.type === 'DEBUFF') {
          t.appliedEffects.splice(i, 1);
          removeEffect(t, e);
        } else {
          i += 1;
        }
      }
      const em = makeEffect('Embrace', 2);
      t.appliedEffects.push(em);
      applyEffect(t, em);
    }
  } else if (c.kind === 'A') {
    // AntiHero: Stun(2) every non-leader on both teams.
    for (const player of state.players) {
      for (const id of player.teamIds) {
        if (id === player.leaderId) continue;
        const t = state.champions[id];
        if (!t) continue;
        const s = makeEffect('Stun', 2);
        t.appliedEffects.push(s);
        applyEffect(t, s);
      }
    }
  } else {
    // Villain: enemies at <30% HP die instantly.
    const enemy = state.players[otherIndex(c.ownerIndex)];
    const dead: ChampionInstance[] = [];
    for (const id of enemy.teamIds) {
      const t = state.champions[id];
      if (!t) continue;
      if (t.currentHP < 0.3 * t.maxHP) {
        setHp(t, 0);
        dead.push(t);
      }
    }
    cleanup(state, dead);
  }

  if (c.ownerIndex === 0) state.firstLeaderAbilityUsed = true;
  else state.secondLeaderAbilityUsed = true;
  checkAndFinish(state);
  return ok(['leaderAbility']);
}

// ─── end turn ──────────────────────────────────────────────────────────
export function applyEndTurn(state: GameState, userId: string): ActionResult {
  const pre = preActionChecks(state, userId);
  if (pre) return pre;
  // Remove current champion from turn order.
  state.turnOrder.shift();
  if (state.turnOrder.length === 0) refillTurnOrder(state);
  // Skip stunned champions at the head, applying their timers as we go.
  while (state.turnOrder.length > 0) {
    const headId = state.turnOrder[0]!;
    const head = state.champions[headId];
    if (!head) { state.turnOrder.shift(); continue; }
    if (!hasEffect(head, 'Stun')) break;
    tickEffects(head);
    tickCooldowns(head);
    state.turnOrder.shift();
    if (state.turnOrder.length === 0) refillTurnOrder(state);
  }
  const newCurrent = currentChampion(state);
  if (newCurrent) {
    tickEffects(newCurrent);
    tickCooldowns(newCurrent);
    newCurrent.currentActionPoints = newCurrent.maxActionPointsPerTurn;
  }
  checkAndFinish(state);
  return ok(['endTurn']);
}

function refillTurnOrder(state: GameState): void {
  const ids = [
    ...state.players[0].teamIds,
    ...state.players[1].teamIds,
  ];
  state.turnOrder = sortTurnOrder(ids, state.champions);
}

// Surrender — concede the match; the other player wins.
export function applySurrender(state: GameState, userId: string): ActionResult {
  if (state.phase !== 'active') return err('GAME_OVER', 'game is not active');
  const player = state.players.find(p => p.userId === userId);
  if (!player) return err('INVALID_TARGET', 'not a seated player');
  state.phase = 'ended';
  state.winnerIndex = otherIndex(player.index);
  state.endReason = 'surrendered';
  return ok(['surrender']);
}
