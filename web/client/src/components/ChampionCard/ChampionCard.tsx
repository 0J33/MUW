import type { ChampionInstance } from '@muw/shared';
import { EFFECT_DESCRIPTIONS } from '../effects/effectDescriptions.js';

const KIND_LABEL: Record<string, string> = { H: 'Hero', A: 'Anti-Hero', V: 'Villain' };
const KIND_COLOR: Record<string, string> = {
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

export function portraitUrl(name: string, alt = false): string {
  // The original assets are named like "Captain America.png" and "Captain America 2.png"
  // (alt pose). We serve them as-is from /assets.
  const base = name;
  return `/assets/${base}${alt ? ' 2' : ''}.png`;
}

export function ChampionCard({ champ, compact, hover }: { champ: ChampionInstance; compact?: boolean; hover?: boolean }) {
  const hpPct = Math.max(0, Math.min(100, (champ.currentHP / champ.maxHP) * 100));
  // In hover mode the portrait is shown small at the top so the rest of the
  // card (stats / abilities / effects) fits without scrolling.
  if (hover) {
    return (
      <div className="arcade-frame p-2 w-64">
        <div className="flex items-center gap-2 mb-2">
          <div className="relative w-14 h-14 bg-black/40 overflow-hidden flex-shrink-0">
            <img
              src={portraitUrl(champ.name)}
              alt={champ.name}
              className="w-full h-full object-contain pixelart block"
            />
          </div>
          <div className="min-w-0 flex-1">
            <div className="font-pixel text-[0.78rem] text-muwGold truncate leading-tight">{champ.name}</div>
            <div className={`font-pixel text-[0.6rem] mt-1 ${KIND_COLOR[champ.kind] ?? ''}`}>
              {KIND_LABEL[champ.kind] ?? '?'}
            </div>
          </div>
        </div>
        <div className="relative h-3 bg-black/85 overflow-hidden mb-2">
          <div className="absolute inset-y-0 left-0 hp-bar-fill" style={{ width: `${hpPct}%` }} />
          <div className="absolute inset-0 flex items-center justify-center font-pixel text-[0.55rem] text-white drop-shadow-[1px_1px_0_#000]">
            {champ.currentHP} / {champ.maxHP}
          </div>
        </div>
        {renderStatsAndAbilities(champ)}
      </div>
    );
  }
  return (
    <div className={`arcade-frame p-2 ${compact ? 'w-40' : 'w-64'}`}>
      <div className="relative mb-2 bg-black/40 aspect-square w-full overflow-hidden">
        <img
          src={portraitUrl(champ.name)}
          alt={champ.name}
          className="w-full h-full object-contain pixelart block"
        />
        <div className={`absolute top-1 left-1 font-pixel text-[0.7rem] bg-black/80 px-1.5 py-0.5 ${KIND_COLOR[champ.kind] ?? ''}`}>
          {KIND_LABEL[champ.kind] ?? '?'}
        </div>
        <div className="absolute bottom-0 inset-x-0 h-4 bg-black/85 border-t border-black overflow-hidden">
          <div className="absolute inset-y-0 left-0 hp-bar-fill" style={{ width: `${hpPct}%` }} />
          <div className="absolute inset-0 flex items-center justify-center font-pixel text-[0.62rem] text-white drop-shadow-[1px_1px_0_#000]">
            {champ.currentHP} / {champ.maxHP}
          </div>
        </div>
      </div>
      <div className="font-pixel text-[0.85rem] text-muwGold truncate">{champ.name}</div>
      <div className="font-pixel text-[0.62rem] text-gray-300 grid grid-cols-2 gap-x-2 gap-y-1 mt-1">
        <span>Health {champ.currentHP}/{champ.maxHP}</span>
        <span>Mana {champ.mana}</span>
        <span>Actions {champ.currentActionPoints}/{champ.maxActionPointsPerTurn}</span>
        <span>Speed {champ.speed}</span>
        <span>Attack {champ.attackDamage}</span>
        <span>Range {champ.attackRange}</span>
      </div>
      {!compact && (
        <div className="mt-2 space-y-1">
          {champ.abilities.map(a => (
            <div key={a.name} className="font-pixel text-[0.62rem] bg-black/40 px-2 py-1 border border-white/10">
              <div className={`flex justify-between gap-2 ${ABILITY_COLOR[a.kind] ?? 'text-white'}`}>
                <span className="truncate">{a.name}</span>
                <span className="shrink-0">{ABILITY_LABEL[a.kind] ?? a.kind}</span>
              </div>
              <div className="text-gray-300 text-[0.62rem] mt-0.5">
                {a.kind === 'DMG' ? `${a.damageAmount} damage` : a.kind === 'HEL' ? `${a.healAmount} healing` : `Applies ${a.effectName} for ${a.effectDuration} turns`}
              </div>
              <div className="text-gray-400 text-[0.62rem] mt-0.5 leading-relaxed">
                {AREA_LABEL[a.castArea] ?? a.castArea} · Range {a.castRange} · Mana {a.manaCost} · Actions {a.requiredActionPoints}{a.currentCooldown > 0 ? ` · Cooldown ${a.currentCooldown}` : ''}
              </div>
            </div>
          ))}
        </div>
      )}
      {champ.appliedEffects.length > 0 && (
        <div className="pt-2 space-y-1">
          {champ.appliedEffects.map((e, i) => {
            const desc = EFFECT_DESCRIPTIONS[e.name];
            return (
              <div
                key={i}
                className={`px-2 py-1 ${e.type === 'BUFF' ? 'bg-emerald-900/40 border-l-2 border-emerald-500' : 'bg-red-900/40 border-l-2 border-red-500'}`}
              >
                <div className={`flex items-baseline justify-between font-pixel text-[0.62rem] ${e.type === 'BUFF' ? 'text-emerald-200' : 'text-red-200'}`}>
                  <span>{desc?.name ?? e.name}</span>
                  <span className="text-gray-400">{e.duration} {e.duration === 1 ? 'turn' : 'turns'}</span>
                </div>
                {desc && (
                  <div className="font-vt text-[0.85rem] text-gray-300 leading-tight mt-0.5">{desc.description}</div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </div>
  );
}

// Shared stats + abilities + effects block — used by both the compact hover
// card and the regular card body (when the hover mode is on, the layout above
// renders only the header then defers to this helper).
function renderStatsAndAbilities(champ: ChampionInstance) {
  return (
    <>
      <div className="font-pixel text-[0.55rem] text-gray-300 grid grid-cols-2 gap-x-2 gap-y-0.5">
        <span>Health {champ.currentHP}/{champ.maxHP}</span>
        <span>Mana {champ.mana}</span>
        <span>Actions {champ.currentActionPoints}/{champ.maxActionPointsPerTurn}</span>
        <span>Speed {champ.speed}</span>
        <span>Attack {champ.attackDamage}</span>
        <span>Range {champ.attackRange}</span>
      </div>
      <div className="mt-2 space-y-1">
        {champ.abilities.map(a => (
          <div key={a.name} className="font-pixel text-[0.55rem] bg-black/40 px-2 py-1 border border-white/10">
            <div className={`flex justify-between gap-2 ${ABILITY_COLOR[a.kind] ?? 'text-white'}`}>
              <span className="truncate">{a.name}</span>
              <span className="shrink-0">{ABILITY_LABEL[a.kind] ?? a.kind}</span>
            </div>
            <div className="text-gray-300 mt-0.5 leading-tight">
              {a.kind === 'DMG' ? `${a.damageAmount} damage` : a.kind === 'HEL' ? `${a.healAmount} healing` : `${a.effectName} (${a.effectDuration}t)`}
              {' · '}{AREA_LABEL[a.castArea] ?? a.castArea}
              {' · '}Mana {a.manaCost}
              {' · '}Actions {a.requiredActionPoints}
              {a.currentCooldown > 0 ? ` · Cooldown ${a.currentCooldown}` : ''}
            </div>
          </div>
        ))}
      </div>
      {champ.appliedEffects.length > 0 && (
        <div className="mt-2 space-y-1">
          {champ.appliedEffects.map((e, i) => {
            const desc = EFFECT_DESCRIPTIONS[e.name];
            return (
              <div key={i} className={`px-1.5 py-1 ${e.type === 'BUFF' ? 'bg-emerald-900/40 border-l-2 border-emerald-500' : 'bg-red-900/40 border-l-2 border-red-500'}`}>
                <div className={`flex items-baseline justify-between font-pixel text-[0.55rem] ${e.type === 'BUFF' ? 'text-emerald-200' : 'text-red-200'}`}>
                  <span>{desc?.name ?? e.name}</span>
                  <span className="text-gray-400">{e.duration} {e.duration === 1 ? 'turn' : 'turns'}</span>
                </div>
                {desc && (
                  <div className="font-vt text-[0.78rem] text-gray-300 leading-tight mt-0.5">{desc.description}</div>
                )}
              </div>
            );
          })}
        </div>
      )}
    </>
  );
}
