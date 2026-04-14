import type { ChampionInstance, Direction, GameState } from '../engine/state.js';
import { otherIndex } from '../engine/state.js';
import { currentChampion } from '../engine/setup.js';
import {
  applyAttack, applyCastAbility, applyEndTurn, applyLeaderAbility, applyMove,
  type AbilityTarget,
} from '../engine/moves.js';

// Heuristic one-ply bot: for each legal action, simulate it on a clone of
// state, score the resulting state from the bot's perspective, and pick the
// best. Deterministic given a state (modulo Dodge rolls inside simulation,
// which is fine — those are already resolved via the seeded RNG).

const DIRS: Direction[] = ['UP', 'DOWN', 'LEFT', 'RIGHT'];

export function botPickAction(state: GameState, userId: string): void {
  const me = currentChampion(state);
  if (!me) { applyEndTurn(state, userId); return; }
  const botIndex = me.ownerIndex;

  interface Candidate { score: number; commit: () => void }
  const cands: Candidate[] = [];

  // Try each of the 4 movement directions.
  for (const d of DIRS) {
    cands.push({
      score: scoreAfter(state, botIndex, (g) => { applyMove(g, userId, d); }),
      commit: () => { applyMove(state, userId, d); },
    });
  }
  // Try each of the 4 attack directions.
  for (const d of DIRS) {
    cands.push({
      score: scoreAfter(state, botIndex, (g) => { applyAttack(g, userId, d); }),
      commit: () => { applyAttack(state, userId, d); },
    });
  }
  // Try each ability, for each plausible target shape.
  for (const a of me.abilities) {
    const targets = plausibleTargets(state, me, a.castArea);
    for (const t of targets) {
      cands.push({
        score: scoreAfter(state, botIndex, (g) => { applyCastAbility(g, userId, a.name, t); }),
        commit: () => { applyCastAbility(state, userId, a.name, t); },
      });
    }
  }
  // Leader ability (once per match).
  const leaderUsed = botIndex === 0 ? state.firstLeaderAbilityUsed : state.secondLeaderAbilityUsed;
  const p = state.players[botIndex];
  if (!leaderUsed && p.leaderId === me.id) {
    cands.push({
      score: scoreAfter(state, botIndex, (g) => { applyLeaderAbility(g, userId); }) + 40, // bias: leader is valuable
      commit: () => { applyLeaderAbility(state, userId); },
    });
  }
  // End-turn as a fallback only — heavy penalty so the bot exhausts useful
  // actions first instead of passing.
  cands.push({
    score: scoreAfter(state, botIndex, (g) => { applyEndTurn(g, userId); }) - 200,
    commit: () => { applyEndTurn(state, userId); },
  });

  cands.sort((a, b) => b.score - a.score);
  const best = cands[0];
  if (best) best.commit();
}

function plausibleTargets(state: GameState, me: ChampionInstance, area: string): AbilityTarget[] {
  if (area === 'SELFTARGET') return [{ kind: 'self' }];
  if (area === 'TEAMTARGET') return [{ kind: 'team' }];
  if (area === 'SURROUND') return [{ kind: 'surround' }];
  if (area === 'DIRECTIONAL') return DIRS.map<AbilityTarget>(d => ({ kind: 'direction', dir: d }));
  // SINGLETARGET: every cell on the board (simulation will reject invalid).
  const out: AbilityTarget[] = [];
  for (let x = 0; x < 5; x++) {
    for (let y = 0; y < 5; y++) {
      if (x === me.x && y === me.y) continue;
      const dist = Math.abs(x - me.x) + Math.abs(y - me.y);
      if (dist <= 4) out.push({ kind: 'cell', x, y });
    }
  }
  return out;
}

function scoreAfter(state: GameState, botIndex: 0 | 1, mutate: (g: GameState) => void): number {
  const clone = structuredClone(state);
  try { mutate(clone); } catch { return -9999; }
  return scoreState(clone, botIndex);
}

function scoreState(s: GameState, botIndex: 0 | 1): number {
  if (s.phase === 'ended') {
    return s.winnerIndex === botIndex ? 100000 : -100000;
  }
  const enemy = otherIndex(botIndex);
  const myTeam = s.players[botIndex].teamIds.map(id => s.champions[id]).filter(Boolean) as ChampionInstance[];
  const enTeam = s.players[enemy].teamIds.map(id => s.champions[id]).filter(Boolean) as ChampionInstance[];
  let score = 0;
  for (const c of myTeam) {
    score += c.currentHP * 1.2;
    score += c.mana * 0.2;
    // AP is barely valued — having spare AP at end of turn is wasted anyway,
    // so the bot should rather spend it walking/attacking.
    score += c.currentActionPoints * 0.5;
    for (const e of c.appliedEffects) score += (e.type === 'BUFF' ? 8 : -12) * e.duration;
  }
  for (const c of enTeam) {
    score -= c.currentHP * 1.5;
    score -= c.mana * 0.2;
    for (const e of c.appliedEffects) score += (e.type === 'DEBUFF' ? 14 : -10) * e.duration;
  }
  // Champion deaths dominate everything else — losing 1 of 3 is catastrophic.
  score += (myTeam.length - enTeam.length) * 800;
  // Positioning: aggressively reward closing the gap. Each square closer is
  // worth more than spending an action point, so movement is preferred over
  // sitting at full AP. Bonus when within attack range.
  for (const me of myTeam) {
    let minDist = 99;
    for (const en of enTeam) {
      const d = Math.abs(me.x - en.x) + Math.abs(me.y - en.y);
      if (d < minDist) minDist = d;
    }
    if (minDist <= me.attackRange) score += 40;
    score -= minDist * 8;
  }
  return score;
}
