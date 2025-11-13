package com.example.labfinal.ui.assets

import android.app.Application
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.util.Log
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
            Log.d("AssetsViewModel", "loadAssets called")
            _uiState.value = AssetsUiState(isLoading = true)

            try {
                val hasInternet = isNetworkAvailable()
                Log.d("AssetsViewModel", "Has internet: $hasInternet")

                val assets = repository.getAllAssets(forceRefresh = hasInternet)
                Log.d("AssetsViewModel", "Assets loaded: ${assets.size}")

                if (assets.isNotEmpty()) {
                    _uiState.value = AssetsUiState(
                        isLoading = false,
                        data = assets,
                        hasError = false,
                        dataSource = if (hasInternet) DataSource.NETWORK else DataSource.LOCAL,
                        lastUpdate = System.currentTimeMillis()
                    )
                    Log.d("AssetsViewModel", "UI updated with ${assets.size} assets")
                } else {
                    Log.e("AssetsViewModel", "No assets found")
                    _uiState.value = AssetsUiState(
                        isLoading = false,
                        data = emptyList(),
                        hasError = true,
                        dataSource = DataSource.UNKNOWN
                    )
                }
            } catch (e: Exception) {
                Log.e("AssetsViewModel", "Error loading assets", e)
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
            Log.d("AssetsViewModel", "saveOfflineData called")
            repository.saveOfflineData()
            // Recargar para mostrar los datos guardados
            loadAssets()
        }
    }

    private fun isNetworkAvailable(): Boolean {
        return try {
            val connectivityManager = getApplication<Application>()
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
            val network = connectivityManager.activeNetwork
            if (network == null) {
                Log.d("AssetsViewModel", "No active network")
                return false
            }
            val capabilities = connectivityManager.getNetworkCapabilities(network)
            if (capabilities == null) {
                Log.d("AssetsViewModel", "No network capabilities")
                return false
            }
            val hasInternet = capabilities.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
            Log.d("AssetsViewModel", "Network capabilities - has internet: $hasInternet")
            hasInternet
        } catch (e: Exception) {
            Log.e("AssetsViewModel", "Error checking network", e)
            false
        }
    }
}