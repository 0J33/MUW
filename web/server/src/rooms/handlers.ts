import type { Server as SocketServer, Socket } from 'socket.io';
import {
  applyAttack, applyCastAbility, applyEndTurn, applyLeaderAbility, applyMove, applySurrender,
  C2S, createGame, GameMoveSchema, GameAttackSchema, GameCastAbilitySchema,
  RoomCreateSchema, RoomJoinSchema, TeamPickSchema, TeamSetLeaderSchema,
  TeamReadySchema, ChatSendSchema, TeamUnpickSchema, TEAM_SIZE,
} from '@muw/shared';
import { abilityTemplates, championByName, championTemplates } from '../gameData.js';
import {
  activeRooms, appendChat, createRoom, findRoomByUserId, joinRoom, removeRoom, type Room,
} from './rooms.js';
import { broadcastRoom } from './broadcast.js';

interface SocketData {
  userId: string;
  username: string;
}

type MuwSocket = Socket & { data: SocketData };

function emitError(socket: MuwSocket, code: string, message: string): void {
  socket.emit('error', { code, message });
}

function getMySeat(room: Room, userId: string) {
  return room.lobby.find(p => p.userId === userId);
}

function allReady(room: Room): boolean {
  return room.lobby.length === 2
    && room.lobby.every(p => p.picks.length === TEAM_SIZE && p.leader !== null && p.ready);
}

function tryStartGame(room: Room): void {
  if (!allReady(room)) return;
  if (room.game) return;
  const [a, b] = room.lobby;
  room.game = createGame({
    seed: Math.floor(Math.random() * 0xffffffff),
    abilities: abilityTemplates,
    champions: championTemplates,
    players: [
      { userId: a!.userId, name: a!.username, teamNames: a!.picks, leaderName: a!.leader! },
      { userId: b!.userId, name: b!.username, teamNames: b!.picks, leaderName: b!.leader! },
    ],
  });
}

function bindSeat(room: Room, userId: string, socketId: string): void {
  room.seatSockets.set(userId, socketId);
  const seat = getMySeat(room, userId);
  if (seat) seat.connected = true;
}

function unbindSeat(room: Room, userId: string): void {
  room.seatSockets.delete(userId);
  const seat = getMySeat(room, userId);
  if (seat) seat.connected = false;
}

export function registerSocketHandlers(io: SocketServer): void {
  io.on('connection', (rawSocket) => {
    const socket = rawSocket as MuwSocket;
    const auth = (socket.handshake.auth ?? {}) as Partial<SocketData>;
    const userId = typeof auth.userId === 'string' ? auth.userId : null;
    const username = typeof auth.username === 'string' && auth.username.trim() ? auth.username.trim().slice(0, 24) : 'Guest';
    if (!userId) { socket.disconnect(true); return; }
    socket.data = { userId, username };

    // Rejoin any room this user was seated in.
    const existing = findRoomByUserId(userId);
    if (existing) {
      bindSeat(existing, userId, socket.id);
      socket.join(`room:${existing.code}`);
      broadcastRoom(io, existing);
    }

    socket.on(C2S.roomCreate, (raw, ack?: (r: { ok: true; code: string } | { ok: false; error: string }) => void) => {
      const parse = RoomCreateSchema.safeParse(raw);
      if (!parse.success) { ack?.({ ok: false, error: 'bad payload' }); return; }
      // Leave any current room first
      const prev = findRoomByUserId(userId);
      if (prev) {
        unbindSeat(prev, userId);
        const idx = prev.lobby.findIndex(p => p.userId === userId);
        if (idx >= 0) prev.lobby.splice(idx, 1);
        if (prev.lobby.length === 0) removeRoom(prev.code);
      }
      const room = createRoom(userId, username);
      bindSeat(room, userId, socket.id);
      socket.join(`room:${room.code}`);
      ack?.({ ok: true, code: room.code });
      broadcastRoom(io, room);
    });

    socket.on(C2S.roomJoin, (raw, ack?: (r: { ok: true; code: string } | { ok: false; error: string }) => void) => {
      const parse = RoomJoinSchema.safeParse(raw);
      if (!parse.success) { ack?.({ ok: false, error: 'bad payload' }); return; }
      const code = parse.data.code.toUpperCase();
      const room = joinRoom(code, userId, username);
      if (!room) { ack?.({ ok: false, error: 'room full or not found' }); return; }
      bindSeat(room, userId, socket.id);
      socket.join(`room:${room.code}`);
      ack?.({ ok: true, code: room.code });
      broadcastRoom(io, room);
    });

    socket.on(C2S.roomLeave, () => {
      const room = findRoomByUserId(userId);
      if (!room) return;
      unbindSeat(room, userId);
      const idx = room.lobby.findIndex(p => p.userId === userId);
      if (idx >= 0) room.lobby.splice(idx, 1);
      socket.leave(`room:${room.code}`);
      if (room.lobby.length === 0) { removeRoom(room.code); return; }
      broadcastRoom(io, room);
    });

    socket.on(C2S.teamPick, (raw) => {
      const parse = TeamPickSchema.safeParse(raw);
      if (!parse.success) { emitError(socket, 'BAD_PAYLOAD', 'bad payload'); return; }
      const room = findRoomByUserId(userId); if (!room || room.game) return;
      const seat = getMySeat(room, userId); if (!seat) return;
      const name = parse.data.championName;
      if (!championByName(name)) { emitError(socket, 'UNKNOWN', 'unknown champion'); return; }
      // Can't pick the same champion twice across both teams (like a draft).
      if (seat.picks.includes(name)) return;
      if (room.lobby.some(p => p.userId !== userId && p.picks.includes(name))) {
        emitError(socket, 'TAKEN', 'champion already picked by opponent'); return;
      }
      if (seat.picks.length >= TEAM_SIZE) return;
      seat.picks.push(name);
      // No auto-leader. The player must explicitly choose one before they can
      // ready up — `canReady` on the client requires a non-null leader.
      broadcastRoom(io, room);
    });

    socket.on(C2S.teamUnpick, (raw) => {
      const parse = TeamUnpickSchema.safeParse(raw);
      if (!parse.success) return;
      const room = findRoomByUserId(userId); if (!room || room.game) return;
      const seat = getMySeat(room, userId); if (!seat) return;
      const i = seat.picks.indexOf(parse.data.championName);
      if (i >= 0) seat.picks.splice(i, 1);
      if (seat.leader && !seat.picks.includes(seat.leader)) seat.leader = seat.picks[0] ?? null;
      seat.ready = false;
      broadcastRoom(io, room);
    });

    socket.on(C2S.teamSetLeader, (raw) => {
      const parse = TeamSetLeaderSchema.safeParse(raw);
      if (!parse.success) return;
      const room = findRoomByUserId(userId); if (!room || room.game) return;
      const seat = getMySeat(room, userId); if (!seat) return;
      if (!seat.picks.includes(parse.data.championName)) return;
      seat.leader = parse.data.championName;
      broadcastRoom(io, room);
    });

    socket.on(C2S.teamReady, (raw) => {
      const parse = TeamReadySchema.safeParse(raw);
      if (!parse.success) return;
      const room = findRoomByUserId(userId); if (!room || room.game) return;
      const seat = getMySeat(room, userId); if (!seat) return;
      if (parse.data.ready && (seat.picks.length !== TEAM_SIZE || !seat.leader)) return;
      seat.ready = parse.data.ready;
      tryStartGame(room);
      broadcastRoom(io, room);
    });

    socket.on(C2S.gameMove, (raw) => {
      const parse = GameMoveSchema.safeParse(raw);
      if (!parse.success) return;
      const room = findRoomByUserId(userId); if (!room?.game) return;
      const res = applyMove(room.game, userId, parse.data.dir);
      if (!res.ok) emitError(socket, res.code, res.error);
      broadcastRoom(io, room);
    });

    socket.on(C2S.gameAttack, (raw) => {
      const parse = GameAttackSchema.safeParse(raw);
      if (!parse.success) return;
      const room = findRoomByUserId(userId); if (!room?.game) return;
      const res = applyAttack(room.game, userId, parse.data.dir);
      if (!res.ok) emitError(socket, res.code, res.error);
      broadcastRoom(io, room);
    });

    socket.on(C2S.gameCastAbility, (raw) => {
      const parse = GameCastAbilitySchema.safeParse(raw);
      if (!parse.success) { emitError(socket, 'BAD_PAYLOAD', 'bad payload'); return; }
      const room = findRoomByUserId(userId); if (!room?.game) return;
      const res = applyCastAbility(room.game, userId, parse.data.abilityName, parse.data.target);
      if (!res.ok) emitError(socket, res.code, res.error);
      broadcastRoom(io, room);
    });

    socket.on(C2S.gameLeader, () => {
      const room = findRoomByUserId(userId); if (!room?.game) return;
      const res = applyLeaderAbility(room.game, userId);
      if (!res.ok) emitError(socket, res.code, res.error);
      broadcastRoom(io, room);
    });

    socket.on(C2S.gameEndTurn, () => {
      const room = findRoomByUserId(userId); if (!room?.game) return;
      const res = applyEndTurn(room.game, userId);
      if (!res.ok) emitError(socket, res.code, res.error);
      broadcastRoom(io, room);
    });

    socket.on(C2S.gameSurrender, () => {
      const room = findRoomByUserId(userId); if (!room?.game) return;
      const res = applySurrender(room.game, userId);
      if (!res.ok) emitError(socket, res.code, res.error);
      broadcastRoom(io, room);
    });

    socket.on(C2S.chatSend, (raw) => {
      const parse = ChatSendSchema.safeParse(raw);
      if (!parse.success) return;
      const room = findRoomByUserId(userId); if (!room) return;
      const msg = appendChat(room, userId, username, parse.data.text);
      io.to(`room:${room.code}`).emit('chat:message', msg);
    });

    socket.on('disconnect', () => {
      const room = findRoomByUserId(userId);
      if (!room) return;
      unbindSeat(room, userId);
      // Don't delete seat on disconnect — allow reconnection. If the lobby is
      // empty (both disconnected) and no game in progress, room TTL cleans up.
      broadcastRoom(io, room);
    });
  });
}

// Sweep idle rooms every 10 minutes. An inactive lobby (no game, no one
// connected) older than 1 hour is garbage-collected.
export function startRoomReaper(): void {
  setInterval(() => {
    const now = Date.now();
    for (const [code, room] of activeRooms.entries()) {
      const anyConnected = room.lobby.some(p => p.connected);
      const ageMs = now - room.lastActivity;
      if (!anyConnected && !room.game && ageMs > 60 * 60 * 1000) {
        removeRoom(code);
      }
    }
  }, 10 * 60 * 1000);
}
