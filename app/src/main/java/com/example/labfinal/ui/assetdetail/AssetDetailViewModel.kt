package com.example.labfinal.ui.assetdetail

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.example.labfinal.data.local.AppDatabase
import com.example.labfinal.data.network.CoinCapApiService
import com.example.labfinal.data.repository.AssetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssetDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AssetRepository(
        assetDao = database.assetDao(),
        apiService = CoinCapApiService()
    )

    private val _uiState = MutableStateFlow(AssetDetailUiState())
    val uiState: StateFlow<AssetDetailUiState> = _uiState.asStateFlow()

    private val assetId: String = checkNotNull(savedStateHandle["assetId"])

    init {
        loadAsset()
    }

    fun loadAsset() {
        viewModelScope.launch {
            _uiState.value = AssetDetailUiState(isLoading = true)

            try {
                val asset = repository.getAssetById(assetId)

                if (asset != null) {
                    _uiState.value = AssetDetailUiState(
                        isLoading = false,
                        data = asset,
                        hasError = false
                    )
                } else {
                    _uiState.value = AssetDetailUiState(
                        isLoading = false,
                        data = null,
                        hasError = true
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AssetDetailUiState(
                    isLoading = false,
                    data = null,
                    hasError = true
                )
            }
        }
    }
}