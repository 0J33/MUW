# Marvel Ultimate War

Turn-based 5×5 tactical combat. Originally a Java Swing uni project (CSEN401); now also a React + socket.io web port that runs online, hotseat, and against a heuristic AI.

Live at **[muw.ojee.net](https://muw.ojee.net)**.

## What's here

- [`Code/`](Code/) — original Java sources (Eclipse-style projects). The behavior-defining engine lives in [`Code/MUW (server-wip)/src/engine/Game.java`](<Code/MUW (server-wip)/src/engine/Game.java>).
- [`MUW/`](MUW/) — built binaries (`.exe`, `.app`, game guide).
- [`web/`](web/) — TypeScript port (shared engine, Node/Express + socket.io server, Vite + React + Tailwind client).

## Rules in 30 seconds

- Each player drafts 3 champions and marks one as their leader.
- Champions are Hero / Villain / Anti-Hero — cross-type attacks deal +50 % damage.
- Per turn a champion has Action Points (move costs 1, normal attack costs 2, abilities vary).
- 3 abilities per champion: damage / heal / crowd-control. Five area patterns: Single, Team, Surround, Directional, Self.
- 10 effects (Stun, Root, Silence, Shock, Disarm, Shield, Dodge, PowerUp, SpeedUp, Embrace).
- Once-per-match leader power: Hero cleanses + buffs the team (Embrace), Anti-Hero stuns every non-leader on both sides (Disorder), Villain executes any enemy below 30 % HP.
- First team to lose all three champions loses.

## Web port

Live deploy + dev instructions live in [`web/README.md`](web/README.md). TL;DR:

```bash
cd web
npm install
npm run dev   # client at :5173, server at :5003
```

## Java port

Open the relevant sub-project in `Code/` with Eclipse / IntelliJ, or run the prebuilt binaries in [`MUW/`](MUW/).

## License / credits

Educational project. Marvel character names and likenesses belong to their respective owners.
