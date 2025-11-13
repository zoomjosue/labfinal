package com.example.labfinal.ui.assets

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.labfinal.data.local.AppDatabase
import com.example.labfinal.data.network.CoinCapApiService
import com.example.labfinal.data.repository.AssetRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AssetsViewModel(application: Application) : AndroidViewModel(application) {

    private val database = AppDatabase.getDatabase(application)
    private val repository = AssetRepository(
        assetDao = database.assetDao(),
        apiService = CoinCapApiService()
    )

    private val _uiState = MutableStateFlow(AssetsUiState())
    val uiState: StateFlow<AssetsUiState> = _uiState.asStateFlow()

    init {
        loadAssets()
    }

    fun loadAssets() {
        viewModelScope.launch {
            _uiState.value = AssetsUiState(isLoading = true)

            try {
                val hasInternet = isNetworkAvailable()
                val assets = repository.getAllAssets(forceRefresh = hasInternet)

                if (assets.isNotEmpty()) {
                    _uiState.value = AssetsUiState(
                        isLoading = false,
                        data = assets,
                        hasError = false,
                        dataSource = if (hasInternet) DataSource.NETWORK else DataSource.LOCAL
                    )
                } else {
                    _uiState.value = AssetsUiState(
                        isLoading = false,
                        data = emptyList(),
                        hasError = true,
                        dataSource = DataSource.UNKNOWN
                    )
                }
            } catch (e: Exception) {
                _uiState.value = AssetsUiState(
                    isLoading = false,
                    data = emptyList(),
                    hasError = true,
                    dataSource = DataSource.UNKNOWN
                )
            }
        }
    }

    fun saveOfflineData() {
        viewModelScope.launch {
            repository.saveOfflineData()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val connectivityManager = getApplication<Application>()
            .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val network = connectivityManager.activeNetwork ?: return false
        val capabilities = connectivityManager.getNetworkCapabilities(network) ?: return false
        return capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    }
}