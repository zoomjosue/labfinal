package com.example.labfinal.ui.assets

import com.example.labfinal.data.model.Asset

data class AssetsUiState(
    val isLoading: Boolean = false,
    val data: List<Asset> = emptyList(),
    val hasError: Boolean = false,
    val dataSource: DataSource = DataSource.UNKNOWN
)

enum class DataSource {
    NETWORK,
    LOCAL,
    UNKNOWN
}