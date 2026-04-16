import { useRef, useState } from 'react';
import type { AbilityInstance, ChampionInstance, ChampionKind } from '@muw/shared';
import { LEADER_POWERS } from '../leader/leaderPowers.js';
import { EFFECT_DESCRIPTIONS } from '../effects/effectDescriptions.js';

const ABILITY_LABEL: Record<string, string> = { DMG: 'Damage', HEL: 'Heal', CC: 'Crowd Control' };
const AREA_LABEL: Record<string, string> = {
  SINGLETARGET: 'Single Target',
  TEAMTARGET: 'Team Target',
  DIRECTIONAL: 'Directional',
  SELFTARGET: 'Self',
  SURROUND: 'Surrounding',
};

export interface AbilityTrayProps {
  champion: ChampionInstance | null;
  // Whether the *currently-acting* champion is the player's leader. The
  // Leader Power button is always shown so the layout doesn't jitter — it's
  // just disabled when not on the leader's turn.
  isLeaderTurn: boolean;
  leaderKind: ChampionKind | null; // null if the player has no surviving leader
  leaderAbilityUsed: boolean;
  onAttack: () => void;
  onMove: () => void;
  onCastAbility: (abilityName: string) => void;
  onLeaderAbility: () => void;
  onEndTurn: () => void;
  onSurrender: () => void;
  yourTurn: boolean;
}

export function AbilityTray(props: AbilityTrayProps) {
  const { champion: c, yourTurn, isLeaderTurn, leaderKind, leaderAbilityUsed } = props;
  // Always render the Leader Power button slot so the row doesn't jitter when
  // the active champion changes between leader and non-leader.
  const leaderPower = leaderKind ? LEADER_POWERS[leaderKind] : null;

  return (
    <div className="arcade-frame w-full p-2 flex flex-wrap gap-1.5 items-stretch min-h-[68px]">
      {/* When no champion is active we still render the frame so layout is stable. */}
      <button
        className="pixel-btn pixel-btn-steel"
        onClick={props.onMove}
        disabled={!c || !yourTurn || c.currentActionPoints < 1}
        title="Move 1 square — costs 1 action — keyboard: M"
      >Move <span className="opacity-60">(M)</span></button>
      <button
        className="pixel-btn"
        onClick={props.onAttack}
        disabled={!c || !yourTurn || c.currentActionPoints < 2}
        title="Attack in a direction — costs 2 actions — keyboard: F"
      >Attack <span className="opacity-60">(F)</span></button>
      {/* Always render 3 ability slots (the engine guarantees each champion
          has exactly 3) so the tray width stays consistent across turns. */}
      {[0, 1, 2].map(i => {
        const a = c?.abilities[i];
        if (!a) {
          return (
            <button key={i} className="pixel-btn pixel-btn-steel opacity-30 pointer-events-none" style={{ minWidth: 170 }} disabled>
              <span className="text-[0.62rem]">— ({i + 1})</span>
            </button>
          );
        }
        return (
          <AbilityButton
            key={a.name}
            a={a}
            hotkey={String(i + 1)}
            disabled={!yourTurn}
            onClick={() => { props.onCastAbility(a.name); }}
            championMana={c!.mana}
            championAp={c!.currentActionPoints}
          />
        );
      })}
      {c && c.abilities.length === 0 && null}
      {leaderPower && (() => {
        const status = leaderAbilityUsed
          ? 'Already used'
          : !isLeaderTurn
            ? "On leader's turn"
            : 'Press to activate';
        return (
          <button
            className="pixel-btn pixel-btn-gold flex flex-col items-start gap-0.5"
            style={{ paddingTop: 6, paddingBottom: 6, width: 'clamp(150px, 35vw, 200px)', height: 52 }}
            onClick={props.onLeaderAbility}
            disabled={!yourTurn || !isLeaderTurn || leaderAbilityUsed}
            title={`${leaderPower.name}: ${leaderPower.description} (keyboard: L)`}
          >
            <span className="text-[0.78rem] leading-tight w-full text-left truncate">{leaderPower.name} <span className="opacity-60">(L)</span></span>
            <span className="text-[0.55rem] opacity-90 leading-tight w-full text-left truncate">
              {status}
            </span>
          </button>
        );
      })()}
      <div className="flex-1" />
      <button
        className="pixel-btn pixel-btn-steel"
        onClick={props.onEndTurn}
        disabled={!c || !yourTurn}
        title="End your turn — keyboard: E"
      >End Turn <span className="opacity-60">(E)</span></button>
      <button
        className="pixel-btn"
        style={{ background: '#5a0a0a', boxShadow: '0 3px 0 0 #2a0505, inset 0 -2px 0 rgba(0,0,0,0.25), inset 0 2px 0 rgba(255,255,255,0.1)' }}
        onClick={props.onSurrender}
      >Surrender</button>
    </div>
  );
}

function AbilityButton({ a, hotkey, disabled, onClick, championMana, championAp }: {
  a: AbilityInstance; hotkey: string; disabled: boolean; onClick: () => void; championMana: number; championAp: number;
}) {
  const cantAfford = championMana < a.manaCost || championAp < a.requiredActionPoints || a.currentCooldown > 0;
  const color = a.kind === 'DMG' ? '' : a.kind === 'HEL' ? 'pixel-btn-emerald' : 'pixel-btn-indigo';
  const summary = a.kind === 'DMG' ? `${a.damageAmount} damage`
    : a.kind === 'HEL' ? `${a.healAmount} healing`
    : `${a.effectName} for ${a.effectDuration} turns`;
  // Track hover so we can pop a rich tooltip with the effect description for
  // CC abilities (where the actual game-changing detail lives).
  const [hoverRect, setHoverRect] = useState<DOMRect | null>(null);
  const btnRef = useRef<HTMLButtonElement | null>(null);
  return (
    <>
      <button
        ref={btnRef}
        className={`pixel-btn ${color} flex flex-col items-start gap-0.5`}
        style={{ paddingTop: 6, paddingBottom: 6, minWidth: 'clamp(120px, 28vw, 170px)' }}
        onClick={onClick}
        disabled={disabled || cantAfford}
        onPointerEnter={(ev) => { setHoverRect(ev.currentTarget.getBoundingClientRect()); }}
        onPointerLeave={() => { setHoverRect(null); }}
        onFocus={() => { if (btnRef.current) setHoverRect(btnRef.current.getBoundingClientRect()); }}
        onBlur={() => { setHoverRect(null); }}
      >
        <span className="text-[0.78rem] leading-tight truncate w-full text-left">{a.name} <span className="opacity-60">({hotkey})</span></span>
        <span className="text-[0.62rem] opacity-90 tracking-wider leading-tight w-full text-left truncate">{summary}</span>
        <span className="text-[0.55rem] opacity-70 leading-tight w-full text-left truncate">
          Mana {a.manaCost} · Actions {a.requiredActionPoints}{a.currentCooldown > 0 ? ` · Cooldown ${a.currentCooldown}` : ''}
        </span>
      </button>
      {hoverRect && <AbilityTooltip a={a} rect={hoverRect} />}
    </>
  );
}

function AbilityTooltip({ a, rect }: { a: AbilityInstance; rect: DOMRect }) {
  const TOOLTIP_W = 280;
  const MARGIN = 12;
  const vw = typeof window === 'undefined' ? 1024 : window.innerWidth;

  let left = rect.left + rect.width / 2 - TOOLTIP_W / 2;
  if (left < MARGIN) left = MARGIN;
  if (left + TOOLTIP_W + MARGIN > vw) left = vw - TOOLTIP_W - MARGIN;
  // Prefer above the button so it doesn't cover the player bar / board.
  const bottom = window.innerHeight - rect.top + 8;

  const effect = a.kind === 'CC' && a.effectName ? EFFECT_DESCRIPTIONS[a.effectName] : null;
  const tone = a.kind === 'DMG' ? 'border-red-500/50' : a.kind === 'HEL' ? 'border-emerald-500/50' : 'border-indigo-500/50';
  return (
    <div className={`pointer-events-none fixed z-40 arcade-frame border-2 ${tone} p-3 space-y-1`} style={{ left, bottom, width: TOOLTIP_W }}>
      <div className="font-pixel text-[0.78rem] text-muwGold">{a.name}</div>
      <div className="font-pixel text-[0.6rem] text-gray-400">{ABILITY_LABEL[a.kind] ?? a.kind} · {AREA_LABEL[a.castArea] ?? a.castArea}</div>
      <div className="font-vt text-[0.95rem] text-gray-100 leading-tight">
        {a.kind === 'DMG' && `Deals ${a.damageAmount} damage to the targets.`}
        {a.kind === 'HEL' && `Restores ${a.healAmount} health to the targets.`}
        {a.kind === 'CC'  && `Applies ${effect?.name ?? a.effectName} to the targets for ${a.effectDuration} turns.`}
      </div>
      {effect && (
        <div className={`mt-1 px-2 py-1 ${effect.kind === 'Buff' ? 'bg-emerald-900/40 border-l-2 border-emerald-500' : 'bg-red-900/40 border-l-2 border-red-500'}`}>
          <div className={`font-pixel text-[0.6rem] ${effect.kind === 'Buff' ? 'text-emerald-200' : 'text-red-200'}`}>
            {effect.name} ({effect.kind})
          </div>
          <div className="font-vt text-[0.85rem] text-gray-200 leading-tight mt-0.5">{effect.description}</div>
        </div>
      )}
      <div className="font-pixel text-[0.55rem] text-gray-400 pt-1 border-t border-white/10 leading-relaxed">
        Range {a.castRange} · Mana {a.manaCost} · Actions {a.requiredActionPoints} · Cooldown {a.baseCooldown}
        {a.currentCooldown > 0 ? ` (${a.currentCooldown} left)` : ''}
      </div>
    </div>
  );
}
