package com.soothsayer.predictor.utils

/**
 * Comprehensive list of major cryptocurrencies for selection
 */
object CryptoList {
    
    data class CryptoPair(
        val symbol: String,
        val displayName: String,
        val apiSymbol: String
    )
    
    val CRYPTO_PAIRS = listOf(
        // Top 50 by market cap
        CryptoPair("BTC", "Bitcoin (BTC/USDT)", "BTCUSDT"),
        CryptoPair("ETH", "Ethereum (ETH/USDT)", "ETHUSDT"),
        CryptoPair("BNB", "Binance Coin (BNB/USDT)", "BNBUSDT"),
        CryptoPair("XRP", "Ripple (XRP/USDT)", "XRPUSDT"),
        CryptoPair("ADA", "Cardano (ADA/USDT)", "ADAUSDT"),
        CryptoPair("SOL", "Solana (SOL/USDT)", "SOLUSDT"),
        CryptoPair("DOGE", "Dogecoin (DOGE/USDT)", "DOGEUSDT"),
        CryptoPair("DOT", "Polkadot (DOT/USDT)", "DOTUSDT"),
        CryptoPair("MATIC", "Polygon (MATIC/USDT)", "MATICUSDT"),
        CryptoPair("AVAX", "Avalanche (AVAX/USDT)", "AVAXUSDT"),
        
        CryptoPair("SHIB", "Shiba Inu (SHIB/USDT)", "SHIBUSDT"),
        CryptoPair("LTC", "Litecoin (LTC/USDT)", "LTCUSDT"),
        CryptoPair("LINK", "Chainlink (LINK/USDT)", "LINKUSDT"),
        CryptoPair("UNI", "Uniswap (UNI/USDT)", "UNIUSDT"),
        CryptoPair("ATOM", "Cosmos (ATOM/USDT)", "ATOMUSDT"),
        CryptoPair("ETC", "Ethereum Classic (ETC/USDT)", "ETCUSDT"),
        CryptoPair("XLM", "Stellar (XLM/USDT)", "XLMUSDT"),
        CryptoPair("XMR", "Monero (XMR/USDT)", "XMRUSDT"),
        CryptoPair("BCH", "Bitcoin Cash (BCH/USDT)", "BCHUSDT"),
        CryptoPair("ALGO", "Algorand (ALGO/USDT)", "ALGOUSDT"),
        
        CryptoPair("VET", "VeChain (VET/USDT)", "VETUSDT"),
        CryptoPair("FIL", "Filecoin (FIL/USDT)", "FILUSDT"),
        CryptoPair("TRX", "TRON (TRX/USDT)", "TRXUSDT"),
        CryptoPair("APT", "Aptos (APT/USDT)", "APTUSDT"),
        CryptoPair("NEAR", "NEAR Protocol (NEAR/USDT)", "NEARUSDT"),
        CryptoPair("ICP", "Internet Computer (ICP/USDT)", "ICPUSDT"),
        CryptoPair("HBAR", "Hedera (HBAR/USDT)", "HBARUSDT"),
        CryptoPair("APE", "ApeCoin (APE/USDT)", "APEUSDT"),
        CryptoPair("ARB", "Arbitrum (ARB/USDT)", "ARBUSDT"),
        CryptoPair("OP", "Optimism (OP/USDT)", "OPUSDT"),
        CryptoPair("HYPE", "Hyperliquid (HYPE/USDT)", "HYPEUSDT"),
        
        CryptoPair("LDO", "Lido DAO (LDO/USDT)", "LDOUSDT"),
        CryptoPair("QNT", "Quant (QNT/USDT)", "QNTUSDT"),
        CryptoPair("CRO", "Cronos (CRO/USDT)", "CROUSDT"),
        CryptoPair("GRT", "The Graph (GRT/USDT)", "GRTUSDT"),
        CryptoPair("MANA", "Decentraland (MANA/USDT)", "MANAUSDT"),
        CryptoPair("SAND", "The Sandbox (SAND/USDT)", "SANDUSDT"),
        CryptoPair("AAVE", "Aave (AAVE/USDT)", "AAVEUSDT"),
        CryptoPair("MKR", "Maker (MKR/USDT)", "MKRUSDT"),
        CryptoPair("SNX", "Synthetix (SNX/USDT)", "SNXUSDT"),
        CryptoPair("AXS", "Axie Infinity (AXS/USDT)", "AXSUSDT"),
        
        CryptoPair("FTM", "Fantom (FTM/USDT)", "FTMUSDT"),
        CryptoPair("EGLD", "MultiversX (EGLD/USDT)", "EGLDUSDT"),
        CryptoPair("EOS", "EOS (EOS/USDT)", "EOSUSDT"),
        CryptoPair("THETA", "Theta Network (THETA/USDT)", "THETAUSDT"),
        CryptoPair("CAKE", "PancakeSwap (CAKE/USDT)", "CAKEUSDT"),
        CryptoPair("XTZ", "Tezos (XTZ/USDT)", "XTZUSDT"),
        CryptoPair("FLOW", "Flow (FLOW/USDT)", "FLOWUSDT"),
        CryptoPair("CHZ", "Chiliz (CHZ/USDT)", "CHZUSDT"),
        CryptoPair("ZEC", "Zcash (ZEC/USDT)", "ZECUSDT"),
        CryptoPair("DASH", "Dash (DASH/USDT)", "DASHUSDT"),
        
        // Popular altcoins
        CryptoPair("1INCH", "1inch (1INCH/USDT)", "1INCHUSDT"),
        CryptoPair("ENJ", "Enjin Coin (ENJ/USDT)", "ENJUSDT"),
        CryptoPair("GALA", "Gala (GALA/USDT)", "GALAUSDT"),
        CryptoPair("IMX", "Immutable X (IMX/USDT)", "IMXUSDT"),
        CryptoPair("LRC", "Loopring (LRC/USDT)", "LRCUSDT"),
        CryptoPair("CRV", "Curve DAO (CRV/USDT)", "CRVUSDT"),
        CryptoPair("COMP", "Compound (COMP/USDT)", "COMPUSDT"),
        CryptoPair("SUSHI", "SushiSwap (SUSHI/USDT)", "SUSHIUSDT"),
        CryptoPair("YFI", "yearn.finance (YFI/USDT)", "YFIUSDT"),
        CryptoPair("BAT", "Basic Attention (BAT/USDT)", "BATUSDT"),
        
        CryptoPair("ZRX", "0x (ZRX/USDT)", "ZRXUSDT"),
        CryptoPair("KNC", "Kyber Network (KNC/USDT)", "KNCUSDT"),
        CryptoPair("BAL", "Balancer (BAL/USDT)", "BALUSDT"),
        CryptoPair("OCEAN", "Ocean Protocol (OCEAN/USDT)", "OCEANUSDT"),
        CryptoPair("REN", "Ren (REN/USDT)", "RENUSDT"),
        CryptoPair("ONE", "Harmony (ONE/USDT)", "ONEUSDT"),
        CryptoPair("ZIL", "Zilliqa (ZIL/USDT)", "ZILUSDT"),
        CryptoPair("KAVA", "Kava (KAVA/USDT)", "KAVAUSDT"),
        CryptoPair("RUNE", "THORChain (RUNE/USDT)", "RUNEUSDT"),
        CryptoPair("WAVES", "Waves (WAVES/USDT)", "WAVESUSDT")
    )
    
    fun getDisplayNames(): List<String> = CRYPTO_PAIRS.map { it.displayName }
    
    fun getApiSymbol(displayName: String): String? {
        return CRYPTO_PAIRS.find { it.displayName == displayName }?.apiSymbol
    }
    
    fun getDisplayName(apiSymbol: String): String? {
        return CRYPTO_PAIRS.find { it.apiSymbol == apiSymbol }?.displayName
    }
    
    fun searchCryptos(query: String): List<CryptoPair> {
        if (query.isBlank()) return CRYPTO_PAIRS
        
        val lowerQuery = query.lowercase()
        return CRYPTO_PAIRS.filter {
            it.symbol.lowercase().contains(lowerQuery) ||
            it.displayName.lowercase().contains(lowerQuery)
        }
    }
}
