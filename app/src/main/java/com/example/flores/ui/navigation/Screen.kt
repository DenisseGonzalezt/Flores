package com.example.flores.ui.navigation

// Define todas las rutas de navegación de la aplicación.
sealed class Screen(val route: String) {

    // Pantallas de autenticación.
    object Login : Screen("login_screen")
    object Register : Screen("register_screen")
    object ClientHome : Screen("client_home_screen")

    // Pantallas que ya tenía el proyecto.
    object Home : Screen("home_screen")

    object Detail : Screen("detail_screen/{itemId}") {
        fun createRoute(itemId: Int) = "detail_screen/$itemId"
    }

    object Form : Screen("form_screen")
    object Inventory : Screen("inventory_screen")
    object ProductForm : Screen("product_form_screen")
    object Settings : Screen("settings_screen")
}