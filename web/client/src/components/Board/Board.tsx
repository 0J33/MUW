import { useState } from 'react';
import { motion } from 'framer-motion';
import type { Cell, ChampionInstance, Direction, GameStateView } from '@muw/shared';
import { ChampionCard, portraitUrl } from '../ChampionCard/ChampionCard.js';
import { ArrowDownIcon, ArrowLeftIcon, ArrowRightIcon, ArrowUpIcon, CloseIcon, StarIcon } from '../icons/Icons.js';
import { EFFECT_DESCRIPTIONS } from '../effects/effectDescriptions.js';

export interface BoardProps {
  state: GameStateView;
  viewerIndex: 0 | 1;
  selectableCells?: Set<string>; // "x,y" keys of cells that should be highlighted as targets
  onCellClick?: (x: number, y: number) => void;
  // When set, the on-screen direction picker is shown above/around the
  // current champion. The arrow click sends the engine direction (already
  // remapped per viewer perspective) back via this callback. `validEngineDirs`,
  // when provided, hides any arrow whose engine direction isn't in the set,
  // so the player can never queue an obviously invalid move/attack/cast.
  directionPicker?: {
    championId: string;
    onPick: (engineDir: Direction) => void;
    onCancel: () => void;
    validEngineDirs?: Set<Direction>;
  } | null;
  // Champion ids that should render with a "Leader" overlay so it's obvious
  // mid-match which unit holds the team's leader power.
  leaderIds?: Set<string>;
}

const COVER_IMG = '/assets/metal box 3.png';

export function Board({ state, viewerIndex, selectableCells, onCellClick, directionPicker, leaderIds }: BoardProps) {
  const [hover, setHover] = useState<{ champ: ChampionInstance; rect: DOMRect } | null>(null);
  const currentId = state.currentChampionId;
  const champById = new Map<string, ChampionInstance>();
  for (const c of state.champions) champById.set(c.id, c);

  // Render rows so the viewer's team appears at the bottom of the screen.
  // Player 0's team lives at engine x=0 and Player 1's at x=4. For viewer 0
  // we reverse, so x=0 ends up at the bottom render row.
  const rowOrder = viewerIndex === 0
    ? state.board.map((_, i) => state.board.length - 1 - i)
    : state.board.map((_, i) => i);

  // Visual → engine direction mapping (so a click on the on-screen UP arrow
  // moves the champion up the screen regardless of their seat).
  const visualToEngine: Record<'UP' | 'DOWN' | 'LEFT' | 'RIGHT', Direction> = viewerIndex === 0
    ? { UP: 'UP', DOWN: 'DOWN', LEFT: 'LEFT', RIGHT: 'RIGHT' }
    : { UP: 'DOWN', DOWN: 'UP', LEFT: 'LEFT', RIGHT: 'RIGHT' };

  const pickerChamp = directionPicker
    ? state.champions.find(c => c.id === directionPicker.championId) ?? null
    : null;

  return (
    <div className="relative w-full h-full flex items-center justify-center">
      <div className="grid grid-cols-5 gap-3 w-full aspect-square max-w-full max-h-full relative">
        {rowOrder.map(x => state.board[x]!.map((cell, y) => {
          const key = `${x},${y}`;
          const selectable = selectableCells?.has(key);
          const classes = ['cell', 'flex items-center justify-center'];
          if (selectable) classes.push('valid-target');
          const champ = cell?.type === 'champion' ? champById.get(cell.id) : null;
          const isCurrent = champ && champ.id === currentId;
          if (isCurrent) classes.push('active-turn');
          return (
            <motion.div
              key={key}
              layout
              whileHover={selectable ? { scale: 1.05 } : { scale: 1.02 }}
              className={classes.join(' ')}
              onClick={() => onCellClick?.(x, y)}
              onPointerEnter={(ev) => { if (champ) setHover({ champ, rect: ev.currentTarget.getBoundingClientRect() }); }}
              onPointerLeave={() => { setHover(h => (h && h.champ.id === champ?.id ? null : h)); }}
            >
              {renderCell(cell, champById, !!isCurrent, leaderIds)}
            </motion.div>
          );
        }))}

        {/* On-screen direction picker — overlaid on the cell of the current champion. */}
        {pickerChamp && directionPicker && (
          <DirectionPicker
            renderRow={viewerIndex === 0 ? state.board.length - 1 - pickerChamp.x : pickerChamp.x}
            renderCol={pickerChamp.y}
            onPick={(visualDir) => { directionPicker.onPick(visualToEngine[visualDir]); }}
            onCancel={directionPicker.onCancel}
            validVisualDirs={directionPicker.validEngineDirs && new Set(
              (Object.keys(visualToEngine) as Array<'UP' | 'DOWN' | 'LEFT' | 'RIGHT'>)
                .filter(v => directionPicker.validEngineDirs!.has(visualToEngine[v]))
            )}
          />
        )}
      </div>

      {hover && <HoverCard champ={hover.champ} rect={hover.rect} />}
    </div>
  );
}

// Floating champion-info card that anchors to the hovered cell using fixed
// positioning. We try to keep the card vertically near the cell, and only
// push it up (or down) when it would otherwise spill past the viewport edge.
// No internal scrolling — the hover variant of ChampionCard uses a small
// portrait + dense layout so the full info fits in roughly 320 px.
function HoverCard({ champ, rect }: { champ: ChampionInstance; rect: DOMRect }) {
  const CARD_W = 256;
  const CARD_H_EST = 340; // header + hp bar + 6 stats + 3 abilities + ~2 effects
  const MARGIN = 12;
  const vw = typeof window === 'undefined' ? 1024 : window.innerWidth;
  const vh = typeof window === 'undefined' ? 768 : window.innerHeight;

  // Horizontal: prefer placing to the right of the cell; flip left if needed;
  // clamp to left margin if even that doesn't fit (very narrow viewport).
  let left = rect.right + 12;
  if (left + CARD_W + MARGIN > vw) left = rect.left - CARD_W - 12;
  if (left < MARGIN) left = MARGIN;

  // Vertical: try to center the card on the cell. If centered position would
  // overflow the bottom, pin the bottom of the card to the viewport. If it
  // would overflow the top, pin the top. Otherwise leave it next to the cell.
  const cellMidY = rect.top + rect.height / 2;
  let top = cellMidY - CARD_H_EST / 2;
  if (top + CARD_H_EST > vh - MARGIN) top = vh - MARGIN - CARD_H_EST;
  if (top < MARGIN) top = MARGIN;

  return (
    <div className="pointer-events-none fixed z-30" style={{ left, top }}>
      <ChampionCard champ={champ} hover />
    </div>
  );
}

function renderCell(cell: Cell, champById: Map<string, ChampionInstance>, isCurrent: boolean, leaderIds?: Set<string>) {
  if (!cell) return null;
  if (cell.type === 'cover') {
    return (
      <div className="relative w-full h-full p-1 bg-black/30">
        <img src={COVER_IMG} alt="Cover" className="w-full h-full object-contain pixelart block" />
        <div className="absolute bottom-0 inset-x-0 font-pixel text-[0.62rem] text-center bg-black/75 text-gray-200 py-0.5">
          {cell.hp}
        </div>
      </div>
    );
  }
  const c = champById.get(cell.id);
  if (!c) return null;
  const pct = Math.max(0, Math.min(100, (c.currentHP / c.maxHP) * 100));
  const isKod = c.currentHP === 0 || c.condition === 'KNOCKEDOUT';
  const isLeader = leaderIds?.has(c.id);
  return (
    <motion.div
      key={c.id}
      layout
      className={`relative w-full h-full ${isLeader ? 'ring-2 ring-muwGold ring-inset' : ''}`}
      initial={{ scale: 0.92, opacity: 0 }}
      animate={{ scale: isKod ? 0.85 : 1, opacity: isKod ? 0.4 : 1 }}
      transition={{ type: 'spring', stiffness: 220, damping: 22 }}
    >
      {/* Idle bob — subtle vertical breathing for the active champion only.
          Filter pulse was distracting; a calm 1px bob reads better. */}
      <motion.img
        src={portraitUrl(c.name)}
        alt={c.name}
        className="absolute inset-0 w-full h-full object-contain pixelart"
        animate={isCurrent ? { y: [0, -2, 0] } : { y: 0 }}
        transition={isCurrent ? { duration: 1.6, repeat: Infinity, ease: 'easeInOut' } : { duration: 0.2 }}
      />
      {/* Top strip — leader marker on the left, AP count on the right.
          Champion name lives in tooltip / sidebar / player bar — keeping it
          off the cell so portraits read cleanly. */}
      <div className="absolute top-0 left-0 right-0 flex justify-between items-center px-1 py-0.5 font-pixel text-[0.62rem] bg-black/75 border-b border-black">
        <span className="flex items-center gap-1">
          {isLeader && <span className="text-muwGold inline-flex" aria-label="Leader"><StarIcon size={9} /></span>}
        </span>
        <span className="text-muwGold">{c.currentActionPoints}</span>
      </div>
      <div className="absolute bottom-0 inset-x-0 h-3 bg-black/80 border-t border-black overflow-hidden">
        <motion.div className="absolute inset-y-0 left-0 hp-bar-fill" animate={{ width: `${pct}%` }} transition={{ duration: 0.35, ease: 'easeOut' }} />
        <div className="absolute inset-0 flex items-center justify-center font-pixel text-[0.5rem] text-white drop-shadow-[1px_1px_0_#000]">
          {c.currentHP}/{c.maxHP}
        </div>
      </div>
      {c.appliedEffects.length > 0 && (
        <div className="absolute top-[14px] right-0.5 flex flex-col gap-0.5 items-end">
          {c.appliedEffects.slice(0, 3).map((e, i) => {
            const desc = EFFECT_DESCRIPTIONS[e.name];
            const tooltip = desc ? `${desc.name} (${e.duration} turns) — ${desc.description}` : `${e.name} (${e.duration} turns)`;
            return (
              <motion.span
                key={`${e.name}-${i}`}
                initial={{ scale: 0.3, opacity: 0 }}
                animate={{ scale: 1, opacity: 1 }}
                className={`font-pixel text-[0.55rem] px-1 ${e.type === 'BUFF' ? 'bg-emerald-700/90' : 'bg-red-800/90'}`}
                title={tooltip}
              >
                {desc?.name ?? e.name}
              </motion.span>
            );
          })}
        </div>
      )}
    </motion.div>
  );
}

// Floating arrow controls positioned around the current champion's cell. Each
// arrow click reports a *visual* direction; the parent translates that to the
// underlying engine direction based on the viewer's seat. `validVisualDirs`,
// if provided, restricts which arrows render (so we don't surface buttons for
// obviously invalid moves / attacks / directional casts).
function DirectionPicker({
  renderRow, renderCol, onPick, onCancel, validVisualDirs,
}: {
  renderRow: number;
  renderCol: number;
  onPick: (dir: 'UP' | 'DOWN' | 'LEFT' | 'RIGHT') => void;
  onCancel: () => void;
  validVisualDirs?: Set<'UP' | 'DOWN' | 'LEFT' | 'RIGHT'>;
}) {
  const cx = (renderCol + 0.5) * 20; // %
  const cy = (renderRow + 0.5) * 20; // %
  const off = 14; // % offset from cell center to where arrows sit
  const styleAt = (dx: number, dy: number): React.CSSProperties => ({
    position: 'absolute',
    left: `${cx + dx}%`,
    top: `${cy + dy}%`,
    transform: 'translate(-50%, -50%)',
    zIndex: 30,
  });
  const show = (d: 'UP' | 'DOWN' | 'LEFT' | 'RIGHT') => !validVisualDirs || validVisualDirs.has(d);
  return (
    <>
      {show('UP') && (
        <button onClick={() => { onPick('UP'); }} style={styleAt(0, -off)} className="dir-btn" aria-label="Up">
          <ArrowUpIcon size={20} />
        </button>
      )}
      {show('DOWN') && (
        <button onClick={() => { onPick('DOWN'); }} style={styleAt(0, +off)} className="dir-btn" aria-label="Down">
          <ArrowDownIcon size={20} />
        </button>
      )}
      {show('LEFT') && (
        <button onClick={() => { onPick('LEFT'); }} style={styleAt(-off, 0)} className="dir-btn" aria-label="Left">
          <ArrowLeftIcon size={20} />
        </button>
      )}
      {show('RIGHT') && (
        <button onClick={() => { onPick('RIGHT'); }} style={styleAt(+off, 0)} className="dir-btn" aria-label="Right">
          <ArrowRightIcon size={20} />
        </button>
      )}
      <button onClick={onCancel} style={styleAt(0, 0)} className="dir-cancel" aria-label="Cancel">
        <CloseIcon size={14} />
      </button>
    </>
  );
}
