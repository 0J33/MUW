import 'dotenv/config';
import express from 'express';
import http from 'node:http';
import cors from 'cors';
import cookieParser from 'cookie-parser';
import { Server as SocketServer } from 'socket.io';
import { sessionMiddleware, cookieNameFor } from './middleware/session.js';
import { registerSocketHandlers, startRoomReaper } from './rooms/handlers.js';

const PORT = parseInt(process.env.PORT ?? '5003', 10);
const CLIENT_URL = process.env.CLIENT_URL ?? 'http://localhost:5173';

const app = express();
const server = http.createServer(app);

app.set('trust proxy', 1);
app.use(cors({ origin: CLIENT_URL, credentials: true }));
app.use(express.json({ limit: '1mb' }));
app.use(cookieParser());
app.use(sessionMiddleware);

app.get('/api/health', (_req, res) => { res.json({ status: 'ok' }); });
app.get('/api/me', (req, res) => { res.json({ userId: req.userId }); });

const io = new SocketServer(server, {
  cors: { origin: CLIENT_URL, credentials: true },
});

// Bridge the cookie userId into socket.handshake.auth so handlers can trust it.
io.use((socket, next) => {
  const cookieHeader = socket.handshake.headers.cookie ?? '';
  const match = new RegExp(`${cookieNameFor()}=([^;]+)`).exec(cookieHeader);
  const userIdFromCookie = match?.[1];
  const authUserId = (socket.handshake.auth?.userId as string | undefined);
  const resolved = userIdFromCookie ?? authUserId;
  if (!resolved) return next(new Error('no session'));
  socket.handshake.auth = { ...(socket.handshake.auth ?? {}), userId: resolved };
  next();
});

registerSocketHandlers(io);
startRoomReaper();

server.listen(PORT, () => {
  console.log(`[muw] server on :${PORT}, client at ${CLIENT_URL}`);
});
