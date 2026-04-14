import { useEffect, useMemo, useState } from 'react';
import { Link } from 'react-router-dom';
import {
  applyAttack, applyCastAbility, applyEndTurn, applyLeaderAbility, applyMove, applySurrender,
  botPickAction, createGame, currentChampion, parseAbilities, parseChampions, TEAM_SIZE,
  type AbilityTarget, type Direction, type GameState, type GameStateView,
} from '@muw/shared';
import { TeamSelect } from '../components/TeamSelect/TeamSelect.js';
import { MatchView } from '../components/Match/MatchView.js';

const HUMAN_ID = 'me';
const BOT_ID = 'bot';

// Pacing constants — tuned so the player can read each enemy action.
const FIRST_BOT_ACTION_DELAY_MS = 3500; // longer breath at match start
const NEXT_BOT_ACTION_DELAY_MS = 1800;  // between subsequent bot actions

export function AiMatch() {
  const [phase, setPhase] = useState<'setup' | 'match'>('setup');
  const [picks, setPicks] = useState<string[]>([]);
  const [leader, setLeader] = useState<string | null>(null);
  const [state, setState] = useState<GameState | null>(null);
  const [data, setData] = useState<{ abilities: ReturnType<typeof parseAbilities>; champions: ReturnType<typeof parseChampions> } | null>(null);
  // Tick counter that bumps after every bot action so the bot useEffect can
  // re-fire (turnOrder[0] doesn't change between same-champion sub-actions).
  const [botTick, setBotTick] = useState(0);
  // Reset to false on every fresh match so the very first bot action gets the
  // longer pause; flipped true after the first action plays.
  const [firstActionDone, setFirstActionDone] = useState(false);

  useEffect(() => {
    void (async () => {
      const [a, c] = await Promise.all([
        fetch('/assets/Abilities.csv').then(r => r.text()),
        fetch('/assets/Champions.csv').then(r => r.text()),
      ]);
      setData({ abilities: parseAbilities(a), champions: parseChampions(c) });
    })();
  }, []);

  function pickBotTeam(availableNames: string[], humanPicks: string[]): { team: string[]; leader: string } {
    const pool = availableNames.filter(n => !humanPicks.includes(n));
    // Simple bot team: Hulk if available, then Thor, then Ironman. Fallback: first three.
    const preferred = ['Hulk', 'Thor', 'Ironman', 'Dr Strange', 'Captain America', 'Loki'].filter(n => pool.includes(n));
    const team = [...preferred, ...pool].slice(0, TEAM_SIZE);
    return { team, leader: team[0]! };
  }

  function start() {
    if (!data || picks.length !== TEAM_SIZE || !leader) return;
    const bot = pickBotTeam(data.champions.map(c => c.name), picks);
    const g = createGame({
      seed: Math.floor(Math.random() * 0xffffffff),
      abilities: data.abilities,
      champions: data.champions,
      players: [
        { userId: HUMAN_ID, name: 'You', teamNames: picks, leaderName: leader },
        { userId: BOT_ID, name: 'Computer', teamNames: bot.team, leaderName: bot.leader },
      ],
    });
    setState(g);
    setPhase('match');
    setBotTick(0);
    setFirstActionDone(false);
  }

  // Bot turn pacing — schedules ONE bot action at a time with a delay so the
  // player can see each move (move, attack, ability) play out. After each
  // action we bump botTick which re-fires this effect to schedule the next.
  useEffect(() => {
    if (!state || state.phase !== 'active') return;
    const current = currentChampion(state);
    if (!current || current.ownerIndex !== 1) return; // not bot's turn
    const delay = firstActionDone ? NEXT_BOT_ACTION_DELAY_MS : FIRST_BOT_ACTION_DELAY_MS;
    const t = setTimeout(() => {
      // Re-check phase right before acting (it might have changed during the
      // delay if the human surrendered or a victory was triggered).
      if (state.phase !== 'active') return;
      const before = current.id;
      botPickAction(state, BOT_ID);
      const now = currentChampion(state);
      if (!now || now.id === before) { applyEndTurn(state, BOT_ID); }
      setState({ ...state });
      setBotTick(n => n + 1);
      if (!firstActionDone) setFirstActionDone(true);
    }, delay);
    return () => { clearTimeout(t); };
  // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [state?.turnOrder[0], state?.phase, botTick, firstActionDone]);

  function mutate(fn: (g: GameState) => void) {
    if (!state) return;
    fn(state);
    setState({ ...state });
  }

  const actions = {
    move: (d: Direction) => { mutate(g => { applyMove(g, HUMAN_ID, d); }); },
    attack: (d: Direction) => { mutate(g => { applyAttack(g, HUMAN_ID, d); }); },
    castAbility: (name: string, target: AbilityTarget) => { mutate(g => { applyCastAbility(g, HUMAN_ID, name, target); }); },
    leaderAbility: () => { mutate(g => { applyLeaderAbility(g, HUMAN_ID); }); },
    endTurn: () => { mutate(g => { applyEndTurn(g, HUMAN_ID); }); },
    surrender: () => { mutate(g => { applySurrender(g, HUMAN_ID); }); },
  };

  const view: GameStateView | null = useMemo(() => state ? viewFromState(state) : null, [state]);

  if (phase === 'setup' || !view) {
    return (
      <div className="h-screen w-screen flex flex-col p-4 gap-3 overflow-hidden">
        <header className="flex items-center justify-between">
          <h1 className="font-pixel text-base text-muwGold">Versus Computer · Pick Your Team</h1>
          <Link to="/" className="pixel-btn pixel-btn-steel">Back to Menu</Link>
        </header>
        <div className="flex-1 min-h-0 overflow-y-auto thin-scroll pr-2">
          <TeamSelect
            myPicks={picks}
            myLeader={leader}
            opponentPicks={[]}
            locked={false}
            onPick={(n) => { setPicks(prev => prev.includes(n) || prev.length >= TEAM_SIZE ? prev : [...prev, n]); }}
            onUnpick={(n) => { setPicks(prev => prev.filter(x => x !== n)); if (leader === n) setLeader(null); }}
            onSetLeader={(n) => { if (picks.includes(n)) setLeader(n); }}
          />
        </div>
        <button className="pixel-btn pixel-btn-gold self-start" disabled={picks.length !== TEAM_SIZE || !leader || !data} onClick={start}>
          Start Match
        </button>
      </div>
    );
  }

  return <MatchView state={view} viewerUserId={HUMAN_ID} actions={actions} />;
}

function viewFromState(g: GameState): GameStateView {
  const curr = currentChampion(g);
  return {
    code: 'AI',
    phase: g.phase,
    viewerIndex: 0,
    currentChampionId: curr?.id ?? null,
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
