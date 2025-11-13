package com.example.labfinal.data.network

import com.example.labfinal.data.network.dto.AssetResponseDto
import com.example.labfinal.data.network.dto.SingleAssetResponseDto
import io.ktor.client.call.*
import io.ktor.client.request.*

class CoinCapApiService {
    private val client = KtorClient.httpClient

    suspend fun getAssets(): AssetResponseDto {
        return client.get("assets").body()
    }

    suspend fun getAssetById(id: String): SingleAssetResponseDto {
        return client.get("assets/$id").body()
    }
}