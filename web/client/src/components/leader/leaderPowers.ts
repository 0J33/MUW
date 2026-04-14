import type { ChampionKind } from '@muw/shared';

export interface LeaderPower {
  name: string;
  description: string;
}

// One-shot per-match power that fires when the leader's champion is on turn.
// Behavior comes from the engine (Game.useLeaderAbility); these strings are
// the user-facing copy. Keep them in sync with shared/src/engine/moves.ts
// applyLeaderAbility.
export const LEADER_POWERS: Record<ChampionKind, LeaderPower> = {
  H: {
    name: 'Embrace',
    description: 'Removes every debuff from your team, then applies Embrace for 2 turns — heals 20 percent max health and boosts mana, speed, and attack.',
  },
  A: {
    name: 'Disorder',
    description: 'Stuns every non-leader champion on both sides for 2 turns. They lose their next two turns entirely.',
  },
  V: {
    name: 'Execute',
    description: 'Instantly knocks out every enemy champion sitting below 30 percent health.',
  },
};
