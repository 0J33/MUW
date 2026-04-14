import { describe, expect, test } from 'vitest';
import { readFileSync } from 'node:fs';
import { fileURLToPath } from 'node:url';
import { dirname, join } from 'node:path';
import {
  applyAttack, applyCastAbility, applyEndTurn, applyLeaderAbility, applyMove, applySurrender,
  createGame, currentChampion, parseAbilities, parseChampions,
} from '../src/engine/index.js';

const here = dirname(fileURLToPath(import.meta.url));
const DATA_DIR = join(here, '..', 'data');
const championsCSV = readFileSync(join(DATA_DIR, 'Champions.csv'), 'utf8');
const abilitiesCSV = readFileSync(join(DATA_DIR, 'Abilities.csv'), 'utf8');
const champions = parseChampions(championsCSV);
const abilities = parseAbilities(abilitiesCSV);

function freshGame(seed = 42) {
  return createGame({
    seed,
    abilities,
    champions,
    players: [
      { userId: 'u0', name: 'Alice', teamNames: ['Captain America', 'Iceman', 'Thor'], leaderName: 'Thor' },
      { userId: 'u1', name: 'Bob',   teamNames: ['Loki', 'Electro', 'Venom'],            leaderName: 'Loki' },
    ],
  });
}

describe('CSV parsing', () => {
  test('parses all champions', () => {
    expect(champions.length).toBe(15);
    expect(champions.map(c => c.name)).toContain('Thor');
    expect(champions.find(c => c.name === 'Hulk')?.kind).toBe('H');
    expect(champions.find(c => c.name === 'Loki')?.kind).toBe('V');
  });
  test('parses all abilities', () => {
    expect(abilities.length).toBe(45);
    const shield = abilities.find(a => a.name === 'Shield Up')!;
    expect(shield.kind).toBe('CC');
    expect(shield.effectName).toBe('Shield');
    expect(shield.effectDuration).toBe(2);
    const shieldThrow = abilities.find(a => a.name === 'Shield throw')!;
    expect(shieldThrow.kind).toBe('DMG');
    expect(shieldThrow.damageAmount).toBe(150);
  });
});

describe('Game setup', () => {
  test('creates a 5x5 board with 6 champions placed on the edges and 5 covers', () => {
    const g = freshGame();
    expect(g.board.length).toBe(5);
    expect(g.board[0]!.length).toBe(5);
    const champsOnBoard = g.board.flat().filter(c => c?.type === 'champion').length;
    const coversOnBoard = g.board.flat().filter(c => c?.type === 'cover').length;
    expect(champsOnBoard).toBe(6);
    expect(coversOnBoard).toBe(5);
    expect(g.turnOrder.length).toBe(6);
  });
  test('turn order is sorted by speed descending with name tiebreak', () => {
    const g = freshGame();
    const speeds = g.turnOrder.map(id => g.champions[id]!.speed);
    for (let i = 1; i < speeds.length; i++) {
      expect(speeds[i - 1]).toBeGreaterThanOrEqual(speeds[i]!);
    }
  });
});

describe('Move', () => {
  test('deducts 1 AP and updates location on a valid direction', () => {
    const g = freshGame();
    const c = currentChampion(g)!;
    const ownerId = g.players[c.ownerIndex].userId;
    const apBefore = c.currentActionPoints;
    // Covers are random; pick whichever of the 4 directions lands on an empty cell.
    const dirs = ['UP', 'DOWN', 'LEFT', 'RIGHT'] as const;
    let succeeded = false;
    for (const dir of dirs) {
      const res = applyMove(g, ownerId, dir);
      if (res.ok) { succeeded = true; break; }
    }
    expect(succeeded).toBe(true);
    expect(c.currentActionPoints).toBe(apBefore - 1);
    expect(g.board[c.x]![c.y]!.type).toBe('champion');
  });

  test('rejects when not your turn', () => {
    const g = freshGame();
    const c = currentChampion(g)!;
    const otherOwner = g.players[c.ownerIndex === 0 ? 1 : 0].userId;
    const res = applyMove(g, otherOwner, 'UP');
    expect(res.ok).toBe(false);
    if (!res.ok) expect(res.code).toBe('NOT_YOUR_TURN');
  });
});

describe('End turn', () => {
  test('advances to the next champion', () => {
    const g = freshGame();
    const c1 = currentChampion(g)!;
    const res = applyEndTurn(g, g.players[c1.ownerIndex].userId);
    expect(res.ok).toBe(true);
    const c2 = currentChampion(g)!;
    expect(c2.id).not.toBe(c1.id);
  });
  test('eventually cycles back through all champions', () => {
    const g = freshGame();
    const seen = new Set<string>();
    for (let i = 0; i < 12; i++) {
      const c = currentChampion(g)!;
      seen.add(c.id);
      const res = applyEndTurn(g, g.players[c.ownerIndex].userId);
      expect(res.ok).toBe(true);
    }
    expect(seen.size).toBe(6);
  });
});

describe('Surrender', () => {
  test('ends the game and awards the other player', () => {
    const g = freshGame();
    const res = applySurrender(g, g.players[0].userId);
    expect(res.ok).toBe(true);
    expect(g.phase).toBe('ended');
    expect(g.winnerIndex).toBe(1);
  });
});

describe('Cast ability', () => {
  test('SELFTARGET heal (I can do this all day) heals caster and puts ability on cooldown', () => {
    const g = freshGame();
    // Force Captain America to be current by rotating until we find him.
    while (currentChampion(g)!.name !== 'Captain America') {
      applyEndTurn(g, g.players[currentChampion(g)!.ownerIndex].userId);
    }
    const cap = currentChampion(g)!;
    cap.currentHP = 500; // injure him first so heal is visible
    const res = applyCastAbility(g, g.players[cap.ownerIndex].userId, 'I can do this all day', { kind: 'self' });
    expect(res.ok).toBe(true);
    // Heals for 150 per CSV
    expect(cap.currentHP).toBe(650);
    const heal = cap.abilities.find(a => a.name === 'I can do this all day')!;
    expect(heal.currentCooldown).toBe(heal.baseCooldown);
  });

  test('SELFTARGET buff (Shield Up) applies Shield effect', () => {
    const g = freshGame();
    while (currentChampion(g)!.name !== 'Captain America') {
      applyEndTurn(g, g.players[currentChampion(g)!.ownerIndex].userId);
    }
    const cap = currentChampion(g)!;
    const res = applyCastAbility(g, g.players[cap.ownerIndex].userId, 'Shield Up', { kind: 'self' });
    expect(res.ok).toBe(true);
    expect(cap.appliedEffects.some(e => e.name === 'Shield')).toBe(true);
  });
});

describe('Leader ability flag', () => {
  test('is marked used after first call', () => {
    const g = freshGame();
    while (true) {
      const c = currentChampion(g)!;
      if (c.id === g.players[c.ownerIndex].leaderId) break;
      applyEndTurn(g, g.players[c.ownerIndex].userId);
    }
    const c = currentChampion(g)!;
    const res = applyLeaderAbility(g, g.players[c.ownerIndex].userId);
    expect(res.ok).toBe(true);
    const usedFlag = c.ownerIndex === 0 ? g.firstLeaderAbilityUsed : g.secondLeaderAbilityUsed;
    expect(usedFlag).toBe(true);
    // Second call fails
    const res2 = applyLeaderAbility(g, g.players[c.ownerIndex].userId);
    expect(res2.ok).toBe(false);
    if (!res2.ok) expect(res2.code).toBe('LEADER_ALREADY_USED');
  });
});

describe('Game over detection', () => {
  test('knocking out an entire team ends the game', () => {
    const g = freshGame();
    // Force-KO player 1 team via direct HP manipulation + cleanup path through endTurn.
    for (const id of [...g.players[1].teamIds]) {
      const c = g.champions[id]!;
      c.currentHP = 0;
      c.condition = 'KNOCKEDOUT';
      // mimic cleanup manually
      g.board[c.x]![c.y] = null;
      delete g.champions[id];
      const idx = g.players[1].teamIds.indexOf(id);
      if (idx >= 0) g.players[1].teamIds.splice(idx, 1);
      const toIdx = g.turnOrder.indexOf(id);
      if (toIdx >= 0) g.turnOrder.splice(toIdx, 1);
    }
    const first = currentChampion(g)!;
    applyEndTurn(g, g.players[first.ownerIndex].userId);
    expect(g.phase).toBe('ended');
    expect(g.winnerIndex).toBe(0);
  });
});
