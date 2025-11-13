package com.example.labfinal.ui.assets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labfinal.data.model.Asset
import com.example.labfinal.ui.components.ErrorScreen
import com.example.labfinal.ui.components.LoadingScreen
import java.text.NumberFormat
import java.util.Locale
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetsScreen(
    onAssetClick: (String) -> Unit,
    viewModel: AssetsViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = { Text("Criptomonedas") },
                actions = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                viewModel.saveOfflineData()
                                snackbarHostState.showSnackbar(
                                    message = "Datos guardados para ver offline",
                                    duration = SnackbarDuration.Short
                                )
                            }
                        }
                    ) {
                        Icon(
                            Icons.Default.Refresh,
                            contentDescription = "Guardar para ver offline"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    actionIconContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        when {
            uiState.isLoading -> {
                LoadingScreen()
            }
            uiState.hasError -> {
                ErrorScreen(
                    errorMessage = "Error al obtener las criptomonedas.\nIntenta de nuevo",
                    onRetry = { viewModel.loadAssets() }
                )
            }
            else -> {
                Column(modifier = Modifier.padding(padding)) {
                    DataSourceIndicator(
                        dataSource = uiState.dataSource,
                        timestamp = uiState.lastUpdate
                    )
                    LazyColumn {
                        items(uiState.data) { asset ->
                            AssetListItem(asset = asset, onClick = onAssetClick)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DataSourceIndicator(dataSource: DataSource, timestamp: Long?) {
    val text = when (dataSource) {
        DataSource.NETWORK -> "Viendo data más reciente"
        DataSource.LOCAL -> {
            if (timestamp != null) {
                val dateFormat = java.text.SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                "Viendo data del ${dateFormat.format(timestamp)}"
            } else {
                "Viendo data guardada"
            }
        }
        DataSource.UNKNOWN -> ""
    }

    if (text.isNotEmpty()) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = if (dataSource == DataSource.NETWORK)
                MaterialTheme.colorScheme.primaryContainer
            else
                MaterialTheme.colorScheme.secondaryContainer
        ) {
            Text(
                text = text,
                modifier = Modifier.padding(8.dp),
                style = MaterialTheme.typography.bodySmall,
                color = if (dataSource == DataSource.NETWORK)
                    MaterialTheme.colorScheme.onPrimaryContainer
                else
                    MaterialTheme.colorScheme.onSecondaryContainer
            )
        }
    }
}

@Composable
fun AssetListItem(asset: Asset, onClick: (String) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clickable { onClick(asset.id) },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = asset.name,
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp
                )
                Text(
                    text = asset.symbol,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            Column(horizontalAlignment = Alignment.End) {
                Text(
                    text = formatCurrency(asset.priceUsd),
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 16.sp
                )
                Text(
                    text = "${String.format("%.2f", asset.changePercent24Hr)}%",
                    style = MaterialTheme.typography.bodySmall,
                    color = if (asset.changePercent24Hr >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }
    }
}

fun formatCurrency(value: Double): String {
    val formatter = NumberFormat.getCurrencyInstance(Locale.US)
    return formatter.format(value)
}