import { useCallback, useEffect, useRef } from 'react';

// Reuses the Java game's WAV sfx + loops the original theme as background
// music. Paths match files copied into client/public/assets.
const SFX_MAP: Record<string, string> = {
  attack: '/assets/attack.wav',
  cast: '/assets/cast.wav',
  heal: '/assets/heal.wav',
  die: '/assets/die.wav',
  move: '/assets/move.wav',
  hover: '/assets/hover.wav',
  select: '/assets/select.wav',
  leader: '/assets/leader.wav',
  // UI shorthand — any pixel button, lobby action, settings toggle uses these.
  // Distinct files would be better but these reuse what the original game shipped.
  click: '/assets/select.wav',
  ui: '/assets/hover.wav',
};

const BGM_URL = '/assets/theme.wav';

const VOLUME_KEY = 'muw:volume';
const MUSIC_VOLUME_KEY = 'muw:music';
const MUTE_KEY = 'muw:muted';

export function readVolume(): number {
  const v = Number(localStorage.getItem(VOLUME_KEY));
  return isFinite(v) && v >= 0 && v <= 1 ? v : 0.5;
}
export function readMusicVolume(): number {
  const v = Number(localStorage.getItem(MUSIC_VOLUME_KEY));
  return isFinite(v) && v >= 0 && v <= 1 ? v : 0.25;
}
export function readMuted(): boolean {
  return localStorage.getItem(MUTE_KEY) === '1';
}
export function setVolume(v: number): void {
  localStorage.setItem(VOLUME_KEY, String(Math.max(0, Math.min(1, v))));
  applyMusicVolume();
}
export function setMusicVolume(v: number): void {
  localStorage.setItem(MUSIC_VOLUME_KEY, String(Math.max(0, Math.min(1, v))));
  applyMusicVolume();
}
export function setMuted(m: boolean): void {
  localStorage.setItem(MUTE_KEY, m ? '1' : '0');
  applyMusicVolume();
}

// ─── Background music ────────────────────────────────────────────────
// One singleton Audio element, looping the original theme. Paused while
// muted; resumes when un-muted (after the gesture-unlock).
let bgm: HTMLAudioElement | null = null;
let audioUnlocked = false;

function ensureBgm(): HTMLAudioElement {
  if (bgm) return bgm;
  bgm = new Audio(BGM_URL);
  bgm.loop = true;
  bgm.preload = 'auto';
  return bgm;
}
function applyMusicVolume(): void {
  if (!bgm) return;
  const v = readMuted() ? 0 : readMusicVolume();
  bgm.volume = v;
  if (v === 0) bgm.pause();
  else if (audioUnlocked) bgm.play().catch(() => { /* ignore */ });
}

function unlockAudio() {
  if (audioUnlocked) return;
  audioUnlocked = true;
  for (const url of Object.values(SFX_MAP)) {
    const a = new Audio(url);
    a.muted = true;
    a.volume = 0;
    a.play().then(() => { a.pause(); a.currentTime = 0; }).catch(() => { /* ignore */ });
  }
  ensureBgm();
  applyMusicVolume();
}

if (typeof window !== 'undefined' && !audioUnlocked) {
  const onGesture = () => { unlockAudio(); };
  window.addEventListener('pointerdown', onGesture, { once: true, passive: true });
  window.addEventListener('keydown', onGesture, { once: true, passive: true });
  window.addEventListener('touchstart', onGesture, { once: true, passive: true });
}

export function useSfx() {
  const pool = useRef<Record<string, HTMLAudioElement[]>>({});

  const play = useCallback((name: string) => {
    if (readMuted()) return;
    const url = SFX_MAP[name];
    if (!url) return;
    const bank = pool.current[name] ??= [];
    let audio = bank.find(a => a.paused || a.ended);
    if (!audio) {
      audio = new Audio(url);
      bank.push(audio);
      if (bank.length > 4) bank.shift();
    }
    audio.currentTime = 0;
    audio.volume = readVolume();
    void audio.play().catch(() => { /* autoplay blocked until gesture */ });
  }, []);

  useEffect(() => () => {
    for (const bank of Object.values(pool.current)) {
      for (const a of bank) a.pause();
    }
  }, []);

  return { play };
}

// One-shot helper for callsites that don't render a hook (raw event handlers,
// outside React tree). Same play semantics as `useSfx().play`.
const oneshotPool: Record<string, HTMLAudioElement[]> = {};
export function playSfx(name: string): void {
  if (readMuted()) return;
  const url = SFX_MAP[name];
  if (!url) return;
  const bank = oneshotPool[name] ??= [];
  let audio = bank.find(a => a.paused || a.ended);
  if (!audio) {
    audio = new Audio(url);
    bank.push(audio);
    if (bank.length > 4) bank.shift();
  }
  audio.currentTime = 0;
  audio.volume = readVolume();
  void audio.play().catch(() => { /* ignore */ });
}
