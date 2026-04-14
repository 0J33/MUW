#!/usr/bin/env bash
# Deploy MUW to the production host. Layout on the box mirrors the mtg-*
# convention:
#   /home/$REMOTE_USER/muw-server/   <-- bundled server.mjs + data + .env
#   /home/$REMOTE_USER/muw-client/   <-- built Vite static bundle
#
# Required env (put these in deploy.local.env, which is gitignored):
#   REMOTE_HOST   target machine (e.g. an internal Tailscale IP or hostname)
#   REMOTE_USER   ssh login user
#   SSHPASS       ssh password (consumed by `sshpass -e`); leave unset if you
#                 use key-based auth and remove the sshpass wrapper below.
# Optional:
#   MUW_SUBDOMAIN  hostname the API is served at (= nginx server_name).
#                  Defaults to muw.ojee.net (single-domain setup).
#   MUW_CLIENT_URL absolute URL of the frontend; baked into the server's
#                  CLIENT_URL env (CORS allow-list + cookie origin).
#                  Defaults to https://$MUW_SUBDOMAIN. Set this when the
#                  frontend lives on a different host (e.g. Cloudflare Pages
#                  serving muw.ojee.net while the API is on muw-api.ojee.net).
#   MUW_PORT       backend port (default 5003)
set -euo pipefail
cd "$(dirname "$0")"

# Source local secrets if present (gitignored).
if [[ -f deploy.local.env ]]; then
  set -o allexport
  # shellcheck disable=SC1091
  source deploy.local.env
  set +o allexport
fi

: "${REMOTE_HOST:?REMOTE_HOST not set — see deploy.local.env.example}"
: "${REMOTE_USER:?REMOTE_USER not set — see deploy.local.env.example}"
: "${SSHPASS:?SSHPASS not set — see deploy.local.env.example}"

SUBDOMAIN="${MUW_SUBDOMAIN:-muw.ojee.net}"
CLIENT_URL="${MUW_CLIENT_URL:-https://$SUBDOMAIN}"
PORT="${MUW_PORT:-5003}"
# `sshpass -e` reads the password from the SSHPASS env var so it never appears
# on the command line or in process listings.
export SSHPASS
SSH="sshpass -e ssh -o StrictHostKeyChecking=accept-new ${REMOTE_USER}@${REMOTE_HOST}"
RSYNC="sshpass -e rsync -az --delete -e 'ssh -o StrictHostKeyChecking=accept-new'"

echo "[1/6] bundling server"
(cd server && npm run bundle >/dev/null)

echo "[2/6] building client"
(cd client && npx vite build >/dev/null)

echo "[3/6] staging server deploy bundle"
STAGING="$(mktemp -d -t muw-deploy.XXXXXX)"
mkdir -p "$STAGING/muw-server/data"
cp server/dist/server.mjs "$STAGING/muw-server/server.mjs"
cp shared/data/Champions.csv shared/data/Abilities.csv "$STAGING/muw-server/data/"
cat > "$STAGING/muw-server/package.json" <<EOF
{
  "name": "muw-server",
  "private": true,
  "version": "0.0.1",
  "type": "module",
  "main": "server.mjs",
  "scripts": { "start": "node server.mjs" },
  "dependencies": {
    "cookie-parser": "^1.4.7",
    "cors": "^2.8.5",
    "dotenv": "^16.4.5",
    "express": "^5.1.0",
    "socket.io": "^4.8.1",
    "uuid": "^10.0.0",
    "zod": "^3.23.8"
  }
}
EOF
cat > "$STAGING/muw-server/.env" <<EOF
PORT=$PORT
CLIENT_URL=$CLIENT_URL
SESSION_SECRET=$(openssl rand -hex 32 2>/dev/null || head -c 48 /dev/urandom | base64)
NODE_ENV=production
EOF
cp -r client/dist "$STAGING/muw-client"

echo "[4/6] rsync to $REMOTE_HOST"
eval $RSYNC "$STAGING/muw-server/" "${REMOTE_USER}@${REMOTE_HOST}:/home/${REMOTE_USER}/muw-server/"
eval $RSYNC "$STAGING/muw-client/" "${REMOTE_USER}@${REMOTE_HOST}:/home/${REMOTE_USER}/muw-client/"

echo "[5/6] install deps + (re)start pm2 service"
$SSH "bash -s" <<SHELL
set -e
cd /home/${REMOTE_USER}/muw-server
source ~/.nvm/nvm.sh 2>/dev/null || true
npm install --omit=dev --silent 2>&1 | tail -5
if pm2 describe muw-server >/dev/null 2>&1; then
  pm2 reload muw-server --update-env
else
  pm2 start server.mjs --name muw-server --time --cwd /home/${REMOTE_USER}/muw-server
fi
pm2 save --silent
echo "pm2 status:"
pm2 describe muw-server | head -20
SHELL

echo "[6/6] nginx site template written to $STAGING/muw-nginx.conf — requires sudo to install:"
cat > "$STAGING/muw-nginx.conf" <<EOF
server {
    listen 80;
    server_name $SUBDOMAIN;

    root /home/${REMOTE_USER}/muw-client;
    index index.html;

    # Static bundle — SPA fallback to index.html.
    location / {
        try_files \$uri /index.html;
    }

    # Socket.io upgrade path.
    location /socket.io/ {
        proxy_pass http://127.0.0.1:$PORT;
        proxy_http_version 1.1;
        proxy_set_header Upgrade \$http_upgrade;
        proxy_set_header Connection "upgrade";
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
        proxy_read_timeout 86400;
    }

    # REST API proxy.
    location /api/ {
        proxy_pass http://127.0.0.1:$PORT;
        proxy_http_version 1.1;
        proxy_set_header Host \$host;
        proxy_set_header X-Real-IP \$remote_addr;
        proxy_set_header X-Forwarded-For \$proxy_add_x_forwarded_for;
        proxy_set_header X-Forwarded-Proto \$scheme;
    }
}
EOF
echo "--- nginx conf ---"
cat "$STAGING/muw-nginx.conf"

echo
echo "Deploy done."
echo "Next (manual, needs sudo on disinteg):"
echo "  1) copy the config above to /etc/nginx/sites-enabled/muw"
echo "  2) sudo nginx -t && sudo systemctl reload nginx"
echo "  3) Add Cloudflare DNS CNAME for $SUBDOMAIN -> the tunnel"
echo
echo "Staging dir preserved at: $STAGING"
