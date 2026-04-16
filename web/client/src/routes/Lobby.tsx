import { useEffect } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { useGameStore } from '../store/gameStore.js';
import { TeamSelect } from '../components/TeamSelect/TeamSelect.js';
import { ChatBox } from '../components/ChatBox/ChatBox.js';
import { SettingsCog } from '../components/Settings/SettingsCog.js';
import { getSocket } from '../net/socket.js';
import { C2S, TEAM_SIZE } from '@muw/shared';

export function Lobby() {
  const { code = '' } = useParams<{ code: string }>();
  const navigate = useNavigate();
  const me = useGameStore(s => s.me);
  const state = useGameStore(s => s.state);
  const chat = useGameStore(s => s.chat);

  const myPlayer = state?.players.find(p => p.userId === me?.userId);
  const opponent = state?.players.find(p => p.userId !== me?.userId);

  useEffect(() => {
    if (state?.phase === 'active') navigate(`/r/${code}/play`, { replace: true });
  }, [state?.phase, navigate, code]);

  function send(event: string, payload: unknown): void { getSocket()?.emit(event, payload); }

  if (!state || state.code !== code) {
    return (
      <div className="h-screen flex items-center justify-center font-pixel text-xs text-gray-400">
        Joining room {code}…
      </div>
    );
  }

  const canReady = (myPlayer?.picks.length ?? 0) === TEAM_SIZE && myPlayer?.leader;

  return (
    <div className="h-svh w-screen flex flex-col p-3 sm:p-4 md:p-6 gap-3 sm:gap-4 overflow-hidden">
      <header className="flex items-end justify-between gap-4 flex-wrap">
        <div>
          <h1 className="font-pixel text-base text-muwGold">Room <span className="bg-muwInk arcade-frame px-2 py-0.5 font-pixel text-base ml-1">{code}</span></h1>
          <p className="font-pixel text-[0.7rem] text-gray-400 mt-1">Share this code with a friend to play</p>
        </div>
        <div className="flex items-center gap-2">
          <span className="font-pixel text-[0.78rem] text-gray-300">
            {state.players.length} of 2 players
            {opponent && !opponent.connected && <span className="ml-2 text-red-400">· Opponent offline</span>}
          </span>
          <button className="pixel-btn pixel-btn-steel" onClick={() => { getSocket()?.emit(C2S.roomLeave); navigate('/'); }}>Leave</button>
          <SettingsCog />
        </div>
      </header>

      <div className="flex-1 min-h-0 grid lg:grid-cols-[1fr_300px] gap-4">
        <div className="overflow-y-auto thin-scroll pr-2 space-y-3">
          <TeamSelect
            myPicks={myPlayer?.picks ?? []}
            myLeader={myPlayer?.leader ?? null}
            opponentPicks={opponent?.picks ?? []}
            locked={!!myPlayer?.ready}
            onPick={(name) => { send(C2S.teamPick, { championName: name }); }}
            onUnpick={(name) => { send(C2S.teamUnpick, { championName: name }); }}
            onSetLeader={(name) => { send(C2S.teamSetLeader, { championName: name }); }}
          />
          <div className="flex gap-3 items-center">
            <button
              className="pixel-btn pixel-btn-gold"
              disabled={!canReady}
              onClick={() => { send(C2S.teamReady, { ready: !myPlayer?.ready }); }}
            >
              {myPlayer?.ready ? 'Unready' : 'Ready'}
            </button>
            <span className="font-pixel text-[0.7rem] text-gray-300">
              {myPlayer?.ready ? 'Waiting for opponent…' : canReady ? 'Press Ready to start' : `Pick ${TEAM_SIZE - (myPlayer?.picks.length ?? 0)} more champions`}
            </span>
          </div>
        </div>

        <aside className="space-y-3">
          <ChatBox messages={chat} onSend={(text) => { send(C2S.chatSend, { text }); }} />
        </aside>
      </div>
    </div>
  );
}
