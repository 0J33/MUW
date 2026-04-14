import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  applyAttack, applyCastAbility, applyEndTurn, applyLeaderAbility, applyMove, applySurrender,
  createGame, parseAbilities, parseChampions, TEAM_SIZE,
  type AbilityTarget, type Direction, type GameState, type GameStateView,
  currentChampion,
} from '@muw/shared';
import { TeamSelect } from '../components/TeamSelect/TeamSelect.js';
import { MatchView } from '../components/Match/MatchView.js';

// Two teams, shared device. Between turns we show a "pass to next player"
// overlay so neither side sees the opponent's prep.

type Phase = 'setup0' | 'setup1' | 'match';

export function LocalMatch() {
  const [phase, setPhase] = useState<Phase>('setup0');
  const [picks, setPicks] = useState<[string[], string[]]>([[], []]);
  const [leaders, setLeaders] = useState<[string | null, string | null]>([null, null]);
  const [state, setState] = useState<GameState | null>(null);
  const [showPass, setShowPass] = useState(false);
  const [data, setData] = useState<{ abilities: ReturnType<typeof parseAbilities>; champions: ReturnType<typeof parseChampions> } | null>(null);

  useEffect(() => {
    void (async () => {
      const [a, c] = await Promise.all([
        fetch('/assets/Abilities.csv').then(r => r.text()),
        fetch('/assets/Champions.csv').then(r => r.text()),
      ]);
      setData({ abilities: parseAbilities(a), champions: parseChampions(c) });
    })();
  }, []);

  const activeSeat: 0 | 1 = phase === 'setup0' ? 0 : 1;
  const mySeatPicks = picks[activeSeat];
  const mySeatLeader = leaders[activeSeat];

  function onPick(name: string) {
    setPicks(prev => {
      const copy: [string[], string[]] = [[...prev[0]], [...prev[1]]];
      if (copy[activeSeat].length >= TEAM_SIZE) return prev;
      if (copy[0].includes(name) || copy[1].includes(name)) return prev;
      copy[activeSeat].push(name);
      return copy;
    });
  }
  function onUnpick(name: string) {
    setPicks(prev => {
      const copy: [string[], string[]] = [[...prev[0]], [...prev[1]]];
      copy[activeSeat] = copy[activeSeat].filter(n => n !== name);
      return copy;
    });
    setLeaders(prev => {
      const copy: [string | null, string | null] = [prev[0], prev[1]];
      if (copy[activeSeat] === name) copy[activeSeat] = null;
      return copy;
    });
  }
  function onSetLeader(name: string) {
    setLeaders(prev => {
      const copy: [string | null, string | null] = [prev[0], prev[1]];
      copy[activeSeat] = name;
      return copy;
    });
  }

  function startMatch() {
    if (!data) return;
    if (picks[0].length !== TEAM_SIZE || picks[1].length !== TEAM_SIZE) return;
    if (!leaders[0] || !leaders[1]) return;
    const g = createGame({
      seed: Math.floor(Math.random() * 0xffffffff),
      abilities: data.abilities,
      champions: data.champions,
      players: [
        { userId: 'local0', name: 'Player 1', teamNames: picks[0], leaderName: leaders[0]! },
        { userId: 'local1', name: 'Player 2', teamNames: picks[1], leaderName: leaders[1]! },
      ],
    });
    setState(g);
    setPhase('match');
    setShowPass(true);
  }

  function runAction(mutate: (g: GameState, uid: string) => unknown) {
    if (!state) return;
    const curr = currentChampion(state);
    if (!curr) return;
    const uid = curr.ownerIndex === 0 ? 'local0' : 'local1';
    const before = curr.id;
    mutate(state, uid);
    setState({ ...state }); // force re-render; engine mutates in place
    const after = currentChampion(state);
    if (after && after.id !== before && after.ownerIndex !== curr.ownerIndex) setShowPass(true);
  }

  const actions = {
    move: (d: Direction) => { runAction((g, uid) => applyMove(g, uid, d)); },
    attack: (d: Direction) => { runAction((g, uid) => applyAttack(g, uid, d)); },
    castAbility: (name: string, target: AbilityTarget) => { runAction((g, uid) => applyCastAbility(g, uid, name, target)); },
    leaderAbility: () => { runAction((g, uid) => applyLeaderAbility(g, uid)); },
    endTurn: () => { runAction((g, uid) => applyEndTurn(g, uid)); },
    surrender: () => { runAction((g, uid) => applySurrender(g, uid)); },
  };

  const view: GameStateView | null = useMemo(() => state ? viewFromLocal(state) : null, [state]);
  const viewerUserId = state ? (currentChampion(state)?.ownerIndex === 0 ? 'local0' : 'local1') : 'local0';

  if (phase !== 'match') {
    return (
      <div className="h-screen w-screen flex flex-col p-4 gap-3 overflow-hidden">
        <header className="flex items-center justify-between">
          <h1 className="font-pixel text-base text-muwGold">Hotseat · {activeSeat === 0 ? 'Player 1' : 'Player 2'} Picks</h1>
          <Link to="/" className="pixel-btn pixel-btn-steel">Back to Menu</Link>
        </header>
        <div className="flex-1 min-h-0 overflow-y-auto thin-scroll pr-2">
          <TeamSelect
            myPicks={mySeatPicks}
            myLeader={mySeatLeader}
            opponentPicks={picks[activeSeat === 0 ? 1 : 0]}
            locked={false}
            onPick={onPick}
            onUnpick={onUnpick}
            onSetLeader={onSetLeader}
          />
        </div>
        <div className="flex gap-3">
          {phase === 'setup0' && (
            <button className="pixel-btn pixel-btn-gold" disabled={mySeatPicks.length !== TEAM_SIZE || !mySeatLeader} onClick={() => { setPhase('setup1'); }}>
              Pass to Player 2
            </button>
          )}
          {phase === 'setup1' && (
            <button className="pixel-btn pixel-btn-gold" disabled={mySeatPicks.length !== TEAM_SIZE || !mySeatLeader} onClick={startMatch}>
              Start Match
            </button>
          )}
        </div>
      </div>
    );
  }

  return (
    <div className="relative">
      {view && <MatchView state={view} viewerUserId={viewerUserId} actions={actions} />}
      {showPass && view && view.phase === 'active' && (
        <div className="fixed inset-0 bg-black/95 z-50 flex items-center justify-center text-center">
          <div className="arcade-frame-gold p-8 space-y-3">
            <h2 className="font-pixel text-2xl text-muwGold">Pass The Device</h2>
            <p className="font-pixel text-[0.78rem] text-gray-300">{viewerUserId === 'local0' ? 'Player 1' : 'Player 2'}&apos;s Turn</p>
            <button className="pixel-btn mt-4" onClick={() => { setShowPass(false); }}>Continue</button>
          </div>
        </div>
      )}
    </div>
  );
}

function viewFromLocal(g: GameState): GameStateView {
  const curr = currentChampion(g);
  return {
    code: 'LOCAL',
    phase: g.phase,
    viewerIndex: curr?.ownerIndex ?? 0,
    currentChampionId: curr ? curr.id : null,
    board: g.board.map(row => row.map(cell => (cell ? { ...cell } : null))),
    champions: Object.values(g.champions).map(c => ({
      ...c,
      abilities: c.abilities.map(a => ({ ...a })),
      appliedEffects: c.appliedEffects.map(e => ({ ...e })),
    })),
    players: g.players.map((p, i) => ({
      userId: p.userId,
      username: p.name,
      connected: true,
      picks: p.teamIds.map(id => g.champions[id]?.name ?? ''),
      leader: p.leaderId ? g.champions[p.leaderId]?.name ?? null : null,
      ready: true,
      seatIndex: i as 0 | 1,
    })),
    turnOrder: [...g.turnOrder],
    firstLeaderAbilityUsed: g.firstLeaderAbilityUsed,
    secondLeaderAbilityUsed: g.secondLeaderAbilityUsed,
    winnerIndex: g.winnerIndex,
    endReason: g.endReason,
    events: [],
  };
}
