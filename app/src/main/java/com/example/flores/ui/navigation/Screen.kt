package com.example.flores.ui.navigation

sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Detail : Screen("detail_screen/{itemId}") {
        fun createRoute(itemId: Int) = "detail_screen/$itemId"
    }
    object Form : Screen("form_screen")
    object Inventory : Screen("inventory_screen")
    object ProductForm : Screen("product_form_screen")
    object Settings : Screen("settings_screen")
}