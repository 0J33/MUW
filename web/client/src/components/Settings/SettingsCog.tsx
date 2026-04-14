import { useEffect, useState } from 'react';
import {
  readMuted, readVolume, readMusicVolume,
  setMuted, setVolume, setMusicVolume,
} from '../../hooks/useSfx.js';
import { GearIcon } from '../icons/Icons.js';

export function SettingsCog() {
  const [open, setOpen] = useState(false);
  const [volume, setVol] = useState(() => readVolume());
  const [musicVol, setMusicVol] = useState(() => readMusicVolume());
  const [muted, setMute] = useState(() => readMuted());

  useEffect(() => { setVolume(volume); }, [volume]);
  useEffect(() => { setMusicVolume(musicVol); }, [musicVol]);
  useEffect(() => { setMuted(muted); }, [muted]);

  return (
    <>
      <button
        aria-label="Settings"
        className="w-10 h-10 arcade-frame flex items-center justify-center hover:brightness-125 transition text-muwGold"
        onClick={() => { setOpen(true); }}
        title="Settings"
      >
        <GearIcon size={18} />
      </button>
      {open && (
        <div className="fixed inset-0 z-40 bg-black/80 flex items-center justify-center p-4" onClick={() => { setOpen(false); }}>
          <div className="arcade-frame p-6 w-full max-w-sm" onClick={(e) => { e.stopPropagation(); }}>
            <h2 className="font-pixel text-lg text-muwGold mb-4">Settings</h2>
            <div className="space-y-4">
              <label className="flex items-center justify-between">
                <span className="font-pixel text-xs">Mute everything</span>
                <input type="checkbox" checked={muted} onChange={(e) => { setMute(e.target.checked); }} className="w-5 h-5 accent-muwGold" />
              </label>

              <label className="block">
                <div className="flex justify-between font-pixel text-[0.78rem] mb-1">
                  <span>Sound effects</span>
                  <span>{Math.round(volume * 100)} percent</span>
                </div>
                <input
                  type="range" min={0} max={1} step={0.05}
                  value={volume}
                  disabled={muted}
                  onChange={(e) => { setVol(Number(e.target.value)); }}
                  className="w-full accent-muwGold"
                />
              </label>

              <label className="block">
                <div className="flex justify-between font-pixel text-[0.78rem] mb-1">
                  <span>Music</span>
                  <span>{Math.round(musicVol * 100)} percent</span>
                </div>
                <input
                  type="range" min={0} max={1} step={0.05}
                  value={musicVol}
                  disabled={muted}
                  onChange={(e) => { setMusicVol(Number(e.target.value)); }}
                  className="w-full accent-muwGold"
                />
              </label>

              <p className="font-pixel text-[0.62rem] text-gray-400">Saved locally · persists across sessions</p>
            </div>
            <div className="mt-6 flex justify-end">
              <button className="pixel-btn" onClick={() => { setOpen(false); }}>Close</button>
            </div>
          </div>
        </div>
      )}
    </>
  );
}
