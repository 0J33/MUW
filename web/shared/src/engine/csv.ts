import type { AbilityInstance, AbilityKind, AreaOfEffect, ChampionKind, EffectName } from './state.js';

// Parsed blueprints — used to spawn fresh instances per champion per match.
export interface AbilityTemplate {
  kind: AbilityKind;
  name: string;
  manaCost: number;
  baseCooldown: number;
  castRange: number;
  castArea: AreaOfEffect;
  requiredActionPoints: number;
  damageAmount: number;
  healAmount: number;
  effectName: EffectName | null;
  effectDuration: number;
}

export interface ChampionTemplate {
  kind: ChampionKind;
  name: string;
  maxHP: number;
  maxMana: number;
  maxActionPointsPerTurn: number;
  speed: number;
  attackRange: number;
  attackDamage: number;
  abilityNames: [string, string, string];
}

// Abilities.csv format (one row per ability):
//   kind, name, manaCost, baseCooldown, requiredActionPoints, castArea,
//   castRange, (damage|heal|effectName), (effectDuration?)
// Fields 2..4 are ints. Field 5 is an AreaOfEffect. Field 6 is int castRange.
// Field 7 is either a damage/heal amount (DMG/HEL) or an effect name (CC).
// Field 8 is an int effect duration for CC, otherwise blank.
// See Game.loadAbilities in Game.java:70-148 for the authoritative format.
export function parseAbilities(csv: string): AbilityTemplate[] {
  const out: AbilityTemplate[] = [];
  for (const rawLine of csv.split(/\r?\n/)) {
    const line = rawLine.trim();
    if (!line) continue;
    const parts = line.split(',');
    const kind = parts[0] as AbilityKind;
    const name = parts[1] ?? '';
    const manaCost = parseInt(parts[2] ?? '0', 10);
    const baseCooldown = parseInt(parts[3] ?? '0', 10);
    const requiredActionPoints = parseInt(parts[4] ?? '0', 10);
    const castArea = parts[5] as AreaOfEffect;
    const castRange = parseInt(parts[6] ?? '0', 10);
    let damageAmount = 0;
    let healAmount = 0;
    let effectName: EffectName | null = null;
    let effectDuration = 0;
    if (kind === 'CC') {
      effectName = (parts[7] ?? '') as EffectName;
      effectDuration = parseInt(parts[8] ?? '0', 10);
    } else if (kind === 'DMG') {
      damageAmount = parseInt(parts[7] ?? '0', 10);
    } else if (kind === 'HEL') {
      healAmount = parseInt(parts[7] ?? '0', 10);
    }
    out.push({
      kind, name, manaCost, baseCooldown, requiredActionPoints,
      castArea, castRange, damageAmount, healAmount, effectName, effectDuration,
    });
  }
  return out;
}

// Champions.csv format (one row per champion):
//   kind, name, maxHP, maxMana, maxActionPointsPerTurn, speed, attackRange,
//   attackDamage, abilityName1, abilityName2, abilityName3
// See Game.loadChampions in Game.java:150-182.
export function parseChampions(csv: string): ChampionTemplate[] {
  const out: ChampionTemplate[] = [];
  for (const rawLine of csv.split(/\r?\n/)) {
    const line = rawLine.trim();
    if (!line) continue;
    const parts = line.split(',');
    out.push({
      kind: parts[0] as ChampionKind,
      name: parts[1] ?? '',
      maxHP: parseInt(parts[2] ?? '0', 10),
      maxMana: parseInt(parts[3] ?? '0', 10),
      maxActionPointsPerTurn: parseInt(parts[4] ?? '0', 10),
      speed: parseInt(parts[5] ?? '0', 10),
      attackRange: parseInt(parts[6] ?? '0', 10),
      attackDamage: parseInt(parts[7] ?? '0', 10),
      abilityNames: [parts[8] ?? '', parts[9] ?? '', parts[10] ?? ''],
    });
  }
  return out;
}

export function newAbilityInstance(template: AbilityTemplate): AbilityInstance {
  return {
    kind: template.kind,
    name: template.name,
    manaCost: template.manaCost,
    baseCooldown: template.baseCooldown,
    currentCooldown: 0,
    castRange: template.castRange,
    castArea: template.castArea,
    requiredActionPoints: template.requiredActionPoints,
    damageAmount: template.damageAmount,
    healAmount: template.healAmount,
    effectName: template.effectName,
    effectDuration: template.effectDuration,
  };
}
