package com.example.labfinal.data.network.dto

import kotlinx.serialization.Serializable

@Serializable
data class AssetResponseDto(
    val data: List<AssetDto>,
    val timestamp: Long
)

// Para un solo asset (/assets/{id})
@Serializable
data class SingleAssetResponseDto(
    val data: AssetDto,
    val timestamp: Long
)

@Serializable
data class AssetDto(
    val id: String,
    val rank: String,
    val symbol: String,
    val name: String,
    val supply: String,
    val maxSupply: String?,
    val marketCapUsd: String,
    val volumeUsd24Hr: String,
    val priceUsd: String,
    val changePercent24Hr: String,
    val vwap24Hr: String?
)