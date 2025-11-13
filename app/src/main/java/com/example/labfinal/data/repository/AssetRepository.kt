package com.example.labfinal.data.repository

import com.example.labfinal.data.local.dao.AssetDao
import com.example.labfinal.data.model.Asset
import com.example.labfinal.data.network.CoinCapApiService
import com.example.labfinal.data.network.mappers.toAsset
import com.example.labfinal.data.network.mappers.toEntity

class AssetRepository(
    private val assetDao: AssetDao,
    private val apiService: CoinCapApiService
) {

    suspend fun getAllAssets(forceRefresh: Boolean = false): List<Asset> {
        if (forceRefresh) {
            return try {
                val response = apiService.getAssets()
                val entities = response.data.map { it.toEntity(System.currentTimeMillis()) }
                assetDao.deleteAll()
                assetDao.insertAll(entities)
                entities.map { it.toAsset() }
            } catch (e: Exception) {
                val localAssets = assetDao.getAllAssets()
                localAssets.map { it.toAsset() }
            }
        } else {
            val localAssets = assetDao.getAllAssets()
            return if (localAssets.isNotEmpty()) {
                localAssets.map { it.toAsset() }
            } else {
                try {
                    val response = apiService.getAssets()
                    val entities = response.data.map { it.toEntity(System.currentTimeMillis()) }
                    assetDao.insertAll(entities)
                    entities.map { it.toAsset() }
                } catch (e: Exception) {
                    emptyList()
                }
            }
        }
    }

    suspend fun getAssetById(id: String): Asset? {
        val entity = assetDao.getAssetById(id)
        return entity?.toAsset()
    }

    suspend fun saveOfflineData() {
        try {
            val response = apiService.getAssets()
            val entities = response.data.map { it.toEntity(System.currentTimeMillis()) }
            assetDao.deleteAll()
            assetDao.insertAll(entities)
        } catch (e: Exception) {
            // Error silencioso
        }
    }
}