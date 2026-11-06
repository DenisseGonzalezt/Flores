package com.example.flores.ui.navigation

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalDrawerSheet
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.Text
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
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
                    text = stringResource(id = R.string.app_name),
                    modifier = Modifier.padding(16.dp)
                )
                Spacer(modifier = Modifier.height(16.dp))

                NavigationDrawerItem(
                    label = { Text(stringResource(id = R.string.menu_home)) },
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
                    label = { Text(stringResource(id = R.string.menu_new_record)) },
                    selected = false,
                    icon = { Icon(Icons.Default.Add, contentDescription = null) },
                    onClick = {
                        scope.launch { drawerState.close() }
                        navController.navigate(Screen.Form.route)
                    }
                )

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

            composable(route = Screen.Settings.route) {
                SettingsScreen(
                    onOpenDrawer = openDrawerAction
                )
            }
        }
    }
}