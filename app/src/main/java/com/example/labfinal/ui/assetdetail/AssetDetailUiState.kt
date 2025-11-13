package com.example.labfinal.ui.assetdetail

import com.example.labfinal.data.model.Asset

data class AssetDetailUiState(
    val isLoading: Boolean = false,
    val data: Asset? = null,
    val hasError: Boolean = false
)