package com.soothsayer.predictor.domain.usecases

import com.soothsayer.predictor.data.models.CryptoAsset
import com.soothsayer.predictor.data.models.Resource
import com.soothsayer.predictor.data.repository.CryptoRepository
import javax.inject.Inject

/**
 * Use case for loading available crypto assets
 */
class LoadCryptoAssetsUseCase @Inject constructor(
    private val repository: CryptoRepository
) {
    
    suspend operator fun invoke(): Resource<List<CryptoAsset>> {
        return repository.getSupportedAssets()
    }
}
