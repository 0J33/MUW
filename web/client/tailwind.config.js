/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        muwRed: '#b3001b',
        muwGold: '#f2c14e',
        muwSteel: '#1b2430',
        muwSteelLight: '#2e3b4a',
        muwInk: '#0d1117',
      },
      fontFamily: {
        pixel: ['"Press Start 2P"', 'monospace'],
        vt: ['"VT323"', 'monospace'],
        // retro-display is kept for rare splash-style headings (menu title only)
        display: ['"Press Start 2P"', 'monospace'],
        sans: ['"VT323"', 'monospace'],
        body: ['"VT323"', 'monospace'],
      },
      boxShadow: {
        glowRed: '0 0 24px rgba(179,0,27,0.6)',
        glowGold: '0 0 18px rgba(242,193,78,0.6)',
      },
    },
  },
  plugins: [],
};
