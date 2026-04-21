/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{js,jsx}'],
  theme: {
    extend: {
      fontFamily: {
        sans: ['Inter', 'system-ui', '-apple-system', 'sans-serif'],
        display: ['"Instrument Serif"', 'Georgia', 'serif'],
        mono: ['"JetBrains Mono"', 'ui-monospace', 'monospace'],
      },
      colors: {
        // Neutral canvas — a touch warmer than pure zinc
        ink: {
          950: '#0a0a0b',
          900: '#111113',
          850: '#17171a',
          800: '#1c1c20',
          700: '#27272b',
          600: '#3f3f45',
          500: '#6b6b74',
          400: '#9696a0',
          300: '#c4c4cc',
          200: '#e4e4e9',
          100: '#f4f4f7',
        },
        // Signature accent — warm amber
        accent: {
          50: '#fef9ee',
          100: '#fdf1d7',
          200: '#fadfae',
          300: '#f6c77a',
          400: '#f1a944',
          500: '#ec8d1f',
          600: '#dd7216',
          700: '#b75815',
          800: '#924519',
          900: '#773a18',
        },
      },
      boxShadow: {
        'soft': '0 1px 2px rgba(0,0,0,0.3), 0 2px 8px rgba(0,0,0,0.2)',
        'glow-accent': '0 0 0 1px rgba(241,169,68,0.2), 0 0 20px rgba(241,169,68,0.15)',
      },
      animation: {
        'fade-in': 'fadeIn 0.3s ease-out',
        'slide-up': 'slideUp 0.35s cubic-bezier(0.16, 1, 0.3, 1)',
        'pulse-soft': 'pulseSoft 2.5s ease-in-out infinite',
        'gradient-shift': 'gradientShift 15s ease infinite',
      },
      keyframes: {
        fadeIn: {
          '0%': { opacity: 0 },
          '100%': { opacity: 1 },
        },
        slideUp: {
          '0%': { opacity: 0, transform: 'translateY(10px)' },
          '100%': { opacity: 1, transform: 'translateY(0)' },
        },
        pulseSoft: {
          '0%, 100%': { opacity: 0.6 },
          '50%': { opacity: 1 },
        },
        gradientShift: {
          '0%, 100%': { backgroundPosition: '0% 50%' },
          '50%': { backgroundPosition: '100% 50%' },
        },
      },
    },
  },
  plugins: [],
}
