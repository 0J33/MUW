import React from 'react';
import { createRoot } from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import { App } from './App.js';
import { playSfx } from './hooks/useSfx.js';
import './styles/globals.css';

const container = document.getElementById('root');
if (!container) throw new Error('#root not found');
createRoot(container).render(
  <React.StrictMode>
    <BrowserRouter>
      <App />
    </BrowserRouter>
  </React.StrictMode>,
);

// Global UI click sound — every chunky pixel button plays the select sfx,
// and any unstyled click on a regular button or anchor in the chrome plays
// a softer hover sfx. Cheap centralized binding, no per-component wiring.
if (typeof document !== 'undefined') {
  document.addEventListener('click', (ev) => {
    const target = ev.target as HTMLElement | null;
    if (!target) return;
    const btn = target.closest('button, a');
    if (!btn) return;
    if (btn.classList.contains('pixel-btn') || btn.classList.contains('dir-btn') || btn.classList.contains('dir-cancel')) {
      playSfx('click');
    } else {
      playSfx('ui');
    }
  });
}
