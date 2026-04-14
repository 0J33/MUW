import type { ChampionInstance } from './state.js';

// Java PriorityQueue stored items in descending order and peeked/popped the
// tail — a min-priority queue by compareTo. Champion.compareTo returned
// `-1 * (speed - other.speed)` (ties broken by name asc), so "smallest"
// compareTo = highest speed. That means the head of the turn order is the
// fastest champion, which matches the original behavior.

function compareChampions(a: ChampionInstance, b: ChampionInstance): number {
  if (a.speed === b.speed) return a.name.localeCompare(b.name);
  return -1 * (a.speed - b.speed);
}

// Sort champion ids by the original compareTo. Turn order head = smallest
// compareTo = fastest champion.
export function sortTurnOrder(ids: string[], champions: Record<string, ChampionInstance>): string[] {
  const copy = [...ids];
  copy.sort((a, b) => {
    const ca = champions[a];
    const cb = champions[b];
    if (!ca || !cb) return 0;
    return compareChampions(ca, cb);
  });
  return copy;
}
