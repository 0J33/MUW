// Session glue — we rely on the server's httpOnly cookie for identity, but
// we also cache the user's chosen display name in localStorage so it survives
// reloads. Before any socket connection we hit /api/me to ensure the cookie
// exists and to learn our userId (used as socket handshake auth).

export interface Me {
  userId: string;
  username: string;
}

const USERNAME_KEY = 'muw:username';

export function getUsername(): string {
  return localStorage.getItem(USERNAME_KEY) ?? '';
}

export function setUsername(name: string): void {
  localStorage.setItem(USERNAME_KEY, name.trim().slice(0, 24));
}

export async function fetchMe(): Promise<Me> {
  const res = await fetch('/api/me', { credentials: 'include' });
  if (!res.ok) throw new Error('session fetch failed');
  const data = await res.json() as { userId: string };
  return { userId: data.userId, username: getUsername() || 'Guest' };
}
