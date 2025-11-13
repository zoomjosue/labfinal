package com.example.labfinal.ui.assetdetail

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.labfinal.data.model.Asset
import com.example.labfinal.ui.assets.formatCurrency
import com.example.labfinal.ui.components.ErrorScreen
import com.example.labfinal.ui.components.LoadingScreen
import java.text.NumberFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AssetDetailScreen(
    assetId: String,
    onBack: () -> Unit,
    viewModel: AssetDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detalle") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            painter = painterResource(android.R.drawable.ic_menu_revert),
                            contentDescription = "Back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary,
                    navigationIconContentColor = MaterialTheme.colorScheme.onPrimary
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
                    errorMessage = "Error al obtener información.\nIntenta de nuevo",
                    onRetry = { viewModel.loadAsset() }
                )
            }
            uiState.data != null -> {
                AssetDetailContent(
                    asset = uiState.data!!,
                    modifier = Modifier.padding(padding)
                )
            }
        }
    }
}

@Composable
fun AssetDetailContent(
    asset: Asset,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = asset.name,
            fontWeight = FontWeight.Bold,
            fontSize = 28.sp,
            textAlign = TextAlign.Center
        )

        Text(
            text = asset.symbol,
            fontSize = 18.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(Modifier.height(32.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    text = formatCurrency(asset.priceUsd),
                    fontWeight = FontWeight.Bold,
                    fontSize = 32.sp
                )

                Spacer(Modifier.height(8.dp))

                Text(
                    text = "${String.format("%.2f", asset.changePercent24Hr)}%",
                    fontSize = 20.sp,
                    color = if (asset.changePercent24Hr >= 0) Color(0xFF4CAF50) else Color(0xFFF44336)
                )
            }
        }

        Spacer(Modifier.height(32.dp))

        Column(
            modifier = Modifier.fillMaxWidth()
        ) {
            InfoRow(label = "Supply:", value = formatNumber(asset.supply))
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow(
                label = "Máximo Supply:",
                value = asset.maxSupply?.let { formatNumber(it) } ?: "N/A"
            )
            Divider(modifier = Modifier.padding(vertical = 12.dp))
            InfoRow(label = "Market Cap USD:", value = formatCurrency(asset.marketCapUsd))
        }
    }
}

@Composable
fun InfoRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.bodyLarge,
            fontWeight = FontWeight.Medium
        )
        Text(
            text = value,
            style = MaterialTheme.typography.bodyLarge,
            textAlign = TextAlign.End
        )
    }
}

fun formatNumber(value: Double): String {
    val formatter = NumberFormat.getNumberInstance(Locale.US)
    formatter.maximumFractionDigits = 2
    return formatter.format(value)
}