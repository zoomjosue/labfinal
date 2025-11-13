package com.example.labfinal.data.model

data class Asset(
    val id: String,
    val symbol: String,
    val name: String,
    val priceUsd: Double,
    val changePercent24Hr: Double,
    val marketCapUsd: Double,
    val maxSupply: Double?,
    val supply: Double
)