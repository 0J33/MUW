// Tiny pixel-art SVG icons. All paths are aligned to whole pixels and use
// `shape-rendering="crispEdges"` so they render hard-edged at any size.
// Sized via the `size` prop (square). Inherit color from `currentColor`.

interface IconProps {
  size?: number;
  className?: string;
}

// 8x8 chunky check mark — a pixel-art tick that matches the Press Start 2P aesthetic.
export function CheckIcon({ size = 16, className }: IconProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 8 8"
      shapeRendering="crispEdges"
      className={className}
      aria-hidden="true"
    >
      <g fill="currentColor">
        <rect x="0" y="4" width="1" height="1" />
        <rect x="1" y="5" width="1" height="1" />
        <rect x="2" y="6" width="1" height="1" />
        <rect x="3" y="5" width="1" height="1" />
        <rect x="4" y="4" width="1" height="1" />
        <rect x="5" y="3" width="1" height="1" />
        <rect x="6" y="2" width="1" height="1" />
        <rect x="7" y="1" width="1" height="1" />
      </g>
    </svg>
  );
}

// 9x9 pixel-art gear — square cog with 4 cardinal teeth and a center hole.
// Drawn at 9x9 so the center pixel is exact and the teeth are symmetric.
export function GearIcon({ size = 18, className }: IconProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 9 9"
      shapeRendering="crispEdges"
      className={className}
      aria-hidden="true"
    >
      <g fill="currentColor">
        {/* teeth (top/bottom/left/right) */}
        <rect x="4" y="0" width="1" height="1" />
        <rect x="4" y="8" width="1" height="1" />
        <rect x="0" y="4" width="1" height="1" />
        <rect x="8" y="4" width="1" height="1" />
        {/* corner teeth */}
        <rect x="1" y="1" width="1" height="1" />
        <rect x="7" y="1" width="1" height="1" />
        <rect x="1" y="7" width="1" height="1" />
        <rect x="7" y="7" width="1" height="1" />
        {/* body ring */}
        <rect x="2" y="2" width="5" height="1" />
        <rect x="2" y="6" width="5" height="1" />
        <rect x="2" y="2" width="1" height="5" />
        <rect x="6" y="2" width="1" height="5" />
        {/* fill behind the hole */}
        <rect x="3" y="3" width="3" height="3" />
      </g>
      {/* center hole — punched out by drawing the page background color */}
      <rect x="4" y="4" width="1" height="1" fill="#0a0d13" />
    </svg>
  );
}

// 7x7 pixel arrow pointing left. The Up / Down / Right variants below reuse
// the same shape via a rotation transform so all four arrows share one glyph.
export function ArrowLeftIcon({ size = 14, className }: IconProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 7 7"
      shapeRendering="crispEdges"
      className={className}
      aria-hidden="true"
    >
      <g fill="currentColor">
        <rect x="0" y="3" width="7" height="1" />
        <rect x="1" y="2" width="1" height="1" />
        <rect x="2" y="1" width="1" height="1" />
        <rect x="3" y="0" width="1" height="1" />
        <rect x="1" y="4" width="1" height="1" />
        <rect x="2" y="5" width="1" height="1" />
        <rect x="3" y="6" width="1" height="1" />
      </g>
    </svg>
  );
}
export function ArrowRightIcon(p: IconProps) {
  return <ArrowLeftIcon {...p} className={`${p.className ?? ''} rotate-180`} />;
}
export function ArrowUpIcon(p: IconProps) {
  return <ArrowLeftIcon {...p} className={`${p.className ?? ''} rotate-90`} />;
}
export function ArrowDownIcon(p: IconProps) {
  return <ArrowLeftIcon {...p} className={`${p.className ?? ''} -rotate-90`} />;
}

// 7x7 pixel "X" — used for the cancel button on the direction picker and any
// other dismiss control.
export function CloseIcon({ size = 12, className }: IconProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 7 7"
      shapeRendering="crispEdges"
      className={className}
      aria-hidden="true"
    >
      <g fill="currentColor">
        <rect x="0" y="0" width="1" height="1" />
        <rect x="6" y="0" width="1" height="1" />
        <rect x="1" y="1" width="1" height="1" />
        <rect x="5" y="1" width="1" height="1" />
        <rect x="2" y="2" width="1" height="1" />
        <rect x="4" y="2" width="1" height="1" />
        <rect x="3" y="3" width="1" height="1" />
        <rect x="2" y="4" width="1" height="1" />
        <rect x="4" y="4" width="1" height="1" />
        <rect x="1" y="5" width="1" height="1" />
        <rect x="5" y="5" width="1" height="1" />
        <rect x="0" y="6" width="1" height="1" />
        <rect x="6" y="6" width="1" height="1" />
      </g>
    </svg>
  );
}

// 7x7 pixel star — used for leader badges if the design needs one.
export function StarIcon({ size = 14, className }: IconProps) {
  return (
    <svg
      width={size}
      height={size}
      viewBox="0 0 7 7"
      shapeRendering="crispEdges"
      className={className}
      aria-hidden="true"
    >
      <g fill="currentColor">
        <rect x="3" y="0" width="1" height="1" />
        <rect x="2" y="1" width="3" height="1" />
        <rect x="0" y="2" width="7" height="1" />
        <rect x="1" y="3" width="5" height="1" />
        <rect x="1" y="4" width="2" height="1" />
        <rect x="4" y="4" width="2" height="1" />
        <rect x="0" y="5" width="2" height="1" />
        <rect x="5" y="5" width="2" height="1" />
      </g>
    </svg>
  );
}
