import { useEffect } from 'react';
import { Routes, Route, Navigate } from 'react-router-dom';
import { Menu } from './routes/Menu.js';
import { Lobby } from './routes/Lobby.js';
import { Match } from './routes/Match.js';
import { LocalMatch } from './routes/LocalMatch.js';
import { AiMatch } from './routes/AiMatch.js';
import { fetchMe, getUsername } from './net/session.js';
import { connectSocket } from './net/socket.js';
import { useGameStore } from './store/gameStore.js';

export function App() {
  const setMe = useGameStore(s => s.setMe);
  const setState = useGameStore(s => s.setState);
  const appendChat = useGameStore(s => s.appendChat);
  const setError = useGameStore(s => s.setError);

  useEffect(() => {
    let cancelled = false;
    void (async () => {
      const me = await fetchMe();
      if (cancelled) return;
      setMe(me);
      const username = getUsername() || 'Guest';
      const socket = connectSocket({ userId: me.userId, username });
      socket.on('state', (s) => { setState(s); });
      socket.on('chat:message', (m) => { appendChat(m); });
      socket.on('error', (e) => { setError(e); });
    })();
    return () => { cancelled = true; };
  }, [setMe, setState, appendChat, setError]);

  return (
    <Routes>
      <Route path="/" element={<Menu />} />
      <Route path="/r/:code" element={<Lobby />} />
      <Route path="/r/:code/play" element={<Match />} />
      <Route path="/local" element={<LocalMatch />} />
      <Route path="/ai" element={<AiMatch />} />
      <Route path="*" element={<Navigate to="/" replace />} />
    </Routes>
  );
}
