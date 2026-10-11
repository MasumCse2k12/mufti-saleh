/** @type {import('tailwindcss').Config} */
export default {
  content: ['./index.html', './src/**/*.{ts,tsx}'],
  theme: {
    extend: {
      colors: {
        forest: {
          50:  '#f0fdf4',
          100: '#dcfce7',
          200: '#bbf7d0',
          600: '#16a34a',
          700: '#15803d',
          800: '#166534',
          900: '#14532d',
          950: '#052e16',
        },
        // Dark green from the mockup
        brand: {
          50:  '#f0f9f0',
          100: '#dcf0dc',
          500: '#2d6a2d',
          600: '#1e4d1e',
          700: '#153b15',
          800: '#0d2b0d',
          900: '#071507',
        },
        cream: '#fef9f0',
      },
      fontFamily: {
        bn: ['Hind Siliguri', 'sans-serif'],
      },
    },
  },
  plugins: [],
}
