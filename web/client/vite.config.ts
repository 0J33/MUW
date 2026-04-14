import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

const SERVER = process.env.VITE_SERVER_URL ?? 'http://localhost:5003';

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/api': { target: SERVER, changeOrigin: true, ws: false },
      '/socket.io': { target: SERVER, changeOrigin: true, ws: true },
    },
  },
});
