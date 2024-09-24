const plugin = require('tailwindcss/plugin')

/** @type {import('tailwindcss').Config} */
module.exports = {
    content: [
        './kotlin/**/*.{html,js}'
    ],
    darkMode: 'class', // or 'media' or 'class'
    theme: {
        extend: {
            colors: {
                primary: {DEFAULT: '#c8289a'},
                secondary: { DEFAULT: '#6f42c1'},
                success: { DEFAULT: '#3cf281'},
                info: { DEFAULT: '#1ba2f6'},
                warning: { DEFAULT: '#d19e09'},
                danger: { DEFAULT: '#e44c55'},
                content: { DEFAULT: '#202430'},
                body: { DEFAULT: '#0f1217'},
                navbar: { DEFAULT: '#1b212d'},
                input: { DEFAULT: '#1c212c'},
                border: { DEFAULT: '#2a3140'},
                // You can add other color categories as needed
            },
            fontFamily: {lato: ['Lato', 'Helvetica Neue', 'Arial', 'sans-serif']},
        },
    },
    plugins: [
        plugin(function ({addUtilities}) {
            addUtilities({
                '.text-shadow-none': {
                    'text-shadow': 'none',
                },
                '.box-shadow-none': {
                    'box-shadow': 'none',
                }
            })
        })
    ],
    safelist: [
        'text-gray-200', 'mb-2,', 'inline-block', 'form-label'
    ],
}

