import { useEffect, useMemo, useRef, useState } from 'react';
import { AnimatePresence, motion } from 'framer-motion';
import type { ChampionInstance, Direction, GameStateView } from '@muw/shared';
import { Board } from '../Board/Board.js';
import { AbilityTray } from '../AbilityTray/AbilityTray.js';
import { ChampionCard } from '../ChampionCard/ChampionCard.js';
import { TurnTracker } from '../TurnTracker/TurnTracker.js';
import { SettingsCog } from '../Settings/SettingsCog.js';
import { useSfx } from '../../hooks/useSfx.js';
import { LEADER_POWERS } from '../leader/leaderPowers.js';
import { StarIcon } from '../icons/Icons.js';

type Intent =
  | { kind: 'none' }
  | { kind: 'move' }
  | { kind: 'attack' }
  | { kind: 'castAbility'; abilityName: string; area: string };

export interface MatchActions {
  move: (d: Direction) => void;
  attack: (d: Direction) => void;
  castAbility: (abilityName: string, target: import('@muw/shared').AbilityTarget) => void;
  leaderAbility: () => void;
  endTurn: () => void;
  surrender: () => void;
}

interface LogEntry { id: number; text: string; tone: 'info' | 'damage' | 'heal' | 'effect' | 'turn' | 'leader' }

export function MatchView({
  state, viewerUserId, actions, headerExtra,
}: {
  state: GameStateView;
  viewerUserId: string;
  actions: MatchActions;
  headerExtra?: React.ReactNode;
}) {
  const [intent, setIntent] = useState<Intent>({ kind: 'none' });
  const { play } = useSfx();

  const me = state.players.find(p => p.userId === viewerUserId);
  const myIndex: 0 | 1 = (me?.seatIndex ?? 0) as 0 | 1;
  const isMyTurn = !!state.currentChampionId && state.champions.find(c => c.id === state.currentChampionId)?.ownerIndex === myIndex;
  const currentChamp = state.champions.find(c => c.id === state.currentChampionId) ?? null;
  const isLeader = currentChamp ? me?.leader === currentChamp.name : false;
  const leaderUsed = myIndex === 0 ? state.firstLeaderAbilityUsed : state.secondLeaderAbilityUsed;

  // ─── Floating damage / heal numbers ──────────────────────────────────
  const prevHpRef = useRef<Record<string, number>>({});
  const [floaters, setFloaters] = useState<Array<{ id: string; x: number; y: number; delta: number; key: number }>>([]);
  const floaterKeyRef = useRef(0);

  // ─── Per-cell visual effect overlays (damage flash / heal aura / cc ring) ─
  type FxKind = 'damage' | 'heal' | 'cc';
  const [fxOverlays, setFxOverlays] = useState<Array<{ key: number; kind: FxKind; x: number; y: number }>>([]);
  const fxKeyRef = useRef(0);
  function pushFx(kind: FxKind, x: number, y: number) {
    fxKeyRef.current += 1;
    const key = fxKeyRef.current;
    setFxOverlays(prev => [...prev, { key, kind, x, y }].slice(-12));
    setTimeout(() => { setFxOverlays(prev => prev.filter(f => f.key !== key)); }, 1000);
  }

  // ─── Action log ──────────────────────────────────────────────────────
  // We diff successive state snapshots and synthesize human-readable lines.
  const prevSnapshotRef = useRef<{
    hp: Record<string, number>; pos: Record<string, [number, number]>;
    effects: Record<string, string[]>; current: string | null; alive: Set<string>;
  } | null>(null);
  const [log, setLog] = useState<LogEntry[]>([]);
  const logIdRef = useRef(0);
  const logScrollRef = useRef<HTMLDivElement | null>(null);
  function pushLog(text: string, tone: LogEntry['tone'] = 'info') {
    logIdRef.current += 1;
    setLog(prev => [...prev, { id: logIdRef.current, text, tone }].slice(-50));
  }
  // Whenever the log grows, glue the scroll to the bottom so the newest
  // entry is always visible.
  useEffect(() => {
    const el = logScrollRef.current;
    if (!el) return;
    el.scrollTop = el.scrollHeight;
  }, [log.length]);

  useEffect(() => {
    // Build current snapshot.
    const snap = {
      hp: {} as Record<string, number>,
      pos: {} as Record<string, [number, number]>,
      effects: {} as Record<string, string[]>,
      current: state.currentChampionId,
      alive: new Set(state.champions.map(c => c.id)),
    };
    for (const c of state.champions) {
      snap.hp[c.id] = c.currentHP;
      snap.pos[c.id] = [c.x, c.y];
      snap.effects[c.id] = c.appliedEffects.map(e => `${e.name}:${e.duration}`);
    }

    // Damage floaters + per-cell hit/heal flash effects.
    const prev = prevHpRef.current;
    const nextFloaters: typeof floaters = [];
    for (const c of state.champions) {
      const prevHp = prev[c.id];
      if (typeof prevHp === 'number' && prevHp !== c.currentHP) {
        floaterKeyRef.current += 1;
        nextFloaters.push({ id: c.id, x: c.x, y: c.y, delta: c.currentHP - prevHp, key: floaterKeyRef.current });
        pushFx(c.currentHP < prevHp ? 'damage' : 'heal', c.x, c.y);
      }
      prev[c.id] = c.currentHP;
    }
    for (const k of Object.keys(prev)) if (!snap.alive.has(k)) delete prev[k];
    if (nextFloaters.length) {
      setFloaters(prevList => [...prevList, ...nextFloaters].slice(-12));
      for (const f of nextFloaters) {
        setTimeout(() => { setFloaters(prevList => prevList.filter(x => x.key !== f.key)); }, 900);
      }
    }

    // Action log entries from snapshot diff.
    const prevSnap = prevSnapshotRef.current;
    if (prevSnap) {
      const championsByName = new Map<string, ChampionInstance>();
      for (const c of state.champions) championsByName.set(c.id, c);

      // HP changes (damage / heal) — deduplicate when one ability hits
      // multiple targets in one tick by grouping per-recipient lines.
      for (const c of state.champions) {
        const prevHp = prevSnap.hp[c.id];
        if (typeof prevHp === 'number' && prevHp !== c.currentHP) {
          const delta = c.currentHP - prevHp;
          if (delta < 0) pushLog(`${c.name} took ${-delta} damage`, 'damage');
          else pushLog(`${c.name} healed ${delta}`, 'heal');
        }
      }
      // KOs
      for (const id of prevSnap.alive) {
        if (!snap.alive.has(id)) {
          // We have to look at prevSnap to know who died (their record is gone from state.champions).
          const prevName = inferNameFromId(id);
          if (prevName) pushLog(`${prevName} was knocked out`, 'damage');
        }
      }
      // Effect changes
      for (const c of state.champions) {
        const before = new Set(prevSnap.effects[c.id] ?? []);
        const after = new Set(snap.effects[c.id] ?? []);
        let gainedAny = false;
        for (const e of after) {
          if (!before.has(e)) {
            const [name] = e.split(':');
            // Skip pure timer-decrement events by checking the bare name prefix.
            const wasOnByName = [...before].some(x => x.startsWith(`${name}:`));
            if (!wasOnByName) {
              pushLog(`${c.name} gained ${name}`, 'effect');
              gainedAny = true;
            }
          }
        }
        if (gainedAny) pushFx('cc', c.x, c.y);
        for (const e of before) {
          const [name] = e.split(':');
          const stillOnByName = [...after].some(x => x.startsWith(`${name}:`));
          if (!stillOnByName) pushLog(`${c.name}: ${name} expired`, 'effect');
        }
      }
      // Turn change
      if (prevSnap.current !== snap.current && snap.current) {
        const c = state.champions.find(c => c.id === snap.current);
        if (c) pushLog(`${c.name}'s turn`, 'turn');
      }
      // Leader power used
      if (!prevSnap && (state.firstLeaderAbilityUsed || state.secondLeaderAbilityUsed)) { /* noop */ }
    }
    prevSnapshotRef.current = snap;
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, [state]);

  // Helper: try to recover a sensible name from a champion id like
  // "c_0_Captain_America_3". Used when the champion is no longer in the
  // current state because they were just KO'd.
  function inferNameFromId(id: string): string | null {
    const m = /^c_[01]_(.+?)_\d+$/.exec(id);
    if (!m) return null;
    return m[1]!.replace(/_/g, ' ');
  }

  // ─── Direction key mapping (W/S/Arrows) ──────────────────────────────
  // Player 1 sees the board flipped relative to engine coords, so their
  // visual UP = engine DOWN. LEFT/RIGHT are unchanged because we only
  // mirror rows, not columns.
  const flipVertical = myIndex === 1;
  const dirKeys: Record<string, Direction> = {
    ArrowUp:    flipVertical ? 'DOWN' : 'UP',
    ArrowDown:  flipVertical ? 'UP'   : 'DOWN',
    ArrowLeft:  'LEFT',
    ArrowRight: 'RIGHT',
    w:          flipVertical ? 'DOWN' : 'UP',
    s:          flipVertical ? 'UP'   : 'DOWN',
    a:          'LEFT',
    d:          'RIGHT',
  };

  function triggerAbilityByIndex(i: number) {
    if (!currentChamp) return;
    const a = currentChamp.abilities[i];
    if (!a) return;
    triggerAbility(a.name);
  }

  useEffect(() => {
    function onKey(ev: KeyboardEvent) {
      if (ev.target instanceof HTMLInputElement || ev.target instanceof HTMLTextAreaElement) return;
      const dir = dirKeys[ev.key];
      // Move intent is "sticky" — the picker stays open after each move so
      // the player can chain moves without re-clicking Move. They dismiss it
      // with Escape (or the on-screen cancel button). Attacks / abilities
      // are one-shots and still reset.
      if (intent.kind === 'move' && dir) { actions.move(dir); play('move'); return; }
      if (intent.kind === 'attack' && dir) { actions.attack(dir); setIntent({ kind: 'none' }); play('attack'); return; }
      if (intent.kind === 'castAbility' && dir && (intent.area === 'DIRECTIONAL')) {
        actions.castAbility(intent.abilityName, { kind: 'direction', dir });
        setIntent({ kind: 'none' });
        play('cast');
        return;
      }
      if (ev.key === 'Escape') setIntent({ kind: 'none' });
      if (ev.key === 'm') setIntent({ kind: 'move' });
      if (ev.key === 'f') setIntent({ kind: 'attack' });
      if (ev.key === 'e') { actions.endTurn(); play('select'); }
      if (ev.key === 'l' && isLeader && !leaderUsed) { actions.leaderAbility(); play('leader'); }
      if (ev.key === '1') triggerAbilityByIndex(0);
      if (ev.key === '2') triggerAbilityByIndex(1);
      if (ev.key === '3') triggerAbilityByIndex(2);
    }
    window.addEventListener('keydown', onKey);
    return () => { window.removeEventListener('keydown', onKey); };
  }, [intent, actions, play, isLeader, leaderUsed, currentChamp, dirKeys]);

  const selectable = useMemo(() => selectableFor(intent, currentChamp, state), [intent, currentChamp, state]);

  // For the on-screen direction picker we hide arrows that would correspond
  // to invalid moves / attacks / directional casts. Computed off the same
  // engine logic that would reject them server-side, so the UI stays honest.
  const validDirs = useMemo(() => validEngineDirsFor(intent, currentChamp, state), [intent, currentChamp, state]);

  function onCellClick(x: number, y: number): void {
    if (intent.kind === 'castAbility' && (intent.area === 'SINGLETARGET')) {
      actions.castAbility(intent.abilityName, { kind: 'cell', x, y });
      setIntent({ kind: 'none' });
      play('cast');
    }
  }

  function triggerAbility(name: string): void {
    const a = currentChamp?.abilities.find(x => x.name === name);
    if (!a) return;
    if (a.castArea === 'SELFTARGET') { actions.castAbility(name, { kind: 'self' }); play(a.kind === 'HEL' ? 'heal' : 'cast'); return; }
    if (a.castArea === 'TEAMTARGET') { actions.castAbility(name, { kind: 'team' }); play(a.kind === 'HEL' ? 'heal' : 'cast'); return; }
    if (a.castArea === 'SURROUND')   { actions.castAbility(name, { kind: 'surround' }); play('cast'); return; }
    setIntent({ kind: 'castAbility', abilityName: name, area: a.castArea });
  }

  // Direction picker is only shown when an intent needs a direction AND it's
  // the viewer's turn AND we have a current champion to anchor it on.
  const showPicker = isMyTurn && currentChamp && (
    intent.kind === 'move' || intent.kind === 'attack' ||
    (intent.kind === 'castAbility' && intent.area === 'DIRECTIONAL')
  );
  const pickerProp = showPicker ? {
    championId: currentChamp.id,
    validEngineDirs: validDirs,
    onPick: (engineDir: Direction) => {
      if (intent.kind === 'move') {
        actions.move(engineDir);
        play('move');
        // Keep the move picker open for chained moves; user dismisses with Esc / cancel.
        return;
      }
      if (intent.kind === 'attack') { actions.attack(engineDir); play('attack'); }
      else if (intent.kind === 'castAbility' && intent.area === 'DIRECTIONAL') {
        actions.castAbility(intent.abilityName, { kind: 'direction', dir: engineDir });
        play('cast');
      }
      setIntent({ kind: 'none' });
    },
    onCancel: () => { setIntent({ kind: 'none' }); },
  } : null;

  // Compute leader-ids BEFORE any conditional early-return — the rules of
  // hooks require useMemo to run on every render in the same order.
  const leaderIds = useMemo(() => {
    const out = new Set<string>();
    for (const p of state.players) {
      if (!p.leader) continue;
      const c = state.champions.find(c => c.ownerIndex === p.seatIndex && c.name === p.leader);
      if (c) out.add(c.id);
    }
    return out;
  }, [state.players, state.champions]);
  const myLeaderChamp = state.champions.find(c => c.ownerIndex === myIndex && leaderIds.has(c.id)) ?? null;

  // Hold the victory screen back for a beat so the killing-blow floater /
  // damage flash / KO animation can finish playing out before the overlay
  // takes over. Surrender skips the delay — there's no animation to wait
  // for, the player just clicked Concede and expects an immediate result.
  const [showVictory, setShowVictory] = useState(false);
  useEffect(() => {
    if (state.phase === 'ended') {
      const delay = state.endReason === 'surrendered' ? 0 : 1000;
      if (delay === 0) { setShowVictory(true); return undefined; }
      const t = setTimeout(() => { setShowVictory(true); }, delay);
      return () => { clearTimeout(t); };
    }
    setShowVictory(false);
    return undefined;
  }, [state.phase, state.endReason]);

  if (state.phase === 'ended' && showVictory) {
    const winner = state.winnerIndex !== null ? state.players[state.winnerIndex] : null;
    const won = winner?.userId === viewerUserId;
    return <VictoryScreen won={won} winnerName={winner?.username ?? ''} />;
  }

  const enemySeat = state.players.find(p => p.userId !== viewerUserId);
  const mySeat = state.players.find(p => p.userId === viewerUserId);

  // Floater visual position depends on whether we flipped the board for the viewer.
  const floaterRow = (engineX: number) => myIndex === 0 ? state.board.length - 1 - engineX : engineX;

  return (
    <div className="h-svh w-screen flex flex-col p-3 md:p-4 gap-2 overflow-hidden">
      <header className="flex items-center justify-between gap-3 flex-wrap shrink-0">
        <div className="flex items-center gap-3 min-w-0">
          <h1 className="font-pixel text-sm md:text-base text-muwGold drop-shadow-[0_2px_0_#b3001b] truncate">Marvel Ultimate War</h1>
          <span className="font-pixel text-[0.7rem] text-gray-400">Room {state.code}</span>
        </div>
        <div className="font-pixel text-[0.78rem] text-center flex-1 min-w-[180px]">
          {isMyTurn
            ? <span className="text-muwGold">Your Turn — {currentChamp?.name}</span>
            : <span className="text-gray-400">Opponent — {currentChamp?.name}</span>}
        </div>
        <div className="flex items-center gap-2">
          {headerExtra}
          <SettingsCog />
        </div>
      </header>

      <PlayerBar seat={enemySeat} champions={state.champions} forIndex={(myIndex === 0 ? 1 : 0) as 0 | 1} isCurrent={currentChamp?.ownerIndex !== myIndex} leaderUsed={myIndex === 0 ? state.secondLeaderAbilityUsed : state.firstLeaderAbilityUsed} label="Opponent" />

      <div className="flex-1 min-h-0 flex gap-3">
        <aside className="hidden lg:flex shrink-0 w-44 flex-col gap-2 min-h-0">
          <div className="font-pixel text-[0.7rem] text-gray-400">Turn Order</div>
          <TurnTracker turnOrder={state.turnOrder} champions={state.champions} currentId={state.currentChampionId} leaderIds={leaderIds} />
          <div className="font-pixel text-[0.7rem] text-gray-400 mt-1">Action Log</div>
          <div ref={logScrollRef} className="arcade-frame p-2 flex-1 min-h-0 overflow-y-auto thin-scroll text-[0.78rem] font-vt leading-tight space-y-0.5">
            {log.length === 0 && <div className="text-gray-500">Waiting for the first move…</div>}
            {log.map(e => (
              <div key={e.id} className={
                e.tone === 'damage' ? 'text-red-300' :
                e.tone === 'heal' ? 'text-emerald-300' :
                e.tone === 'effect' ? 'text-indigo-300' :
                e.tone === 'turn' ? 'text-muwGold' : 'text-gray-200'
              }>{e.text}</div>
            ))}
          </div>
        </aside>

        <div className="flex-1 relative flex items-center justify-center min-w-0 min-h-0">
          <div className="relative aspect-square" style={{ width: 'min(100%, calc(100svh - 280px))', maxHeight: '100%' }}>
            <Board state={state} viewerIndex={myIndex} selectableCells={selectable} onCellClick={onCellClick} directionPicker={pickerProp} leaderIds={leaderIds} />
            {/* Per-cell impact / heal / crowd-control auras. Positioned with the
                same percentage math as floaters; CSS keyframes carry the actual
                animation. */}
            {fxOverlays.map(fx => (
              <div
                key={fx.key}
                className={`fx-overlay fx-${fx.kind}`}
                style={cellOverlayStyle(floaterRow(fx.x), fx.y)}
              />
            ))}
            <AnimatePresence>
              {floaters.map(f => (
                <motion.div
                  key={f.key}
                  initial={{ opacity: 0, y: 0, scale: 0.9 }}
                  animate={{ opacity: 1, y: -46, scale: 1.2 }}
                  exit={{ opacity: 0 }}
                  transition={{ duration: 0.9, ease: 'easeOut' }}
                  className={`absolute pointer-events-none font-pixel text-base drop-shadow-[2px_2px_0_#000] ${f.delta < 0 ? 'text-red-400' : 'text-emerald-300'}`}
                  style={floaterStyle(floaterRow(f.x), f.y)}
                >
                  {f.delta > 0 ? `+${f.delta}` : f.delta}
                </motion.div>
              ))}
            </AnimatePresence>
          </div>
        </div>

        <aside className="hidden md:flex shrink-0 w-72 flex-col gap-2 min-h-0">
          <div className="font-pixel text-[0.7rem] text-gray-400">Active Champion</div>
          <AnimatePresence mode="wait">
            {currentChamp && (
              <motion.div key={currentChamp.id} initial={{ opacity: 0, x: 8 }} animate={{ opacity: 1, x: 0 }} exit={{ opacity: 0, x: 8 }} transition={{ duration: 0.2 }}>
                {/* Use the hover variant so we get small portrait + full
                    stats + abilities + active effects, all in one slot. */}
                <ChampionCard champ={currentChamp} hover />
              </motion.div>
            )}
          </AnimatePresence>
          {/* Reserve fixed slots so the sidebar doesn't reflow when the leader
              dies or when an intent is queued mid-turn. */}
          <div className="arcade-frame p-2 space-y-1 min-h-[120px]">
            {myLeaderChamp ? (
              <>
                <div className="font-pixel text-[0.62rem] text-muwGold flex items-baseline justify-between">
                  <span>Your Leader Power</span>
                  <span className="text-gray-400">{leaderUsed ? 'Used' : 'Ready'}</span>
                </div>
                <div className="font-pixel text-[0.7rem] text-white">{LEADER_POWERS[myLeaderChamp.kind].name}</div>
                <div className="font-vt text-[0.85rem] text-gray-300 leading-tight">{LEADER_POWERS[myLeaderChamp.kind].description}</div>
                <div className="font-pixel text-[0.55rem] text-gray-500">Used by {myLeaderChamp.name}</div>
              </>
            ) : (
              <div className="font-pixel text-[0.62rem] text-gray-500">Your leader has fallen.</div>
            )}
          </div>
          <div className="arcade-frame-gold p-2 font-pixel text-[0.62rem] text-muwGold leading-relaxed min-h-[44px]" style={{ visibility: intent.kind === 'none' ? 'hidden' : 'visible' }}>
            {intent.kind === 'move' && 'Pick a direction with the on-screen arrows or W A S D.'}
            {intent.kind === 'attack' && 'Pick an attack direction with the on-screen arrows or W A S D.'}
            {intent.kind === 'castAbility' && intent.area === 'DIRECTIONAL' && 'Pick a direction with the on-screen arrows or W A S D.'}
            {intent.kind === 'castAbility' && intent.area === 'SINGLETARGET' && 'Click a target cell on the board.'}
          </div>
        </aside>
      </div>

      <PlayerBar seat={mySeat} champions={state.champions} forIndex={myIndex} isCurrent={currentChamp?.ownerIndex === myIndex} leaderUsed={leaderUsed} label="You" />

      <AbilityTray
        champion={currentChamp}
        isLeaderTurn={!!isLeader}
        leaderKind={myLeaderChamp?.kind ?? null}
        leaderAbilityUsed={leaderUsed}
        yourTurn={isMyTurn}
        onAttack={() => { setIntent({ kind: 'attack' }); }}
        onMove={() => { setIntent({ kind: 'move' }); }}
        onCastAbility={triggerAbility}
        onLeaderAbility={() => { actions.leaderAbility(); play('leader'); }}
        onEndTurn={() => { actions.endTurn(); play('select'); }}
        onSurrender={actions.surrender}
      />
    </div>
  );
}

function PlayerBar({ seat, champions, forIndex, isCurrent, leaderUsed, label }: {
  seat: { userId: string; username: string; leader: string | null } | undefined;
  champions: ChampionInstance[];
  forIndex: 0 | 1;
  isCurrent: boolean;
  leaderUsed: boolean;
  label: string;
}) {
  if (!seat) return null;
  const team = champions.filter(c => c.ownerIndex === forIndex);
  return (
    <div className={`arcade-frame ${isCurrent ? 'arcade-frame-gold' : ''} px-2 py-1 flex items-center gap-2 shrink-0`}>
      <div className="font-pixel text-[0.78rem] text-muwGold truncate max-w-[120px]">{seat.username}</div>
      <div className="font-pixel text-[0.62rem] text-gray-400">{label}</div>
      <div className="flex gap-1.5 flex-wrap">
        {team.map(c => {
          const pct = Math.max(0, Math.min(100, (c.currentHP / c.maxHP) * 100));
          const isLeader = c.name === seat.leader;
          return (
            <div
              key={c.id}
              className={`relative w-44 bg-black/40 border ${isLeader ? 'border-muwGold' : 'border-white/10'} px-1.5 py-1`}
              title={`${c.name}${isLeader ? ' (Leader)' : ''} — ${c.currentHP}/${c.maxHP} health`}
            >
              <div className="flex items-baseline justify-between gap-1 font-pixel text-[0.62rem]">
                <span className="truncate flex items-center gap-1">
                  {isLeader && <span className="text-muwGold inline-flex"><StarIcon size={8} /></span>}
                  {c.name}
                </span>
              </div>
              {/* Health bar with the numeric values overlaid in pixel font.
                  Two layers — colored fill clipped behind, full-width text on top. */}
              <div className="relative h-3.5 bg-black/80 mt-1 overflow-hidden">
                <div className="absolute inset-y-0 left-0 hp-bar-fill" style={{ width: `${pct}%` }} />
                <div className="absolute inset-0 flex items-center justify-center font-pixel text-[0.55rem] text-white drop-shadow-[1px_1px_0_#000] mix-blend-normal">
                  {c.currentHP} / {c.maxHP}
                </div>
              </div>
            </div>
          );
        })}
      </div>
      <div className="flex-1" />
      <div className="font-pixel text-[0.62rem] text-gray-400">Leader Power: {leaderUsed ? 'Used' : 'Ready'}</div>
    </div>
  );
}

function floaterStyle(renderRow: number, renderCol: number): React.CSSProperties {
  return {
    left: `${(renderCol + 0.5) * 20}%`,
    top: `${(renderRow + 0.5) * 20}%`,
    transform: 'translate(-50%, -50%)',
  };
}

// Position an overlay div over a single board cell. The board is a 5×5 grid
// occupying 100% of the wrapper; each cell is 20% × 20%.
function cellOverlayStyle(renderRow: number, renderCol: number): React.CSSProperties {
  return {
    left: `${renderCol * 20}%`,
    top: `${renderRow * 20}%`,
    width: '20%',
    height: '20%',
    position: 'absolute',
  };
}

function selectableFor(intent: Intent, c: ChampionInstance | null, state: GameStateView): Set<string> | undefined {
  if (!c || intent.kind !== 'castAbility' || intent.area !== 'SINGLETARGET') return undefined;
  const a = c.abilities.find(x => x.name === intent.abilityName);
  if (!a) return undefined;
  const out = new Set<string>();
  for (let x = 0; x < 5; x++) {
    for (let y = 0; y < 5; y++) {
      const dist = Math.abs(x - c.x) + Math.abs(y - c.y);
      if (dist > 0 && dist <= a.castRange) {
        const cell = state.board[x]?.[y];
        if (cell) out.add(`${x},${y}`);
      }
    }
  }
  return out;
}

// Returns the set of engine directions that would actually do something for
// the current intent. Used to gate the on-screen direction picker so the
// player isn't offered moves they can't make / attacks that would whiff.
function validEngineDirsFor(intent: Intent, c: ChampionInstance | null, state: GameStateView): Set<Direction> | undefined {
  if (!c) return undefined;
  if (intent.kind === 'move') {
    if (c.appliedEffects.some(e => e.name === 'Root')) return new Set();
    if (c.currentActionPoints < 1) return new Set();
    const out = new Set<Direction>();
    for (const d of (['UP', 'DOWN', 'LEFT', 'RIGHT'] as Direction[])) {
      const { x, y } = step(c.x, c.y, d);
      if (x < 0 || x >= 5 || y < 0 || y >= 5) continue;
      if (state.board[x]?.[y] === null) out.add(d);
    }
    return out;
  }
  if (intent.kind === 'attack') {
    if (c.appliedEffects.some(e => e.name === 'Disarm')) return new Set();
    if (c.currentActionPoints < 2) return new Set();
    const out = new Set<Direction>();
    for (const d of (['UP', 'DOWN', 'LEFT', 'RIGHT'] as Direction[])) {
      let cx = c.x, cy = c.y;
      for (let i = 0; i < c.attackRange; i++) {
        const next = step(cx, cy, d);
        cx = next.x; cy = next.y;
        if (cx < 0 || cx >= 5 || cy < 0 || cy >= 5) break;
        const cell = state.board[cx]?.[cy];
        if (!cell) continue;
        if (cell.type === 'cover') { out.add(d); break; }
        const target = state.champions.find(z => z.id === cell.id);
        if (!target) continue;
        if (target.ownerIndex === c.ownerIndex) continue; // pass through friendlies
        out.add(d); break;
      }
    }
    return out;
  }
  if (intent.kind === 'castAbility' && intent.area === 'DIRECTIONAL') {
    const a = c.abilities.find(x => x.name === intent.abilityName);
    if (!a) return new Set();
    if (c.mana < a.manaCost) return new Set();
    if (c.currentActionPoints < a.requiredActionPoints) return new Set();
    if (a.currentCooldown > 0) return new Set();
    if (c.appliedEffects.some(e => e.name === 'Silence')) return new Set();
    const isDamaging = a.kind === 'DMG';
    const isHealing = a.kind === 'HEL';
    const isCC = a.kind === 'CC';
    const isDebuff = isCC && a.effectName !== null
      && (['Stun', 'Root', 'Silence', 'Shock', 'Disarm'] as Array<typeof a.effectName>).includes(a.effectName);
    const isBuff = isCC && !isDebuff;
    const out = new Set<Direction>();
    for (const d of (['UP', 'DOWN', 'LEFT', 'RIGHT'] as Direction[])) {
      let cx = c.x, cy = c.y;
      for (let i = 0; i < a.castRange; i++) {
        const next = step(cx, cy, d);
        cx = next.x; cy = next.y;
        if (cx < 0 || cx >= 5 || cy < 0 || cy >= 5) break;
        const cell = state.board[cx]?.[cy];
        if (!cell) continue;
        if (cell.type === 'cover') {
          if (isDamaging) { out.add(d); break; }
          continue;
        }
        const target = state.champions.find(z => z.id === cell.id);
        if (!target) continue;
        const friendly = target.ownerIndex === c.ownerIndex;
        if ((isDamaging && !friendly) || (isHealing && friendly) || (isCC && isDebuff && !friendly) || (isCC && isBuff && friendly)) {
          out.add(d);
          break;
        }
      }
    }
    return out;
  }
  return undefined;
}

function step(x: number, y: number, d: Direction): { x: number; y: number } {
  if (d === 'UP') return { x: x + 1, y };
  if (d === 'DOWN') return { x: x - 1, y };
  if (d === 'LEFT') return { x, y: y - 1 };
  return { x, y: y + 1 };
}

function VictoryScreen({ won, winnerName }: { won: boolean; winnerName: string }) {
  // Pick a grammatical verb based on whether the name is a pronoun.
  const isPronoun = /^(you|i|we|they)$/i.test(winnerName);
  const verb = isPronoun ? 'win' : 'wins';
  const subtitle = won ? 'You won the match' : winnerName ? `${winnerName} ${verb} the match` : 'The match has ended';
  return (
    <div className="h-svh w-screen relative flex items-center justify-center overflow-hidden">
      <motion.div
        initial={{ opacity: 0, scale: 0.6 }} animate={{ opacity: 1, scale: 1 }} transition={{ duration: 0.8 }}
        className="absolute inset-0"
        style={{
          background: won
            ? 'radial-gradient(circle at 50% 50%, rgba(242,193,78,0.35), transparent 65%)'
            : 'radial-gradient(circle at 50% 50%, rgba(179,0,27,0.35), transparent 65%)',
        }}
      />
      {won && Array.from({ length: 14 }).map((_, i) => (
        <motion.div
          key={i}
          initial={{ opacity: 0, y: 0, x: 0, rotate: 0 }}
          animate={{ opacity: [0, 1, 0.9, 0], y: [20, -420], rotate: [0, 360] }}
          transition={{ duration: 2.4, delay: i * 0.06, repeat: Infinity, repeatDelay: 1.2 }}
          className="absolute w-2 h-6"
          style={{
            left: `${10 + (i * 73) % 80}%`,
            bottom: '8%',
            background: i % 2 ? '#f2c14e' : '#b3001b',
          }}
        />
      ))}
      <motion.div
        initial={{ scale: 0.8, opacity: 0 }} animate={{ scale: 1, opacity: 1 }}
        transition={{ type: 'spring', damping: 12, stiffness: 120 }}
        className="relative arcade-frame-gold p-8 text-center"
      >
        <motion.h1
          initial={{ letterSpacing: '0.5em', opacity: 0 }}
          animate={{ letterSpacing: '0.06em', opacity: 1 }}
          transition={{ duration: 0.8 }}
          className={`font-pixel text-4xl ${won ? 'text-muwGold drop-shadow-[0_4px_0_#b3001b]' : 'text-red-400'}`}
        >
          {won ? 'VICTORY' : 'DEFEAT'}
        </motion.h1>
        <p className="mt-4 font-pixel text-[0.78rem] text-gray-200">{subtitle}</p>
        <div className="mt-6 flex items-center justify-center gap-3">
          <a href="/" className="pixel-btn">Menu</a>
        </div>
      </motion.div>
    </div>
  );
}
