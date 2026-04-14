import { newAbilityInstance, type AbilityTemplate, type ChampionTemplate } from './csv.js';
import { sortTurnOrder } from './priorityQueue.js';
import { rand, randInt } from './rng.js';
import {
  BOARD_HEIGHT,
  BOARD_WIDTH,
  TEAM_SIZE,
  type Cell,
  type ChampionInstance,
  type GameState,
  type PlayerState,
} from './state.js';

export interface CreateGameInput {
  seed: number;
  abilities: AbilityTemplate[];
  champions: ChampionTemplate[];
  players: [
    { userId: string; name: string; teamNames: string[]; leaderName: string },
    { userId: string; name: string; teamNames: string[]; leaderName: string },
  ];
}

let championIdCounter = 0;
export function generateChampionId(name: string, ownerIndex: 0 | 1): string {
  championIdCounter += 1;
  return `c_${ownerIndex}_${name.replace(/\s+/g, '_')}_${championIdCounter}`;
}

function instantiateChampion(
  template: ChampionTemplate,
  abilityTemplates: AbilityTemplate[],
  ownerIndex: 0 | 1,
): ChampionInstance {
  const abilities = template.abilityNames.map(n => {
    const t = abilityTemplates.find(a => a.name === n);
    if (!t) throw new Error(`Ability not found: ${n}`);
    return newAbilityInstance(t);
  });
  return {
    id: generateChampionId(template.name, ownerIndex),
    ownerIndex,
    kind: template.kind,
    name: template.name,
    maxHP: template.maxHP,
    currentHP: template.maxHP,
    mana: template.maxMana,
    maxActionPointsPerTurn: template.maxActionPointsPerTurn,
    currentActionPoints: template.maxActionPointsPerTurn,
    attackRange: template.attackRange,
    attackDamage: template.attackDamage,
    speed: template.speed,
    abilities,
    appliedEffects: [],
    condition: 'ACTIVE',
    x: -1,
    y: -1,
  };
}

function emptyBoard(): Cell[][] {
  return Array.from({ length: BOARD_WIDTH }, () => Array<Cell>(BOARD_HEIGHT).fill(null));
}

function placeChampions(state: GameState): void {
  for (const player of state.players) {
    const teamIds = player.teamIds;
    const col = player.index === 0 ? 0 : BOARD_HEIGHT - 1;
    for (let i = 0; i < TEAM_SIZE; i++) {
      const id = teamIds[i]!;
      const y = i + 1; // rows 1,2,3 as in Game.java:206-218
      const c = state.champions[id]!;
      c.x = col;
      c.y = y;
      state.board[col]![y] = { type: 'champion', id };
    }
  }
}

function placeCovers(state: GameState): void {
  // Same constants as Game.placeCovers (Game.java:192-204): 5 covers with
  // x in [1, BOARDWIDTH-2] (i.e. 1..3) and y in [0, BOARDHEIGHT).
  let placed = 0;
  while (placed < 5) {
    const x = randInt(state, 1, BOARD_WIDTH - 1); // [1, 4) = 1..3 inclusive
    const y = randInt(state, 0, BOARD_HEIGHT);
    if (state.board[x]![y] === null) {
      const hp = Math.floor(rand(state) * 900) + 100; // [100, 1000)
      state.board[x]![y] = { type: 'cover', hp };
      placed += 1;
    }
  }
}

function prepareTurnOrder(state: GameState): void {
  const ids = [
    ...state.players[0].teamIds,
    ...state.players[1].teamIds,
  ];
  state.turnOrder = sortTurnOrder(ids, state.champions);
}

export function createGame(input: CreateGameInput): GameState {
  const [p0, p1] = input.players;
  const players: [PlayerState, PlayerState] = [
    { userId: p0.userId, name: p0.name, index: 0, teamIds: [], leaderId: null },
    { userId: p1.userId, name: p1.name, index: 1, teamIds: [], leaderId: null },
  ];
  const champions: Record<string, ChampionInstance> = {};

  const addTeam = (player: PlayerState, names: string[], leaderName: string): void => {
    for (const name of names) {
      const tmpl = input.champions.find(c => c.name === name);
      if (!tmpl) throw new Error(`Champion not found: ${name}`);
      const inst = instantiateChampion(tmpl, input.abilities, player.index);
      champions[inst.id] = inst;
      player.teamIds.push(inst.id);
      if (tmpl.name === leaderName) player.leaderId = inst.id;
    }
    if (!player.leaderId) throw new Error(`Leader ${leaderName} not in team for player ${player.name}`);
  };

  addTeam(players[0], p0.teamNames, p0.leaderName);
  addTeam(players[1], p1.teamNames, p1.leaderName);

  const state: GameState = {
    phase: 'active',
    board: emptyBoard(),
    champions,
    players,
    turnOrder: [],
    firstLeaderAbilityUsed: false,
    secondLeaderAbilityUsed: false,
    rngSeed: input.seed,
    rngState: input.seed >>> 0,
    winnerIndex: null,
    endReason: null,
    events: [],
  };

  placeChampions(state);
  placeCovers(state);
  prepareTurnOrder(state);

  return state;
}

export function currentChampion(state: GameState): ChampionInstance | null {
  const id = state.turnOrder[0];
  if (!id) return null;
  return state.champions[id] ?? null;
}

export function checkGameOver(state: GameState): 0 | 1 | null {
  const p0Alive = state.players[0].teamIds.some(id => state.champions[id] !== undefined);
  const p1Alive = state.players[1].teamIds.some(id => state.champions[id] !== undefined);
  if (!p0Alive) return 1;
  if (!p1Alive) return 0;
  return null;
}
