import type { ChampionInstance } from '@muw/shared';
import { portraitUrl } from '../ChampionCard/ChampionCard.js';

// Compact 3-col grid so all 6 turn-order portraits fit in the narrow sidebar
// at every size — no horizontal scrolling needed.
export function TurnTracker({
  turnOrder, champions, currentId, leaderIds,
}: {
  turnOrder: string[];
  champions: ChampionInstance[];
  currentId: string | null;
  leaderIds?: Set<string>;
}) {
  const byId = new Map<string, ChampionInstance>();
  for (const c of champions) byId.set(c.id, c);
  return (
    <div className="grid grid-cols-3 gap-1.5">
      {turnOrder.map((id, i) => {
        const c = byId.get(id);
        if (!c) return null;
        const isCurrent = id === currentId;
        const isLeader = leaderIds?.has(id);
        const who = c.ownerIndex === 0 ? 'border-blue-400' : 'border-red-400';
        return (
          <div key={id} className={`flex flex-col items-center ${isCurrent ? '' : 'opacity-60'}`}>
            <div className={`relative w-full aspect-square overflow-hidden border-2 ${isCurrent ? 'border-muwGold' : who} bg-black`}>
              <img src={portraitUrl(c.name)} alt={c.name} className="w-full h-full object-contain pixelart" />
              {isLeader && (
                <div className="absolute top-0 left-0 right-0 bg-muwGold text-muwInk font-pixel text-[0.5rem] text-center leading-none px-0.5 py-px">
                  Leader
                </div>
              )}
            </div>
            <span className="font-pixel text-[0.5rem] mt-0.5 text-gray-300 truncate w-full text-center">
              {i === 0 ? 'Now' : `+${i}`}
            </span>
          </div>
        );
      })}
    </div>
  );
}
