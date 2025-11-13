package com.example.labfinal.navigation

import kotlinx.serialization.Serializable

@Serializable
object Assets

@Serializable
data class AssetDetail(val assetId: String)