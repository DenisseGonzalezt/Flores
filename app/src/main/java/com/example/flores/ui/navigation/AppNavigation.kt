package com.example.flores.ui.navigation

import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flores.R
import com.example.flores.data.AppContainer
import com.example.flores.data.UserModel
import com.example.flores.ui.auth.AuthViewModel
import com.example.flores.ui.auth.AuthViewModelFactory
import com.example.flores.ui.screens.auth.ClientHomeScreen
import com.example.flores.ui.screens.auth.LoginScreen
import com.example.flores.ui.screens.auth.RegisterScreen
import com.example.flores.ui.screens.detail.DetailScreen
import com.example.flores.ui.screens.form.FormScreen
import com.example.flores.ui.screens.home.HomeScreen
import com.example.flores.ui.screens.inventory.InventoryScreen
import com.example.flores.ui.screens.inventory.ProductFormScreen
import com.example.flores.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {

    // Crea el ViewModel usando el repositorio de Firebase.
    val authViewModel: AuthViewModel = viewModel(
        factory = AuthViewModelFactory(AppContainer.authRepository)
    )

    // Observa los datos de autenticación.
    val user by authViewModel.user.collectAsState()
    val isLoading by authViewModel.isLoading.collectAsState()
    val message by authViewModel.message.collectAsState()
    val sessionChecked by authViewModel.sessionChecked.collectAsState()

    // Comprueba si existe una sesión activa al abrir la aplicación.
    LaunchedEffect(Unit) {
        authViewModel.checkSession()
    }

    // Mientras se comprueba la sesión, muestra un indicador de carga.
    if (!sessionChecked) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = androidx.compose.ui.Alignment.Center
        ) {
            CircularProgressIndicator()
        }
        return
    }

    // Si no existe un usuario autenticado, muestra Login y Registro.
    if (user == null) {

        val authNavController = rememberNavController()

        NavHost(
            navController = authNavController,
            startDestination = Screen.Login.route
        ) {

            composable(Screen.Login.route) {
                LoginScreen(
                    onLogin = { email, password ->
                        authViewModel.login(email, password)
                    },
                    onNavigateToRegister = {
                        authViewModel.clearMessage()
                        authNavController.navigate(Screen.Register.route)
                    },
                    isLoading = isLoading,
                    message = message
                )
            }

            composable(Screen.Register.route) {
                RegisterScreen(
                    onRegister = { name, email, password, phone, address ->
                        authViewModel.register(
                            name,
                            email,
                            password,
                            phone,
                            address
                        )
                    },
                    onNavigateToLogin = {
                        authViewModel.clearMessage()
                        authNavController.popBackStack()
                    },
                    isLoading = isLoading,
                    message = message
                )
            }
        }

    } else if (user?.role == UserModel.ADMINISTRADOR) {

        // Navegación exclusiva del administrador.
        AdminNavigation(
            onLogout = { authViewModel.logout() }
        )

    } else {

        // Pantalla principal del cliente.
        ClientHomeScreen(
            user = user!!,
            onLogout = {
                authViewModel.logout()
            }
        )
    }
}

// Navegación que conserva las pantallas originales del administrador.
@Composable
private fun AdminNavigation(
    onLogout: () -> Unit
) {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(
        initialValue = DrawerValue.Closed
    )
    val scope = rememberCoroutineScope()

    val openDrawerAction: () -> Unit = {
        scope.launch {
            drawerState.open()
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            ModalDrawerSheet {

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "${stringResource(id = R.string.app_name)} • Admin",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(16.dp)
                )

                HorizontalDivider()

                Spacer(modifier = Modifier.height(8.dp))

                NavigationDrawerItem(
                    label = { Text("Pedidos") },
                    selected = false,
                    icon = {
                        Icon(Icons.Default.Home, contentDescription = null)
                    },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) {
                                inclusive = true
                            }
                        }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Registrar Pedido") },
                    selected = false,
                    icon = {
                        Icon(Icons.Default.Add, contentDescription = null)
                    },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Form.route)
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Catálogo e Inventario") },
                    selected = false,
                    icon = {
                        Icon(Icons.Default.List, contentDescription = null)
                    },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Inventory.route)
                    }
                )

                HorizontalDivider(
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                NavigationDrawerItem(
                    label = {
                        Text(stringResource(id = R.string.menu_settings))
                    },
                    selected = false,
                    icon = {
                        Icon(Icons.Default.Settings, contentDescription = null)
                    },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Settings.route)
                    }
                )

                // Permite al administrador cerrar su sesión.
                NavigationDrawerItem(
                    label = { Text("Cerrar sesión") },
                    selected = false,
                    onClick = {
                        scope.launch { drawerState.close() }
                        onLogout()
                    }
                )
            }
        }
    ) {

        NavHost(
            navController = navController,
            startDestination = Screen.Home.route
        ) {

            composable(route = Screen.Home.route) {
                HomeScreen(
                    onNavigateToDetail = { itemId ->
                        navController.navigate(
                            Screen.Detail.createRoute(itemId)
                        )
                    },
                    onNavigateToForm = {
                        navController.navigate(Screen.Form.route)
                    },
                    onOpenDrawer = openDrawerAction
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(
                    navArgument("itemId") {
                        type = NavType.IntType
                    }
                )
            ) { backStackEntry ->

                val itemId =
                    backStackEntry.arguments?.getInt("itemId") ?: 0

                DetailScreen(
                    itemId = itemId,
                    onBack = {
                        navController.popBackStack()
                    },
                    onOpenDrawer = openDrawerAction
                )
            }

            composable(route = Screen.Form.route) {
                FormScreen(
                    onBack = {
                        navController.popBackStack()
                    },
                    onOpenDrawer = openDrawerAction
                )
            }

            composable(route = Screen.Inventory.route) {
                InventoryScreen(
                    onNavigateToProductForm = {
                        navController.navigate(Screen.ProductForm.route)
                    },
                    onOpenDrawer = openDrawerAction
                )
            }

            composable(route = Screen.ProductForm.route) {
                ProductFormScreen(
                    onBack = {
                        navController.popBackStack()
                    }
                )
            }

            composable(route = Screen.Settings.route) {
                SettingsScreen(
                    onOpenDrawer = openDrawerAction
                )
            }
        }
    }
}
