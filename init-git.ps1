# SoothSayer - Quick Start Script
# Run this in PowerShell to initialize Git and prepare for GitHub

Write-Host " SoothSayer - Initializing Git Repository" -ForegroundColor Cyan
Write-Host "=" * 50 -ForegroundColor Gray

# Navigate to project directory
Set-Location "c:\Users\don_t\Desktop\Projects\Soothsayer Predictor"

# Check if git is installed
try {
    git --version | Out-Null
    Write-Host " Git is installed" -ForegroundColor Green
} catch {
    Write-Host " Git not found. Please install Git first:" -ForegroundColor Red
    Write-Host "   Download from: https://git-scm.com/download/win" -ForegroundColor Yellow
    exit 
}

# Initialize Git repository
Write-Host "`n Initializing Git repository..." -ForegroundColor Cyan
git init

# Configure Git (update with your info)
Write-Host "`n Configure Git user (update if needed):" -ForegroundColor Cyan
$userName = Read-Host "Enter your Git username (or press Enter to skip)"
if ($userName) {
    git config user.name "$userName"
    Write-Host " Git username set to: $userName" -ForegroundColor Green
}

$userEmail = Read-Host "Enter your Git email (or press Enter to skip)"
if ($userEmail) {
    git config user.email "$userEmail"
    Write-Host " Git email set to: $userEmail" -ForegroundColor Green
}

# Add all files
Write-Host "`n Adding files to Git..." -ForegroundColor Cyan
git add .

# Create initial commit
Write-Host "`n Creating initial commit..." -ForegroundColor Cyan
git commit -m "Initial commit: SoothSayer Android app

- Implemented 8+ pattern detection algorithms
- Multi-source data integration (Binance, CoinGecko, CryptoCompare)
- Clean Architecture + MVVM pattern
- Room database with optimized storage
- Comprehensive documentation
- Material Design  UI"

Write-Host " Initial commit created!" -ForegroundColor Green

# Show status
Write-Host "`n Repository Status:" -ForegroundColor Cyan
git log --oneline -
git status

# Next steps
Write-Host "`n" -ForegroundColor Gray
Write-Host "=" * 50 -ForegroundColor Gray
Write-Host " Git repository initialized successfully!" -ForegroundColor Green
Write-Host "=" * 50 -ForegroundColor Gray

Write-Host "`n Next Steps:" -ForegroundColor Cyan
Write-Host ". Create GitHub repository at: https://github.com/new" -ForegroundColor White
Write-Host ". Run these commands:" -ForegroundColor White
Write-Host "   git remote add origin https://github.com/YOUR_USERNAME/soothsayer-predictor.git" -ForegroundColor Yellow
Write-Host "   git branch -M main" -ForegroundColor Yellow
Write-Host "   git push -u origin main" -ForegroundColor Yellow
Write-Host "`n. Or use GitHub CLI (if installed):" -ForegroundColor White
Write-Host "   gh repo create soothsayer-predictor --public --source=. --remote=origin --push" -ForegroundColor Yellow

Write-Host "`n Full instructions: docs\GITHUB_SYNC.md" -ForegroundColor Cyan
Write-Host "`n Happy coding!" -ForegroundColor Magenta
