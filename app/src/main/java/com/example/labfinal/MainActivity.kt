package com.example.labfinal

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.labfinal.navigation.AssetDetail
import com.example.labfinal.navigation.Assets
import com.example.labfinal.ui.assetdetail.AssetDetailScreen
import com.example.labfinal.ui.assets.AssetsScreen
import com.example.labfinal.ui.theme.LabFinalTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            LabFinalTheme {
                Surface(modifier = Modifier.fillMaxSize()) {
                    AppNavHost()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppNavHost() {
    val navController = rememberNavController()

    NavHost(navController = navController, startDestination = Assets) {
        composable<Assets> {
            BackHandler {
                (navController.context as? ComponentActivity)?.finish()
            }

            AssetsScreen(
                onAssetClick = { id ->
                    navController.navigate(AssetDetail(id))
                }
            )
        }

        composable<AssetDetail> { backStackEntry ->
            val detail = backStackEntry.toRoute<AssetDetail>()
            AssetDetailScreen(
                assetId = detail.assetId,
                onBack = { navController.popBackStack() }
            )
        }
    }
}