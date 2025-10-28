package com.soothsayer.predictor.di

import android.content.Context
import androidx.room.Room
import com.soothsayer.predictor.BuildConfig
import com.soothsayer.predictor.data.local.AppDatabase
import com.soothsayer.predictor.data.local.CryptoAssetDao
import com.soothsayer.predictor.data.local.PatternDao
import com.soothsayer.predictor.data.local.PriceDataDao
import com.soothsayer.predictor.data.remote.BinanceApi
import com.soothsayer.predictor.data.remote.CoinGeckoApi
import com.soothsayer.predictor.data.remote.CryptoCompareApi
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import javax.inject.Named
import javax.inject.Singleton

/**
 * Hilt dependency injection module
 */
@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    
    /**
     * Provide Room database
     * Configured for minimal size with automatic cleanup
     */
    @Provides
    @Singleton
    fun provideAppDatabase(@ApplicationContext context: Context): AppDatabase {
        return Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            AppDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }
    
    @Provides
    @Singleton
    fun providePriceDataDao(database: AppDatabase): PriceDataDao {
        return database.priceDataDao()
    }
    
    @Provides
    @Singleton
    fun provideCryptoAssetDao(database: AppDatabase): CryptoAssetDao {
        return database.cryptoAssetDao()
    }
    
    @Provides
    @Singleton
    fun providePatternDao(database: AppDatabase): PatternDao {
        return database.patternDao()
    }
    
    /**
     * Provide OkHttp client with logging
     */
    @Provides
    @Singleton
    fun provideOkHttpClient(): OkHttpClient {
        val loggingInterceptor = HttpLoggingInterceptor().apply {
            level = if (BuildConfig.DEBUG) {
                HttpLoggingInterceptor.Level.BODY
            } else {
                HttpLoggingInterceptor.Level.NONE
            }
        }
        
        return OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(loggingInterceptor)
            .addInterceptor { chain ->
                val request = chain.request()
                val startTime = System.currentTimeMillis()
                
                try {
                    val response = chain.proceed(request)
                    val elapsed = System.currentTimeMillis() - startTime
                    
                    // Log API performance
                    android.util.Log.d("API", "${request.url} - ${elapsed}ms - ${response.code}")
                    
                    response
                } catch (e: Exception) {
                    android.util.Log.e("API", "Error: ${request.url}", e)
                    throw e
                }
            }
            .build()
    }
    
    /**
     * Provide Binance API (PRIMARY)
     */
    @Provides
    @Singleton
    @Named("binance")
    fun provideBinanceRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.BINANCE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideBinanceApi(@Named("binance") retrofit: Retrofit): BinanceApi {
        return retrofit.create(BinanceApi::class.java)
    }
    
    /**
     * Provide CoinGecko API (FALLBACK)
     */
    @Provides
    @Singleton
    @Named("coingecko")
    fun provideCoinGeckoRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.COINGECKO_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideCoinGeckoApi(@Named("coingecko") retrofit: Retrofit): CoinGeckoApi {
        return retrofit.create(CoinGeckoApi::class.java)
    }
    
    /**
     * Provide CryptoCompare API (BACKUP)
     */
    @Provides
    @Singleton
    @Named("cryptocompare")
    fun provideCryptoCompareRetrofit(okHttpClient: OkHttpClient): Retrofit {
        return Retrofit.Builder()
            .baseUrl(BuildConfig.CRYPTOCOMPARE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }
    
    @Provides
    @Singleton
    fun provideCryptoCompareApi(@Named("cryptocompare") retrofit: Retrofit): CryptoCompareApi {
        return retrofit.create(CryptoCompareApi::class.java)
    }
}
