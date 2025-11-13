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
            return fetchFromNetworkAndSave()
        } else {
            val localAssets = assetDao.getAllAssets()
            Log.d("AssetRepository", "Local assets count: ${localAssets.size}")

            return if (localAssets.isNotEmpty()) {
                Log.d("AssetRepository", "Devolviendo datos locales (offline)")
                localAssets.map { it.toAsset() }
            } else {
                Log.d("AssetRepository", "No hay datos locales → intentando red...")
                fetchFromNetworkAndSave()
            }
        }
    }

    // Función auxiliar para no repetir código
    private suspend fun fetchFromNetworkAndSave(): List<Asset> {
        return try {
            Log.d("AssetRepository", "→ Haciendo petición GET a https://api.coincap.io/v2/assets")

            val response = apiService.getAssets()

            Log.d("AssetRepository", "✓ Respuesta recibida correctamente")
            Log.d("AssetRepository", "Timestamp del servidor: ${response.timestamp}")
            Log.d("AssetRepository", "Cantidad de criptos recibidas: ${response.data.size}")

            val entities = response.data.map { it.toEntity(System.currentTimeMillis()) }

            assetDao.deleteAll()
            assetDao.insertAll(entities)
            Log.d("AssetRepository", "Datos guardados en Room correctamente")

            entities.map { it.toAsset() }

        } catch (e: Exception) {
            // AQUÍ ESTÁ EL LOG QUE NOS VA A DECIR EL ERROR REAL
            Log.e("AssetRepository", "ERROR EXACTO AL CONECTAR A COINCAP:", e)
            Log.e("AssetRepository", "Tipo de excepción: ${e.javaClass.simpleName}")
            Log.e("AssetRepository", "Mensaje: ${e.message}")
            e.printStackTrace()

            // Intenta devolver datos locales aunque falle la red
            val local = assetDao.getAllAssets()
            if (local.isNotEmpty()) {
                Log.d("AssetRepository", "Red falló pero hay datos locales → devolviendo ${local.size}")
                return local.map { it.toAsset() }
            }

            Log.w("AssetRepository", "No hay datos locales ni conexión → devolviendo lista vacía")
            emptyList()
        }
    }

    // El resto de funciones las dejamos igual (solo con un pequeño log extra)
    suspend fun getAssetById(id: String): Asset? {
        Log.d("AssetRepository", "getAssetById called for id: $id")
        // ... (igual que antes)
        return try {
            val response = apiService.getAssetById(id)
            val entity = response.data.toEntity(System.currentTimeMillis())
            assetDao.insertAll(listOf(entity))
            entity.toAsset()
        } catch (e: Exception) {
            Log.e("AssetRepository", "Error detallado en getAssetById:", e)
            assetDao.getAssetById(id)?.toAsset()
        }
    }

    suspend fun saveOfflineData() {
        try {
            Log.d("AssetRepository", "saveOfflineData iniciado...")
            val response = apiService.getAssets()
            val entities = response.data.map { it.toEntity(System.currentTimeMillis()) }
            assetDao.deleteAll()
            assetDao.insertAll(entities)
            Log.d("AssetRepository", "saveOfflineData → ${entities.size} criptos guardadas")
        } catch (e: Exception) {
            Log.e("AssetRepository", "ERROR EN saveOfflineData:", e)
            e.printStackTrace()
        }
    }
}