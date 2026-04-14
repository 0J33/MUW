// Pure data types for the MUW game state. No classes, no hidden state —
// actions are (state, payload) → state.

export type ChampionKind = 'H' | 'A' | 'V'; // Hero, AntiHero, Villain
export type AreaOfEffect = 'SINGLETARGET' | 'TEAMTARGET' | 'DIRECTIONAL' | 'SELFTARGET' | 'SURROUND';
export type Direction = 'UP' | 'DOWN' | 'LEFT' | 'RIGHT';
export type Condition = 'ACTIVE' | 'INACTIVE' | 'ROOTED' | 'KNOCKEDOUT';
export type EffectType = 'BUFF' | 'DEBUFF';
export type EffectName = 'Stun' | 'Shield' | 'Dodge' | 'Root' | 'Silence' | 'Shock' | 'PowerUp' | 'SpeedUp' | 'Embrace' | 'Disarm';
export type AbilityKind = 'DMG' | 'HEL' | 'CC';

export const BOARD_WIDTH = 5;
export const BOARD_HEIGHT = 5;
export const TEAM_SIZE = 3;

export interface AbilityInstance {
  kind: AbilityKind;
  name: string;
  manaCost: number;
  baseCooldown: number;
  currentCooldown: number;
  castRange: number;
  castArea: AreaOfEffect;
  requiredActionPoints: number;
  damageAmount: number; // DMG
  healAmount: number;   // HEL
  effectName: EffectName | null; // CC
  effectDuration: number;        // CC
}

export interface EffectInstance {
  name: EffectName;
  type: EffectType;
  duration: number;
}

export interface ChampionInstance {
  id: string;
  ownerIndex: 0 | 1;
  kind: ChampionKind;
  name: string;
  maxHP: number;
  currentHP: number;
  mana: number;
  maxActionPointsPerTurn: number;
  currentActionPoints: number;
  attackRange: number;
  attackDamage: number;
  speed: number;
  abilities: AbilityInstance[];
  appliedEffects: EffectInstance[];
  condition: Condition;
  x: number;
  y: number;
}

export interface CoverCell {
  type: 'cover';
  hp: number;
}

export interface ChampionCell {
  type: 'champion';
  id: string;
}

export type Cell = ChampionCell | CoverCell | null;

export interface PlayerState {
  userId: string;
  name: string;
  index: 0 | 1;
  teamIds: string[];
  leaderId: string | null;
}

export type GamePhase = 'lobby' | 'teamSelect' | 'active' | 'ended';

export interface GameEvent {
  ts: number;
  type: string;
  data: Record<string, unknown>;
}

export interface GameState {
  phase: GamePhase;
  board: Cell[][];
  champions: Record<string, ChampionInstance>;
  players: [PlayerState, PlayerState];
  turnOrder: string[]; // champion ids, index 0 is current
  firstLeaderAbilityUsed: boolean;
  secondLeaderAbilityUsed: boolean;
  rngSeed: number;
  rngState: number;
  winnerIndex: 0 | 1 | null;
  // Why the match ended. 'killed' = the losing team ran out of champions;
  // 'surrendered' = a player conceded. Used by the client to decide whether
  // to delay the victory screen for an animation beat.
  endReason: 'killed' | 'surrendered' | null;
  events: GameEvent[];
}

export function otherIndex(i: 0 | 1): 0 | 1 {
  return (1 - i) as 0 | 1;
}
