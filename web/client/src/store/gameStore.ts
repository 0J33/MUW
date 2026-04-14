import { create } from 'zustand';
import type { ChatMessage, GameStateView } from '@muw/shared';

export interface GameStore {
  me: { userId: string; username: string } | null;
  state: GameStateView | null;
  chat: ChatMessage[];
  lastError: { code: string; message: string } | null;
  setMe: (me: { userId: string; username: string }) => void;
  setState: (s: GameStateView) => void;
  appendChat: (m: ChatMessage) => void;
  replaceChat: (msgs: ChatMessage[]) => void;
  setError: (e: { code: string; message: string } | null) => void;
  reset: () => void;
}

export const useGameStore = create<GameStore>((set) => ({
  me: null,
  state: null,
  chat: [],
  lastError: null,
  setMe: (me) => { set({ me }); },
  setState: (s) => { set({ state: s }); },
  appendChat: (m) => { set(prev => ({ chat: [...prev.chat, m].slice(-200) })); },
  replaceChat: (msgs) => { set({ chat: msgs.slice(-200) }); },
  setError: (e) => { set({ lastError: e }); },
  reset: () => { set({ state: null, chat: [], lastError: null }); },
}));
