import { useEffect, useState } from 'react';
import { parseAbilities, parseChampions, type AbilityTemplate, type ChampionTemplate, TEAM_SIZE } from '@muw/shared';
import { portraitUrl } from '../ChampionCard/ChampionCard.js';
import { CheckIcon } from '../icons/Icons.js';
import { LEADER_POWERS } from '../leader/leaderPowers.js';

let cachedC: ChampionTemplate[] | null = null;
let cachedA: AbilityTemplate[] | null = null;

export async function loadChampionTemplates(): Promise<ChampionTemplate[]> {
  if (cachedC) return cachedC;
  const csv = await fetch('/assets/Champions.csv').then(r => r.text());
  cachedC = parseChampions(csv);
  return cachedC;
}
async function loadAbilityTemplates(): Promise<AbilityTemplate[]> {
  if (cachedA) return cachedA;
  const csv = await fetch('/assets/Abilities.csv').then(r => r.text());
  cachedA = parseAbilities(csv);
  return cachedA;
}

const KIND_BORDER: Record<string, string> = {
  H: 'ring-blue-400',
  A: 'ring-purple-400',
  V: 'ring-red-400',
};
const KIND_FULL: Record<string, string> = { H: 'Hero', A: 'Anti-Hero', V: 'Villain' };
const KIND_TINT: Record<string, string> = {
  H: 'text-blue-300',
  A: 'text-purple-300',
  V: 'text-red-300',
};
const ABILITY_COLOR: Record<string, string> = {
  DMG: 'text-red-300',
  HEL: 'text-emerald-300',
  CC:  'text-indigo-300',
};
const ABILITY_LABEL: Record<string, string> = { DMG: 'Damage', HEL: 'Heal', CC: 'Crowd Control' };
const AREA_LABEL: Record<string, string> = {
  SINGLETARGET: 'Single Target',
  TEAMTARGET: 'Team Target',
  DIRECTIONAL: 'Directional',
  SELFTARGET: 'Self',
  SURROUND: 'Surrounding',
};

export interface TeamSelectProps {
  myPicks: string[];
  myLeader: string | null;
  opponentPicks: string[];
  locked: boolean;
  onPick: (name: string) => void;
  onUnpick: (name: string) => void;
  onSetLeader: (name: string) => void;
}

export function TeamSelect(props: TeamSelectProps) {
  const [roster, setRoster] = useState<ChampionTemplate[]>([]);
  const [abilities, setAbilities] = useState<AbilityTemplate[]>([]);
  const [hover, setHover] = useState<ChampionTemplate | null>(null);
  useEffect(() => { void loadChampionTemplates().then(setRoster); }, []);
  useEffect(() => { void loadAbilityTemplates().then(setAbilities); }, []);
  const pickedByOpponent = new Set(props.opponentPicks);
  const pickedByMe = new Set(props.myPicks);

  const previewChamp = hover
    ?? roster.find(c => pickedByMe.has(c.name))
    ?? roster[0]
    ?? null;

  return (
    <div className="flex flex-col gap-3">
      <div className="flex items-baseline justify-between flex-wrap gap-2">
        <h2 className="font-pixel text-sm text-muwGold">Pick {TEAM_SIZE} Champions</h2>
        <span className="font-pixel text-[0.62rem] text-gray-400">
          Click to pick · Click again to set as leader · Right-click to remove
        </span>
      </div>

      <div className="grid md:grid-cols-[minmax(0,1fr)_300px] lg:grid-cols-[minmax(0,1fr)_340px] gap-4 items-start">
        {/* Auto-fill grid; tiles are wide enough for "Quicksilver" to sit on
            a single line of the pixel font without wrapping. */}
        <div className="grid gap-3" style={{ gridTemplateColumns: 'repeat(auto-fill, minmax(168px, 1fr))' }}>
          {roster.map(c => {
            const taken = pickedByOpponent.has(c.name);
            const mine = pickedByMe.has(c.name);
            const isLeader = props.myLeader === c.name;
            const disabled = props.locked || taken || (!mine && pickedByMe.size >= TEAM_SIZE);
            return (
              <button
                key={c.name}
                className={`
                  relative arcade-frame p-3 transition group
                  ${isLeader ? 'arcade-frame-leader' : mine ? 'arcade-frame-picked' : ''}
                  ${disabled && !mine ? 'opacity-40 cursor-not-allowed' : 'hover:-translate-y-0.5 hover:brightness-110'}
                `}
                onClick={() => {
                  if (props.locked) return;
                  if (mine) props.onSetLeader(c.name);
                  else if (!taken && pickedByMe.size < TEAM_SIZE) props.onPick(c.name);
                }}
                onContextMenu={(e) => { e.preventDefault(); if (mine) props.onUnpick(c.name); }}
                onPointerEnter={() => { setHover(c); }}
                disabled={disabled && !mine}
                title={c.name}
              >
                <div className="aspect-square w-full bg-black/50 overflow-hidden">
                  <img src={portraitUrl(c.name)} alt={c.name} className="w-full h-full object-contain pixelart block" />
                </div>
                {/* Picked indicator — bold corner badge; replaced by Leader badge if also leader. */}
                {mine && !isLeader && (
                  <div className="absolute top-1.5 left-1.5 bg-muwGold text-muwInk w-5 h-5 flex items-center justify-center">
                    <CheckIcon size={14} />
                  </div>
                )}
                {isLeader && (
                  <div className="absolute top-1.5 left-1.5 right-1.5 bg-muwGold text-muwInk font-pixel text-[0.62rem] font-bold px-1.5 py-0.5 text-center">
                    Leader
                  </div>
                )}
                {taken && <div className="absolute inset-3 bg-red-900/85 flex items-center justify-center font-pixel text-[0.7rem]">Taken</div>}
                {/* Allow long single-word names ("Quicksilver") to wrap mid-word
                    instead of being clipped, and shrink one-letter for breathing
                    room. The reserved minHeight keeps every tile the same size
                    whether the name fits in one or two lines. */}
                <div
                  className={`font-pixel text-[0.6rem] mt-2 text-center leading-tight break-words ${mine ? 'text-muwGold' : 'text-gray-200'}`}
                  style={{ minHeight: '2.4em', overflowWrap: 'anywhere', wordBreak: 'break-word' }}
                >
                  {c.name}
                </div>
              </button>
            );
          })}
        </div>

        <div className="md:sticky md:top-0">
          {previewChamp && <PreviewPanel c={previewChamp} abilities={abilities} />}
        </div>
      </div>
    </div>
  );
}

function PreviewPanel({ c, abilities }: { c: ChampionTemplate; abilities: AbilityTemplate[] }) {
  return (
    <div className="arcade-frame p-3 flex flex-col gap-2.5">
      <div className="flex items-center gap-3">
        <div className="w-16 h-16 arcade-frame p-1.5 flex-shrink-0 bg-black/40">
          <img src={portraitUrl(c.name)} alt={c.name} className="w-full h-full object-contain pixelart block" />
        </div>
        <div className="min-w-0 flex-1">
          <div className="font-pixel text-[0.85rem] text-muwGold truncate leading-tight">{c.name}</div>
          <div className={`font-pixel text-[0.62rem] mt-1 ${KIND_TINT[c.kind]}`}>{KIND_FULL[c.kind]}</div>
        </div>
      </div>

      <div className="grid grid-cols-2 gap-1 font-pixel text-[0.62rem]">
        <Stat label="Health"  value={c.maxHP} />
        <Stat label="Mana"    value={c.maxMana} />
        <Stat label="Actions" value={c.maxActionPointsPerTurn} />
        <Stat label="Speed"   value={c.speed} />
        <Stat label="Attack"  value={c.attackDamage} />
        <Stat label="Range"   value={c.attackRange} />
      </div>

      <div>
        <div className="font-pixel text-[0.62rem] text-muwGold mb-1">Abilities</div>
        <div className="space-y-1">
          {c.abilityNames.map(name => {
            const a = abilities.find(x => x.name === name);
            if (!a) return (
              <div key={name} className="bg-black/40 border border-white/10 px-2 py-1 font-vt text-xs">
                {name} <span className="text-gray-500">(loading)</span>
              </div>
            );
            return <AbilityLine key={name} a={a} />;
          })}
        </div>
      </div>

      {/* Leader power for this champion's kind. Same power for every Hero /
          Anti-Hero / Villain — matters once you make them your leader. */}
      <div>
        <div className="font-pixel text-[0.62rem] text-muwGold mb-1">If made Leader</div>
        <div className="bg-black/50 border border-muwGold/40 px-2 py-1.5">
          <div className="font-pixel text-[0.7rem] text-muwGold">{LEADER_POWERS[c.kind].name}</div>
          <div className="font-vt text-[0.85rem] text-gray-300 leading-tight mt-1">{LEADER_POWERS[c.kind].description}</div>
        </div>
      </div>
    </div>
  );
}

function Stat({ label, value }: { label: string; value: number }) {
  return (
    <div className="bg-black/40 border border-muwSteelLight px-1.5 py-1 flex items-baseline justify-between gap-2">
      <span className="text-gray-400 truncate">{label}</span>
      <span className="text-white">{value}</span>
    </div>
  );
}

function AbilityLine({ a }: { a: AbilityTemplate }) {
  const amount = a.kind === 'DMG' ? `${a.damageAmount} damage`
    : a.kind === 'HEL' ? `${a.healAmount} healing`
    : `Applies ${a.effectName} for ${a.effectDuration} turns`;
  return (
    <div className="bg-black/50 border border-muwSteelLight px-2 py-1.5">
      <div className="flex items-baseline justify-between gap-2">
        <span className={`font-pixel text-[0.7rem] ${ABILITY_COLOR[a.kind]} truncate`}>{a.name}</span>
        <span className={`font-pixel text-[0.55rem] ${ABILITY_COLOR[a.kind]} shrink-0`}>{ABILITY_LABEL[a.kind]}</span>
      </div>
      <div className="font-pixel text-[0.55rem] text-gray-300 mt-1 leading-relaxed">{amount}</div>
      <div className="font-pixel text-[0.55rem] text-gray-400 mt-1 leading-relaxed">
        {AREA_LABEL[a.castArea] ?? a.castArea} · Range {a.castRange} · Mana {a.manaCost} · Actions {a.requiredActionPoints} · Cooldown {a.baseCooldown}
      </div>
    </div>
  );
}
