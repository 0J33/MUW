import type { Server as SocketServer } from 'socket.io';
import { currentChampion, type GameStateView, type LobbyPlayer } from '@muw/shared';
import type { Room } from './rooms.js';

export function buildLobbyView(room: Room): LobbyPlayer[] {
  // Clone so downstream mutations can't corrupt the room.
  return room.lobby.map(p => ({ ...p, picks: [...p.picks] }));
}

export function buildGameView(room: Room, userId: string): GameStateView {
  const seat = room.lobby.find(p => p.userId === userId);
  const viewerIndex = seat?.seatIndex ?? null;
  if (!room.game) {
    return {
      code: room.code,
      phase: 'lobby',
      viewerIndex,
      currentChampionId: null,
      board: [],
      champions: [],
      players: buildLobbyView(room),
      turnOrder: [],
      firstLeaderAbilityUsed: false,
      secondLeaderAbilityUsed: false,
      winnerIndex: null,
      endReason: null,
      events: [],
    };
  }
  const g = room.game;
  const curr = currentChampion(g);
  return {
    code: room.code,
    phase: g.phase,
    viewerIndex,
    currentChampionId: curr ? curr.id : null,
    board: g.board.map(row => row.map(cell => (cell ? { ...cell } : null))),
    champions: Object.values(g.champions).map(c => ({
      ...c,
      abilities: c.abilities.map(a => ({ ...a })),
      appliedEffects: c.appliedEffects.map(e => ({ ...e })),
    })),
    players: buildLobbyView(room),
    turnOrder: [...g.turnOrder],
    firstLeaderAbilityUsed: g.firstLeaderAbilityUsed,
    secondLeaderAbilityUsed: g.secondLeaderAbilityUsed,
    winnerIndex: g.winnerIndex,
    endReason: g.endReason,
    events: g.events.slice(-50),
  };
}

export function broadcastRoom(io: SocketServer, room: Room): void {
  // Per-seat sanitized state. Stats are public (no hidden info in MUW), so
  // viewers see the same board — but viewerIndex differs so the client knows
  // which side it is.
  for (const seat of room.lobby) {
    const sid = room.seatSockets.get(seat.userId);
    if (!sid) continue;
    io.to(sid).emit('state', buildGameView(room, seat.userId));
  }
}
