import type { EffectName } from '@muw/shared';

// Plain-language descriptions of every effect, taken from the original Java
// implementation in Code/MUW (server-wip)/src/model/effects/. Keep these in
// sync with shared/src/engine/effects.ts apply/remove functions.

export interface EffectDescription {
  name: string;
  kind: 'Buff' | 'Debuff';
  description: string;
}

export const EFFECT_DESCRIPTIONS: Record<EffectName, EffectDescription> = {
  Stun: {
    name: 'Stun',
    kind: 'Debuff',
    description: 'The champion is inactive and skips their next turns until the effect expires.',
  },
  Root: {
    name: 'Root',
    kind: 'Debuff',
    description: 'The champion cannot move (abilities and attacks still work).',
  },
  Silence: {
    name: 'Silence',
    kind: 'Debuff',
    description: 'The champion cannot cast abilities while silenced.',
  },
  Shock: {
    name: 'Shock',
    kind: 'Debuff',
    description: 'Speed and attack damage reduced by 10 percent, and the champion loses one action point per turn.',
  },
  Disarm: {
    name: 'Disarm',
    kind: 'Debuff',
    description: 'Cannot perform a regular attack. Gains a weak free ability called Punch (50 damage, 1 action point) instead.',
  },
  Shield: {
    name: 'Shield',
    kind: 'Buff',
    description: 'Blocks the next incoming attack or damaging ability completely. Slight speed boost while up.',
  },
  Dodge: {
    name: 'Dodge',
    kind: 'Buff',
    description: 'Fifty percent chance to dodge any incoming attack. Slight speed boost.',
  },
  PowerUp: {
    name: 'Power Up',
    kind: 'Buff',
    description: 'All damaging and healing abilities are 20 percent stronger.',
  },
  SpeedUp: {
    name: 'Speed Up',
    kind: 'Buff',
    description: 'Speed increases by 15 percent and the champion gains one extra action point per turn.',
  },
  Embrace: {
    name: 'Embrace',
    kind: 'Buff',
    description: 'Instantly heals 20 percent of max health and increases mana, speed, and attack damage by 20 percent.',
  },
};
