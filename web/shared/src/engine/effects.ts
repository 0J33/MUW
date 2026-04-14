import type { AbilityInstance, ChampionInstance, EffectInstance, EffectName, EffectType } from './state.js';

// Preserves the Java integer-division quirks (Shield/Shock/Dodge/Embrace/SpeedUp
// have a "round to nearest 5" correction on remove so stat drift from repeated
// integer division doesn't accumulate). Keeping this 1:1 with the original.

const DEBUFF_NAMES = new Set<EffectName>(['Stun', 'Root', 'Silence', 'Shock', 'Disarm']);
const BUFF_NAMES = new Set<EffectName>(['Shield', 'Dodge', 'PowerUp', 'SpeedUp', 'Embrace']);

export function effectTypeOf(name: EffectName): EffectType {
  if (DEBUFF_NAMES.has(name)) return 'DEBUFF';
  if (BUFF_NAMES.has(name)) return 'BUFF';
  throw new Error(`Unknown effect: ${name}`);
}

export function makeEffect(name: EffectName, duration: number): EffectInstance {
  return { name, type: effectTypeOf(name), duration };
}

function roundTo5(v: number): number {
  return Math.floor((v + 2) / 5) * 5;
}

export function applyEffect(c: ChampionInstance, e: EffectInstance): void {
  switch (e.name) {
    case 'Stun':
      c.condition = 'INACTIVE';
      return;
    case 'Root':
      if (c.condition !== 'INACTIVE') c.condition = 'ROOTED';
      return;
    case 'Silence':
      c.currentActionPoints = c.currentActionPoints + 2;
      c.maxActionPointsPerTurn = c.maxActionPointsPerTurn + 2;
      clampAp(c);
      return;
    case 'Shock':
      c.speed = Math.trunc(c.speed * 0.9);
      c.attackDamage = Math.trunc(c.attackDamage * 0.9);
      c.currentActionPoints = c.currentActionPoints - 1;
      c.maxActionPointsPerTurn = c.maxActionPointsPerTurn - 1;
      clampAp(c);
      return;
    case 'Disarm': {
      // Add a free fallback "Punch" ability with no cost (Java Disarm adds a
      // DamagingAbility named "Punch" to the champion's ability list).
      const punch: AbilityInstance = {
        kind: 'DMG',
        name: 'Punch',
        manaCost: 0,
        baseCooldown: 1,
        currentCooldown: 0,
        castRange: 1,
        castArea: 'SINGLETARGET',
        requiredActionPoints: 1,
        damageAmount: 50,
        healAmount: 0,
        effectName: null,
        effectDuration: 0,
      };
      c.abilities.push(punch);
      return;
    }
    case 'Shield':
      c.speed = Math.trunc(c.speed * 1.02);
      return;
    case 'Dodge':
      c.speed = Math.trunc(c.speed * 1.05);
      return;
    case 'Embrace':
      c.currentHP = Math.min(c.maxHP, Math.trunc(c.maxHP * 0.2) + c.currentHP);
      c.mana = Math.trunc(c.mana * 1.2);
      c.speed = Math.trunc(c.speed * 1.2);
      c.attackDamage = Math.trunc(c.attackDamage * 1.2);
      return;
    case 'SpeedUp':
      c.speed = Math.trunc(c.speed * 1.15);
      c.currentActionPoints = c.currentActionPoints + 1;
      c.maxActionPointsPerTurn = c.maxActionPointsPerTurn + 1;
      clampAp(c);
      return;
    case 'PowerUp':
      for (const a of c.abilities) {
        if (a.kind === 'HEL') a.healAmount = Math.trunc(a.healAmount * 1.2);
        else if (a.kind === 'DMG') a.damageAmount = Math.trunc(a.damageAmount * 1.2);
      }
      return;
  }
}

export function removeEffect(c: ChampionInstance, e: EffectInstance): void {
  switch (e.name) {
    case 'Stun': {
      // Re-evaluate condition: still stunned by another Stun? rooted?
      const stillStun = c.appliedEffects.some(x => x.name === 'Stun');
      const stillRoot = c.appliedEffects.some(x => x.name === 'Root');
      if (stillStun) c.condition = 'INACTIVE';
      else if (stillRoot) c.condition = 'ROOTED';
      else c.condition = 'ACTIVE';
      return;
    }
    case 'Root': {
      const stillRoot = c.appliedEffects.some(x => x.name === 'Root');
      if (c.condition !== 'INACTIVE' && !stillRoot) c.condition = 'ACTIVE';
      return;
    }
    case 'Silence':
      c.currentActionPoints = c.currentActionPoints - 2;
      c.maxActionPointsPerTurn = c.maxActionPointsPerTurn - 2;
      clampAp(c);
      return;
    case 'Shock':
      c.speed = Math.trunc(c.speed / 0.9);
      c.attackDamage = Math.trunc(c.attackDamage / 0.9);
      c.currentActionPoints = c.currentActionPoints + 1;
      c.maxActionPointsPerTurn = c.maxActionPointsPerTurn + 1;
      c.speed = roundTo5(c.speed);
      c.attackDamage = roundTo5(c.attackDamage);
      clampAp(c);
      return;
    case 'Disarm':
      for (let i = 0; i < c.abilities.length; i++) {
        if (c.abilities[i]!.name === 'Punch') {
          c.abilities.splice(i, 1);
          break;
        }
      }
      return;
    case 'Shield':
      c.speed = Math.trunc(c.speed / 1.02);
      c.speed = roundTo5(c.speed);
      return;
    case 'Dodge':
      c.speed = Math.trunc(c.speed / 1.05);
      c.speed = roundTo5(c.speed);
      return;
    case 'Embrace':
      c.speed = Math.trunc(c.speed / 1.2);
      c.attackDamage = Math.trunc(c.attackDamage / 1.2);
      c.speed = roundTo5(c.speed);
      c.attackDamage = roundTo5(c.attackDamage);
      return;
    case 'SpeedUp':
      c.speed = Math.trunc(c.speed / 1.15);
      c.currentActionPoints = c.currentActionPoints - 1;
      c.maxActionPointsPerTurn = c.maxActionPointsPerTurn - 1;
      c.speed = roundTo5(c.speed);
      clampAp(c);
      return;
    case 'PowerUp':
      for (const a of c.abilities) {
        if (a.kind === 'HEL') a.healAmount = Math.trunc(a.healAmount / 1.2);
        else if (a.kind === 'DMG') a.damageAmount = Math.trunc(a.damageAmount / 1.2);
      }
      return;
  }
}

function clampAp(c: ChampionInstance): void {
  if (c.currentActionPoints < 0) c.currentActionPoints = 0;
  if (c.currentActionPoints > c.maxActionPointsPerTurn) c.currentActionPoints = c.maxActionPointsPerTurn;
}

export function hasEffect(c: ChampionInstance, name: EffectName): boolean {
  return c.appliedEffects.some(e => e.name === name);
}

// Tick effect durations down by 1; remove any that reach 0 (calling their
// remove side effect). Mirrors Game.updateTimers.
export function tickEffects(c: ChampionInstance): void {
  let i = 0;
  while (i < c.appliedEffects.length) {
    const e = c.appliedEffects[i]!;
    e.duration -= 1;
    if (e.duration === 0) {
      c.appliedEffects.splice(i, 1);
      removeEffect(c, e);
    } else {
      i += 1;
    }
  }
}

// Tick cooldowns down by 1 (min 0). Mirrors Game.updateTimers second loop.
export function tickCooldowns(c: ChampionInstance): void {
  for (const a of c.abilities) {
    if (a.currentCooldown > 0) a.currentCooldown -= 1;
  }
}
