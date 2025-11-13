package com.example.labfinal.data.network.mappers

import com.example.labfinal.data.local.entity.AssetEntity
import com.example.labfinal.data.model.Asset
import com.example.labfinal.data.network.dto.AssetDto

fun AssetDto.toEntity(timestamp: Long): AssetEntity {
    return AssetEntity(
        id = this.id,
        symbol = this.symbol,
        name = this.name,
        priceUsd = this.priceUsd,
        changePercent24Hr = this.changePercent24Hr,
        marketCapUsd = this.marketCapUsd,
        maxSupply = this.maxSupply,
        supply = this.supply,
        savedAt = timestamp
    )
}

fun AssetEntity.toAsset(): Asset {
    return Asset(
        id = this.id,
        symbol = this.symbol,
        name = this.name,
        priceUsd = this.priceUsd.toDoubleOrNull() ?: 0.0,
        changePercent24Hr = this.changePercent24Hr.toDoubleOrNull() ?: 0.0,
        marketCapUsd = this.marketCapUsd.toDoubleOrNull() ?: 0.0,
        maxSupply = this.maxSupply?.toDoubleOrNull(),
        supply = this.supply.toDoubleOrNull() ?: 0.0
    )
}