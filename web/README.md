# MUW — Marvel Ultimate War (web)

React + socket.io port of the Java Swing MUW uni project. Turn-based tactical combat on a 5×5 board with 1v1 online, local hotseat, and single-player vs AI modes.

## Structure

- `shared/` — pure TypeScript game engine, CSV data, socket protocol types
- `server/` — Node + Express + socket.io; in-memory rooms, server-authoritative rules
- `client/` — Vite + React + Tailwind; all UI, sound, and AI

## Dev

```bash
npm install
npm run dev       # runs server and client in parallel
```

Server listens on `:5003`, client on `:5173`.

## Deployment

`./deploy.sh` bundles the server with esbuild, builds the Vite client, rsyncs both to a remote host, and (re)starts the PM2 service for `muw-server`.

Before running it, copy [`deploy.local.env.example`](deploy.local.env.example) to `deploy.local.env` (gitignored) and fill in `REMOTE_HOST`, `REMOTE_USER`, and `SSHPASS`. Set `MUW_SUBDOMAIN` / `MUW_PORT` if you want non-defaults.

Layout the script lays down on the remote box:

- `/home/$REMOTE_USER/muw-server/` — bundled `server.mjs` + `data/` + `package.json` + `.env`, run with `pm2 start server.mjs --name muw-server`.
- `/home/$REMOTE_USER/muw-client/` — built Vite `dist/`, served by nginx.
- nginx site (`/etc/nginx/sites-enabled/muw`) — generated config printed at the end of the deploy; install it with sudo. SPA fallback to `index.html`, `/socket.io/` and `/api/` proxied to the backend port.


## Rules source

Ported from `MUW/Code/MUW (server-wip)/src/engine/Game.java`. Behavior-preserving.
