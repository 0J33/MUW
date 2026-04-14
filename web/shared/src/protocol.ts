import { z } from 'zod';
import type { Cell, ChampionInstance, Direction, GamePhase } from './engine/state.js';

// ─── Incoming events (client → server) ────────────────────────────────────
export const RoomCreateSchema = z.object({});
export const RoomJoinSchema = z.object({ code: z.string().min(4).max(8) });
export const RoomLeaveSchema = z.object({});
export const TeamPickSchema = z.object({ championName: z.string().min(1) });
export const TeamUnpickSchema = z.object({ championName: z.string().min(1) });
export const TeamSetLeaderSchema = z.object({ championName: z.string().min(1) });
export const TeamReadySchema = z.object({ ready: z.boolean() });
export const GameMoveSchema = z.object({ dir: z.enum(['UP', 'DOWN', 'LEFT', 'RIGHT']) });
export const GameAttackSchema = z.object({ dir: z.enum(['UP', 'DOWN', 'LEFT', 'RIGHT']) });
export const GameCastAbilitySchema = z.object({
  abilityName: z.string().min(1),
  target: z.discriminatedUnion('kind', [
    z.object({ kind: z.literal('self') }),
    z.object({ kind: z.literal('direction'), dir: z.enum(['UP', 'DOWN', 'LEFT', 'RIGHT']) }),
    z.object({ kind: z.literal('cell'), x: z.number().int().min(0).max(4), y: z.number().int().min(0).max(4) }),
    z.object({ kind: z.literal('team') }),
    z.object({ kind: z.literal('surround') }),
  ]),
});
export const GameLeaderSchema = z.object({});
export const GameEndTurnSchema = z.object({});
export const GameSurrenderSchema = z.object({});
export const ChatSendSchema = z.object({ text: z.string().min(1).max(500) });
export const SessionHandshakeSchema = z.object({ username: z.string().min(1).max(24) });

// ─── Event names ──────────────────────────────────────────────────────────
export const C2S = {
  roomCreate: 'room:create',
  roomJoin: 'room:join',
  roomLeave: 'room:leave',
  teamPick: 'team:pick',
  teamUnpick: 'team:unpick',
  teamSetLeader: 'team:setLeader',
  teamReady: 'team:ready',
  gameMove: 'game:move',
  gameAttack: 'game:attack',
  gameCastAbility: 'game:castAbility',
  gameLeader: 'game:leader',
  gameEndTurn: 'game:endTurn',
  gameSurrender: 'game:surrender',
  chatSend: 'chat:send',
  sessionHandshake: 'session:handshake',
} as const;

export const S2C = {
  state: 'state',
  roomCreated: 'room:created',
  roomJoined: 'room:joined',
  roomLeft: 'room:left',
  actionEvent: 'actionEvent',
  chatMessage: 'chat:message',
  matchEnd: 'matchEnd',
  error: 'error',
} as const;

// ─── Outgoing payloads ────────────────────────────────────────────────────
export interface ChatMessage {
  id: string;
  userId: string;
  username: string;
  text: string;
  ts: number;
}

export type Lobby = {
  code: string;
  hostUserId: string;
  phase: 'lobby' | 'teamSelect' | 'active' | 'ended';
  players: LobbyPlayer[];
  chat: ChatMessage[];
  createdAt: number;
};

export interface LobbyPlayer {
  userId: string;
  username: string;
  connected: boolean;
  picks: string[];        // champion names
  leader: string | null;  // champion name
  ready: boolean;
  seatIndex: 0 | 1 | null;
}

// GameStateView is the sanitized snapshot sent to each client per tick.
export interface GameStateView {
  code: string;
  phase: GamePhase;
  viewerIndex: 0 | 1 | null;
  currentChampionId: string | null;
  board: Cell[][];
  champions: ChampionInstance[];
  players: LobbyPlayer[];
  turnOrder: string[];
  firstLeaderAbilityUsed: boolean;
  secondLeaderAbilityUsed: boolean;
  winnerIndex: 0 | 1 | null;
  endReason: 'killed' | 'surrendered' | null;
  events: { ts: number; type: string; data: Record<string, unknown> }[];
  lastSfx?: string[];
}

export type DirectionT = Direction;
