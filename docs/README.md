# SoothSayer Documentation Site

This folder contains the GitHub Pages website for SoothSayer.

## Structure

- `index.html` - Main landing page
- `styles.css` - All styling (dark theme, responsive design)
- `app.js` - Interactive features (live prices, animations, charts)
- `PATTERN_ALGORITHMS.md` - Technical documentation (linked from site)
- `PATTERN_COMPARISON.md` - Pattern comparison guide (linked from site)

## Features

### Live Components
- Real-time crypto price ticker (BTC, ETH, SOL, BNB) via CoinGecko API
- Interactive hero chart visualization
- Smooth scroll navigation
- Fade-in animations on scroll
- Responsive design (desktop, tablet, mobile)

### Sections
1. **Hero** - Project intro with key stats (9 algorithms, 70 cryptos, 73% win rate)
2. **Features** - 6 core features (RSI, MA, Support/Resistance, Volume, Volatility, Time-based)
3. **Pattern Examples** - 4 real pattern cards (RSI Oversold, Golden Cross, Divergence, Support)
4. **Algorithms** - 9 algorithm cards explaining each detection method
5. **Supported Cryptos** - Grid of all 70+ supported cryptocurrencies
6. **Download** - APK download and GitHub repo links
7. **Footer** - Documentation links, disclaimer, social links

## Deployment

GitHub Pages is configured to serve from the `docs/` folder on the `main` branch.

**Site URL:** https://stagged1-cloud.github.io/SoothSayer

## Local Development

To test locally:
1. Open `index.html` in a browser
2. Or use a local server: `python -m http.server 8000`
3. Navigate to `http://localhost:8000`

## Future Enhancements (Option B)

When ready to build the full web app:
- Port Kotlin pattern detection logic to JavaScript/TypeScript
- Add real-time pattern detection in browser
- Interactive charts with pattern markers
- Crypto selection and analysis
- Pattern filtering and sorting
- Historical pattern performance graphs
- User accounts and saved patterns (optional)

## Technologies

- Pure HTML5, CSS3, JavaScript (no frameworks for simplicity)
- CoinGecko API for live crypto prices
- Canvas API for chart visualization
- CSS Grid and Flexbox for responsive layout
- CSS custom properties for theming
- Intersection Observer API for animations

## Color Scheme

- Primary: `#6366f1` (Indigo)
- Secondary: `#8b5cf6` (Purple)
- Accent: `#ec4899` (Pink)
- Success: `#10b981` (Green)
- Danger: `#ef4444` (Red)
- Warning: `#f59e0b` (Amber)
- Dark Background: `#0f172a` (Slate 900)
- Card Background: `#1e293b` (Slate 800)

## Performance

- Total size: ~100KB (HTML + CSS + JS)
- No external dependencies
- Optimized images (when added)
- Lazy loading animations
- Fast initial load

## Browser Support

- Chrome/Edge 90+
- Firefox 88+
- Safari 14+
- Mobile browsers (iOS Safari, Chrome Mobile)

---

*Last Updated: October 29, 2025*
