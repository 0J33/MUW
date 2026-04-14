import { useParams } from 'react-router-dom';
import { useGameStore } from '../store/gameStore.js';
import { getSocket } from '../net/socket.js';
import { C2S, type AbilityTarget, type Direction } from '@muw/shared';
import { MatchView } from '../components/Match/MatchView.js';

export function Match() {
  const { code = '' } = useParams<{ code: string }>();
  const me = useGameStore(s => s.me);
  const state = useGameStore(s => s.state);

  if (!state || !me || state.code !== code || state.phase === 'lobby') {
    return <div className="min-h-screen flex items-center justify-center text-gray-400">Loading match…</div>;
  }

  const socket = getSocket();
  const actions = {
    move: (d: Direction) => socket?.emit(C2S.gameMove, { dir: d }),
    attack: (d: Direction) => socket?.emit(C2S.gameAttack, { dir: d }),
    castAbility: (abilityName: string, target: AbilityTarget) =>
      socket?.emit(C2S.gameCastAbility, { abilityName, target }),
    leaderAbility: () => socket?.emit(C2S.gameLeader),
    endTurn: () => socket?.emit(C2S.gameEndTurn),
    surrender: () => socket?.emit(C2S.gameSurrender),
  };

  return <MatchView state={state} viewerUserId={me.userId} actions={actions} />;
}
