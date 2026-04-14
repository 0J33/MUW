import { useState } from 'react';
import { useNavigate } from 'react-router-dom';
import { useGameStore } from '../store/gameStore.js';
import { getSocket } from '../net/socket.js';
import { C2S } from '@muw/shared';
import { setUsername, getUsername } from '../net/session.js';
import { SettingsCog } from '../components/Settings/SettingsCog.js';

export function Menu() {
  const me = useGameStore(s => s.me);
  const navigate = useNavigate();
  const [username, setName] = useState(getUsername() || '');
  const [joinCode, setJoinCode] = useState('');
  const [busy, setBusy] = useState(false);

  function ensureName(): string {
    const n = username.trim();
    if (!n) return 'Guest';
    setUsername(n);
    return n;
  }

  function onCreate() {
    const sock = getSocket();
    if (!sock) return;
    setBusy(true);
    const name = ensureName();
    sock.auth = { userId: me!.userId, username: name };
    sock.emit(C2S.roomCreate, {}, (res: { ok: true; code: string } | { ok: false; error: string }) => {
      setBusy(false);
      if (res.ok) navigate(`/r/${res.code}`);
    });
  }

  function onJoin() {
    const sock = getSocket();
    const code = joinCode.trim().toUpperCase();
    if (!sock || !code) return;
    setBusy(true);
    const name = ensureName();
    sock.auth = { userId: me!.userId, username: name };
    sock.emit(C2S.roomJoin, { code }, (res: { ok: true; code: string } | { ok: false; error: string }) => {
      setBusy(false);
      if (res.ok) navigate(`/r/${res.code}`);
    });
  }

  return (
    <div className="h-screen w-screen flex items-center justify-center p-6 relative overflow-hidden">
      <div className="absolute top-4 right-4 z-10"><SettingsCog /></div>

      {/* Decorative pixel grid backdrop */}
      <div
        aria-hidden
        className="absolute inset-0 opacity-20 pointer-events-none"
        style={{
          backgroundImage: 'linear-gradient(to right, #1b2430 1px, transparent 1px), linear-gradient(to bottom, #1b2430 1px, transparent 1px)',
          backgroundSize: '40px 40px',
        }}
      />

      <div className="relative w-full max-w-md flex flex-col items-center gap-6">
        <div className="text-center">
          <h1 className="font-pixel text-3xl md:text-4xl text-muwGold drop-shadow-[0_3px_0_#b3001b] leading-tight">
            MARVEL
          </h1>
          <h2 className="font-pixel text-lg md:text-xl text-white mt-2 drop-shadow-[0_2px_0_#b3001b]">ULTIMATE WAR</h2>
          <p className="font-pixel text-[0.7rem] text-gray-400 mt-4">5 by 5 tactical combat · pick three · fight</p>
        </div>

        <div className="arcade-frame p-5 w-full space-y-4">
          <label className="block">
            <span className="font-pixel text-[0.78rem] text-gray-300">Player Name</span>
            <input
              className="mt-1 w-full bg-black/60 border-2 border-muwSteelLight px-3 py-2 font-vt text-lg focus:outline-none focus:border-muwGold"
              value={username}
              onChange={e => { setName(e.target.value); }}
              maxLength={24}
              placeholder="Guest"
            />
          </label>

          <div className="grid grid-cols-1 gap-3">
            <button
              className="pixel-btn w-full"
              onClick={onCreate}
              disabled={busy || !me}
            >
              Create Online Room
            </button>
            <div className="flex gap-2">
              <input
                className="flex-1 bg-black/60 border-2 border-muwSteelLight px-3 py-2 font-pixel text-sm uppercase tracking-widest placeholder:text-gray-600 focus:outline-none focus:border-muwGold"
                value={joinCode}
                onChange={e => { setJoinCode(e.target.value); }}
                maxLength={6}
                placeholder="CODE"
              />
              <button className="pixel-btn pixel-btn-gold" onClick={onJoin} disabled={busy || !me || !joinCode.trim()}>Join</button>
            </div>
          </div>

          <div className="grid grid-cols-2 gap-3 pt-2">
            <button className="pixel-btn pixel-btn-steel" onClick={() => { navigate('/local'); }}>Hotseat</button>
            <button className="pixel-btn pixel-btn-steel" onClick={() => { navigate('/ai'); }}>Versus Computer</button>
          </div>
        </div>

        <p className="font-pixel text-[0.62rem] text-gray-500 text-center">
          Based on Marvel Ultimate War by 0J33 · Assets &copy; their respective owners
        </p>
      </div>
    </div>
  );
}
