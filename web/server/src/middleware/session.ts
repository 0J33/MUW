import type { NextFunction, Request, Response } from 'express';
import { v4 as uuidv4 } from 'uuid';

// A lightweight guest session. We issue an httpOnly cookie with a uuid userId.
// There's no password, no DB — just a stable identifier for reconnection.

const COOKIE_NAME = 'muw_uid';
const COOKIE_MAX_AGE_MS = 1000 * 60 * 60 * 24 * 30; // 30 days

declare module 'express-serve-static-core' {
  interface Request {
    userId?: string;
  }
}

export function sessionMiddleware(req: Request, res: Response, next: NextFunction): void {
  let uid = req.cookies?.[COOKIE_NAME] as string | undefined;
  if (!uid) {
    uid = uuidv4();
    // In prod the frontend is on cloudflare pages (e.g. muw.ojee.net) and the
    // API on a different subdomain (e.g. muw-api.ojee.net). For the cookie
    // to flow with cross-site fetch + websocket upgrades, it must be
    // SameSite=None; Secure. Locally we keep SameSite=Lax over plain HTTP.
    const crossSite = process.env.NODE_ENV === 'production';
    res.cookie(COOKIE_NAME, uid, {
      httpOnly: true,
      sameSite: crossSite ? 'none' : 'lax',
      maxAge: COOKIE_MAX_AGE_MS,
      secure: crossSite,
    });
  }
  req.userId = uid;
  next();
}

export function cookieNameFor(): string { return COOKIE_NAME; }
