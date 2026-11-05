package com.tuempresa.floreria
sealed class Screen(val route: String) {
    object Catalog : Screen("catalog_screen")
    object Detail : Screen("detail_screen")
}