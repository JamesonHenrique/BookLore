/** @type {import('tailwindcss').Config} */
module.exports = {
  content: [
    './src/**/*.{html,js,ts,}',
    './public/**/*.html',
    './index.html'

  ],
  theme: {
    extend: {},
  },
  plugins: [require('daisyui')],
};
