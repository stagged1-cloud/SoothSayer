// Crypto data from CryptoList.kt
const cryptoList = [
    { symbol: 'BTC', name: 'Bitcoin', id: 'bitcoin' },
    { symbol: 'ETH', name: 'Ethereum', id: 'ethereum' },
    { symbol: 'BNB', name: 'Binance Coin', id: 'binancecoin' },
    { symbol: 'XRP', name: 'Ripple', id: 'ripple' },
    { symbol: 'SOL', name: 'Solana', id: 'solana' },
    { symbol: 'ADA', name: 'Cardano', id: 'cardano' },
    { symbol: 'DOGE', name: 'Dogecoin', id: 'dogecoin' },
    { symbol: 'TRX', name: 'TRON', id: 'tron' },
    { symbol: 'LINK', name: 'Chainlink', id: 'chainlink' },
    { symbol: 'AVAX', name: 'Avalanche', id: 'avalanche-2' },
    { symbol: 'DOT', name: 'Polkadot', id: 'polkadot' },
    { symbol: 'MATIC', name: 'Polygon', id: 'matic-network' },
    { symbol: 'UNI', name: 'Uniswap', id: 'uniswap' },
    { symbol: 'LTC', name: 'Litecoin', id: 'litecoin' },
    { symbol: 'ATOM', name: 'Cosmos', id: 'cosmos' },
    { symbol: 'ETC', name: 'Ethereum Classic', id: 'ethereum-classic' },
    { symbol: 'XLM', name: 'Stellar', id: 'stellar' },
    { symbol: 'HBAR', name: 'Hedera', id: 'hedera-hashgraph' },
    { symbol: 'APT', name: 'Aptos', id: 'aptos' },
    { symbol: 'FIL', name: 'Filecoin', id: 'filecoin' },
    { symbol: 'ARB', name: 'Arbitrum', id: 'arbitrum' },
    { symbol: 'NEAR', name: 'NEAR Protocol', id: 'near' },
    { symbol: 'VET', name: 'VeChain', id: 'vechain' },
    { symbol: 'ALGO', name: 'Algorand', id: 'algorand' },
    { symbol: 'AAVE', name: 'Aave', id: 'aave' },
    { symbol: 'GRT', name: 'The Graph', id: 'the-graph' },
    { symbol: 'SAND', name: 'The Sandbox', id: 'the-sandbox' },
    { symbol: 'MANA', name: 'Decentraland', id: 'decentraland' },
    { symbol: 'AXS', name: 'Axie Infinity', id: 'axie-infinity' },
    { symbol: 'HYPE', name: 'Hyperliquid', id: 'hyperliquid' }
];

// Fetch live crypto prices
async function fetchCryptoPrices() {
    try {
        const ids = ['bitcoin', 'ethereum', 'solana', 'binancecoin'].join(',');
        const response = await fetch(
            `https://api.coingecko.com/api/v3/simple/price?ids=${ids}&vs_currencies=usd&include_24hr_change=true`
        );
        const data = await response.json();
        
        updateTicker('btc', data.bitcoin);
        updateTicker('eth', data.ethereum);
        updateTicker('sol', data.solana);
        updateTicker('bnb', data.binancecoin);
    } catch (error) {
        console.error('Error fetching prices:', error);
    }
}

function updateTicker(symbol, data) {
    if (!data) return;
    
    const priceEl = document.getElementById(`${symbol}Price`);
    const changeEl = document.getElementById(`${symbol}Change`);
    
    if (priceEl) {
        priceEl.textContent = `$${data.usd.toLocaleString()}`;
    }
    
    if (changeEl && data.usd_24h_change !== undefined) {
        const change = data.usd_24h_change;
        changeEl.textContent = `${change > 0 ? '+' : ''}${change.toFixed(2)}%`;
        changeEl.className = `ticker-change ${change > 0 ? 'positive' : 'negative'}`;
    }
}

// Populate crypto grid
function populateCryptoGrid() {
    const grid = document.getElementById('cryptoGrid');
    if (!grid) return;
    
    cryptoList.forEach(crypto => {
        const item = document.createElement('div');
        item.className = 'crypto-item';
        item.innerHTML = `
            <div class="crypto-symbol">${crypto.symbol}</div>
            <div class="crypto-name">${crypto.name}</div>
        `;
        grid.appendChild(item);
    });
}

// Simple chart visualization for hero section
function drawHeroChart() {
    const canvas = document.getElementById('heroChart');
    if (!canvas) return;
    
    const ctx = canvas.getContext('2d');
    const width = canvas.offsetWidth;
    const height = 300;
    
    canvas.width = width;
    canvas.height = height;
    
    // Generate sample data (simulating 90 days of BTC price movement)
    const dataPoints = 90;
    const data = [];
    let basePrice = 60000;
    
    for (let i = 0; i < dataPoints; i++) {
        const randomChange = (Math.random() - 0.48) * 2000;
        basePrice += randomChange;
        basePrice = Math.max(55000, Math.min(70000, basePrice));
        data.push(basePrice);
    }
    
    // Draw gradient background
    const gradient = ctx.createLinearGradient(0, 0, 0, height);
    gradient.addColorStop(0, 'rgba(99, 102, 241, 0.3)');
    gradient.addColorStop(1, 'rgba(99, 102, 241, 0)');
    
    ctx.fillStyle = gradient;
    ctx.beginPath();
    ctx.moveTo(0, height);
    
    data.forEach((price, i) => {
        const x = (i / (dataPoints - 1)) * width;
        const y = height - ((price - 55000) / 15000) * height;
        
        if (i === 0) {
            ctx.lineTo(x, y);
        } else {
            ctx.lineTo(x, y);
        }
    });
    
    ctx.lineTo(width, height);
    ctx.closePath();
    ctx.fill();
    
    // Draw line
    ctx.strokeStyle = '#6366f1';
    ctx.lineWidth = 2;
    ctx.beginPath();
    
    data.forEach((price, i) => {
        const x = (i / (dataPoints - 1)) * width;
        const y = height - ((price - 55000) / 15000) * height;
        
        if (i === 0) {
            ctx.moveTo(x, y);
        } else {
            ctx.lineTo(x, y);
        }
    });
    
    ctx.stroke();
    
    // Mark RSI oversold point (around day 30)
    const oversoldX = (30 / (dataPoints - 1)) * width;
    const oversoldY = height - ((data[30] - 55000) / 15000) * height;
    
    ctx.fillStyle = '#ef4444';
    ctx.beginPath();
    ctx.arc(oversoldX, oversoldY, 6, 0, Math.PI * 2);
    ctx.fill();
    
    // Mark MA crossover (around day 60)
    const crossX = (60 / (dataPoints - 1)) * width;
    const crossY = height - ((data[60] - 55000) / 15000) * height;
    
    ctx.fillStyle = '#10b981';
    ctx.beginPath();
    ctx.arc(crossX, crossY, 6, 0, Math.PI * 2);
    ctx.fill();
    
    // Mark support level (around day 75)
    const supportX = (75 / (dataPoints - 1)) * width;
    const supportY = height - ((data[75] - 55000) / 15000) * height;
    
    ctx.fillStyle = '#f59e0b';
    ctx.beginPath();
    ctx.arc(supportX, supportY, 6, 0, Math.PI * 2);
    ctx.fill();
}

// Smooth scroll for anchor links
document.addEventListener('DOMContentLoaded', () => {
    // Populate crypto grid
    populateCryptoGrid();
    
    // Draw hero chart
    drawHeroChart();
    
    // Fetch live prices
    fetchCryptoPrices();
    
    // Update prices every 60 seconds
    setInterval(fetchCryptoPrices, 60000);
    
    // Smooth scroll for navigation links
    document.querySelectorAll('a[href^="#"]').forEach(anchor => {
        anchor.addEventListener('click', function (e) {
            e.preventDefault();
            const target = document.querySelector(this.getAttribute('href'));
            if (target) {
                const offsetTop = target.offsetTop - 100; // Account for sticky nav
                window.scrollTo({
                    top: offsetTop,
                    behavior: 'smooth'
                });
            }
        });
    });
    
    // Intersection Observer for fade-in animations
    const observerOptions = {
        threshold: 0.1,
        rootMargin: '0px 0px -100px 0px'
    };
    
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('fade-in-up');
                observer.unobserve(entry.target);
            }
        });
    }, observerOptions);
    
    // Observe all feature cards, algorithm cards, and pattern examples
    document.querySelectorAll('.feature-card, .algorithm-card, .pattern-example').forEach(el => {
        observer.observe(el);
    });
    
    // Add scroll effect to navbar
    let lastScroll = 0;
    const navbar = document.querySelector('.navbar');
    
    window.addEventListener('scroll', () => {
        const currentScroll = window.pageYOffset;
        
        if (currentScroll > lastScroll && currentScroll > 100) {
            navbar.style.transform = 'translateY(-100%)';
        } else {
            navbar.style.transform = 'translateY(0)';
        }
        
        lastScroll = currentScroll;
    });
});

// Redraw chart on window resize
window.addEventListener('resize', () => {
    drawHeroChart();
});

// Add some interactivity to pattern examples
document.addEventListener('DOMContentLoaded', () => {
    const patternExamples = document.querySelectorAll('.pattern-example');
    
    patternExamples.forEach(example => {
        example.addEventListener('mouseenter', () => {
            example.style.borderColor = 'var(--primary-color)';
        });
        
        example.addEventListener('mouseleave', () => {
            example.style.borderColor = 'var(--border-color)';
        });
    });
});

// Console easter egg
console.log('%c🔮 SoothSayer Pattern Predictor', 'font-size: 20px; font-weight: bold; color: #6366f1;');
console.log('%cDetecting patterns in 70+ cryptocurrencies using 9 advanced algorithms', 'font-size: 12px; color: #94a3b8;');
console.log('%cGitHub: https://github.com/stagged1-cloud/SoothSayer', 'font-size: 12px; color: #8b5cf6;');
