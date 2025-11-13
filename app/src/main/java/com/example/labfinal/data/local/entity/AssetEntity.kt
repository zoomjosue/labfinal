package com.example.labfinal.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "assets")
data class AssetEntity(
    @PrimaryKey
    val id: String,
    val symbol: String,
    val name: String,
    val priceUsd: String,
    val changePercent24Hr: String,
    val marketCapUsd: String,
    val maxSupply: String?,
    val supply: String,
    val savedAt: Long
)