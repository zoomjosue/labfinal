package com.example.labfinal.data.repository

import android.util.Log
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
        Log.d("AssetRepository", "getAllAssets called. forceRefresh: $forceRefresh")

        if (forceRefresh) {
            return try {
                Log.d("AssetRepository", "Fetching from network...")
                val response = apiService.getAssets()
                Log.d("AssetRepository", "Network response received: ${response.data.size} assets")

                val entities = response.data.map { it.toEntity(System.currentTimeMillis()) }
                assetDao.deleteAll()
                assetDao.insertAll(entities)

                Log.d("AssetRepository", "Data saved to local database")
                entities.map { it.toAsset() }
            } catch (e: Exception) {
                Log.e("AssetRepository", "Error fetching from network", e)
                val localAssets = assetDao.getAllAssets()
                Log.d("AssetRepository", "Returning ${localAssets.size} local assets")
                localAssets.map { it.toAsset() }
            }
        } else {
            val localAssets = assetDao.getAllAssets()
            Log.d("AssetRepository", "Local assets count: ${localAssets.size}")

            return if (localAssets.isNotEmpty()) {
                Log.d("AssetRepository", "Returning local assets")
                localAssets.map { it.toAsset() }
            } else {
                try {
                    Log.d("AssetRepository", "No local data, fetching from network...")
                    val response = apiService.getAssets()
                    Log.d("AssetRepository", "Network response received: ${response.data.size} assets")

                    val entities = response.data.map { it.toEntity(System.currentTimeMillis()) }
                    assetDao.insertAll(entities)

                    Log.d("AssetRepository", "Data saved to local database")
                    entities.map { it.toAsset() }
                } catch (e: Exception) {
                    Log.e("AssetRepository", "Error fetching from network", e)
                    emptyList()
                }
            }
        }
    }

    suspend fun getAssetById(id: String): Asset? {
        Log.d("AssetRepository", "getAssetById called for id: $id")

        return try {
            // Primero intenta obtener de la red para tener datos actualizados
            Log.d("AssetRepository", "Fetching asset from network...")
            val response = apiService.getAssetById(id)
            val entity = response.data.toEntity(System.currentTimeMillis())

            // Guarda o actualiza en la base de datos
            assetDao.insertAll(listOf(entity))

            Log.d("AssetRepository", "Asset fetched and saved")
            entity.toAsset()
        } catch (e: Exception) {
            Log.e("AssetRepository", "Error fetching asset from network, trying local", e)
            // Si falla, intenta obtener de la base de datos local
            val entity = assetDao.getAssetById(id)
            entity?.toAsset()
        }
    }

    suspend fun saveOfflineData() {
        try {
            Log.d("AssetRepository", "Saving offline data...")
            val response = apiService.getAssets()
            val entities = response.data.map { it.toEntity(System.currentTimeMillis()) }
            assetDao.deleteAll()
            assetDao.insertAll(entities)
            Log.d("AssetRepository", "Offline data saved successfully: ${entities.size} assets")
        } catch (e: Exception) {
            Log.e("AssetRepository", "Error saving offline data", e)
        }
    }
}