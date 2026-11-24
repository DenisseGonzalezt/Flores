package com.example.flores.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flores.R
import com.example.flores.ui.screens.detail.DetailScreen
import com.example.flores.ui.screens.form.FormScreen
import com.example.flores.ui.screens.home.HomeScreen
import com.example.flores.ui.screens.inventory.InventoryScreen
import com.example.flores.ui.screens.inventory.ProductFormScreen
import com.example.flores.ui.screens.settings.SettingsScreen
import kotlinx.coroutines.launch

@Composable
fun AppNavigation() {
    val navController = rememberNavController()
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val openDrawerAction: () -> Unit = {
        scope.launch { drawerState.open() }
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
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Home.route) {
                            popUpTo(Screen.Home.route) { inclusive = true }
                        }
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Registrar Pedido") },
                    selected = false,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Form.route)
                    }
                )

                NavigationDrawerItem(
                    label = { Text("Catálogo e Inventario") },
                    selected = false,
                    icon = { Icon(Icons.Default.List, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Inventory.route)
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))

                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.menu_settings)) },
                    selected = false,
                    icon = { Icon(Icons.Default.Settings, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Settings.route)
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
                        navController.navigate(Screen.Detail.createRoute(itemId))
                    },
                    onNavigateToForm = {
                        navController.navigate(Screen.Form.route)
                    },
                    onOpenDrawer = openDrawerAction
                )
            }

            composable(
                route = Screen.Detail.route,
                arguments = listOf(navArgument("itemId") { type = NavType.IntType })
            ) { backStackEntry ->
                val itemId = backStackEntry.arguments?.getInt("itemId") ?: 0
                DetailScreen(
                    itemId = itemId,
                    onBack = { navController.popBackStack() },
                    onOpenDrawer = openDrawerAction
                )
            }

            composable(route = Screen.Form.route) {
                FormScreen(
                    onBack = { navController.popBackStack() },
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
                    onBack = { navController.popBackStack() }
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