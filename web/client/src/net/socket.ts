import { io, type Socket } from 'socket.io-client';
import type { GameStateView, ChatMessage } from '@muw/shared';
import { SERVER_URL } from './session.js';

let socket: Socket | null = null;

export interface ConnectOpts {
  userId: string;
  username: string;
}

export function connectSocket(opts: ConnectOpts): Socket {
  if (socket && socket.connected) return socket;
  socket?.disconnect();
  // When VITE_SERVER_URL is set (Cloudflare Pages build pointing to the
  // standalone API host), socket.io connects directly to that origin.
  // Otherwise it connects same-origin via the Vite dev proxy / nginx mount.
  socket = SERVER_URL
    ? io(SERVER_URL, {
        path: '/socket.io',
        withCredentials: true,
        autoConnect: true,
        auth: { userId: opts.userId, username: opts.username },
      })
    : io({
        path: '/socket.io',
        withCredentials: true,
        autoConnect: true,
        auth: { userId: opts.userId, username: opts.username },
      });
  if (typeof window !== 'undefined') (window as unknown as { __socket: Socket }).__socket = socket;
  return socket;
}

export function getSocket(): Socket | null { return socket; }

export function disconnectSocket(): void {
  socket?.disconnect();
  socket = null;
}

export type StateListener = (state: GameStateView) => void;
export type ChatListener = (msg: ChatMessage) => void;
export type ErrorListener = (err: { code: string; message: string }) => void;
