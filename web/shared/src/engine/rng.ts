// Mulberry32 — small, fast, seeded PRNG. Used for cover HP, Dodge rolls, and
// any other randomness so matches are reproducible given a seed.

export function nextRandom(state: number): { value: number; state: number } {
  let t = (state + 0x6d2b79f5) >>> 0;
  t = Math.imul(t ^ (t >>> 15), t | 1);
  t ^= t + Math.imul(t ^ (t >>> 7), t | 61);
  const value = ((t ^ (t >>> 14)) >>> 0) / 4294967296;
  return { value, state: (state + 0x6d2b79f5) >>> 0 };
}

export interface RngCarrier {
  rngState: number;
}

export function rand(carrier: RngCarrier): number {
  const { value, state } = nextRandom(carrier.rngState);
  carrier.rngState = state;
  return value;
}

export function randInt(carrier: RngCarrier, minInclusive: number, maxExclusive: number): number {
  return Math.floor(rand(carrier) * (maxExclusive - minInclusive)) + minInclusive;
}
