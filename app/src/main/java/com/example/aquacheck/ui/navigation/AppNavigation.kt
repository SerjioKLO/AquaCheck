package com.example.aquacheck.ui.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.aquacheck.ui.screens.ChecklistScreen
import com.example.aquacheck.ui.screens.EvidenciasScreen
import com.example.aquacheck.ui.screens.InicioScreen
import com.example.aquacheck.ui.screens.LoginScreen
import com.example.aquacheck.ui.screens.PerfilScreen
import com.example.aquacheck.ui.screens.PreChequeoScreen
import com.example.aquacheck.ui.screens.ResumenScreen
import com.example.aquacheck.viewmodel.PreChequeoViewModel

@Composable
fun AppNavigation(
    modifier: Modifier = Modifier,
    navController: NavHostController = rememberNavController(),
    viewModel: PreChequeoViewModel = viewModel()
) {
    val context = LocalContext.current
    LaunchedEffect(Unit) {
        viewModel.initDatabase(context)
    }

    NavHost(
        navController = navController,
        startDestination = "login",
        modifier = modifier
    ) {
        composable("login") {
            LoginScreen(
                onLoginClick = { usuario, clave ->
                    viewModel.login(usuario, clave)
                },
                onLoginSuccess = {
                    navController.navigate("inicio") {
                        popUpTo("login") { inclusive = true }
                    }
                }
            )
        }
        composable("inicio") {
            InicioScreen(
                viewModel = viewModel,
                onNewPreChequeoClick = {
                    navController.navigate("prechequeo")
                },
                onNavigateToPerfil = {
                    navController.navigate("perfil")
                },
                onLogoutClick = {
                    navController.navigate("login") {
                        popUpTo(0) { inclusive = true }
                    }
                }
            )
        }
        composable("perfil") {
            PerfilScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                }
            )
        }
        composable("prechequeo") {
            PreChequeoScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToChecklist = { id ->
                    navController.navigate("checklist/$id")
                }
            )
        }
        composable("checklist/{id}") { backStackEntry ->
            val id = backStackEntry.arguments?.getString("id") ?: "1"
            ChecklistScreen(
                viewModel = viewModel,
                checklistId = id,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToEvidencias = {
                    navController.navigate("evidencias")
                }
            )
        }
        composable("evidencias") {
            EvidenciasScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onNavigateToResumen = {
                    navController.navigate("resumen")
                }
            )
        }
        composable("resumen") {
            ResumenScreen(
                viewModel = viewModel,
                onBackClick = {
                    navController.popBackStack()
                },
                onSaveAndFinish = {
                    navController.navigate("inicio") {
                        popUpTo("inicio") { inclusive = true }
                    }
                }
            )
        }
    }
}
