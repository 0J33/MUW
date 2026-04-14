import { v4 as uuidv4 } from 'uuid';
import type { GameState } from '@muw/shared';
import type { ChatMessage, LobbyPlayer } from '@muw/shared';

export interface Room {
  code: string;
  hostUserId: string;
  createdAt: number;
  lastActivity: number;
  // Lobby seats: up to 2. Index 0 is always the host.
  lobby: LobbyPlayer[];
  chat: ChatMessage[];
  // Once both players ready-up, game state is instantiated here.
  game: GameState | null;
  // Per-seat connected socket ids (same userId can reconnect with a new socket).
  seatSockets: Map<string, string>; // userId -> socketId
}

export const activeRooms = new Map<string, Room>();

const ROOM_CODE_CHARS = 'ABCDEFGHJKLMNPQRSTUVWXYZ23456789'; // no ambiguous chars

export function generateRoomCode(): string {
  let code = '';
  do {
    code = '';
    for (let i = 0; i < 6; i++) {
      code += ROOM_CODE_CHARS[Math.floor(Math.random() * ROOM_CODE_CHARS.length)];
    }
  } while (activeRooms.has(code));
  return code;
}

export function createRoom(hostUserId: string, hostUsername: string): Room {
  const code = generateRoomCode();
  const host: LobbyPlayer = {
    userId: hostUserId,
    username: hostUsername,
    connected: true,
    picks: [],
    leader: null,
    ready: false,
    seatIndex: 0,
  };
  const room: Room = {
    code,
    hostUserId,
    createdAt: Date.now(),
    lastActivity: Date.now(),
    lobby: [host],
    chat: [],
    game: null,
    seatSockets: new Map(),
  };
  activeRooms.set(code, room);
  return room;
}

export function joinRoom(code: string, userId: string, username: string): Room | null {
  const room = activeRooms.get(code);
  if (!room) return null;
  // Already seated? return the same room.
  const existing = room.lobby.find(p => p.userId === userId);
  if (existing) {
    existing.username = username;
    existing.connected = true;
    return room;
  }
  if (room.lobby.length >= 2) return null;
  room.lobby.push({
    userId,
    username,
    connected: true,
    picks: [],
    leader: null,
    ready: false,
    seatIndex: 1,
  });
  room.lastActivity = Date.now();
  return room;
}

export function findRoomByUserId(userId: string): Room | null {
  for (const room of activeRooms.values()) {
    if (room.lobby.some(p => p.userId === userId)) return room;
  }
  return null;
}

export function removeRoom(code: string): void {
  activeRooms.delete(code);
}

export function appendChat(room: Room, userId: string, username: string, text: string): ChatMessage {
  const msg: ChatMessage = { id: uuidv4(), userId, username, text: text.slice(0, 500), ts: Date.now() };
  room.chat.push(msg);
  if (room.chat.length > 200) room.chat.shift();
  return msg;
}
