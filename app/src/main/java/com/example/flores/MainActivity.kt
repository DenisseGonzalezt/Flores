package com.example.flores

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.flores.ui.theme.FloresTheme
import com.google.firebase.firestore.ktx.firestore
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            // Estado global para controlar el Modo Oscuro
            var isDarkMode by remember { mutableStateOf(false) }

            FloresTheme(darkTheme = isDarkMode) {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    FlowerApp(
                        isDarkMode = isDarkMode,
                        onThemeChanged = { isDarkMode = it }
                    )
                }
            }
        }
    }
}

// 1. Modelo de datos
data class Flower(
    val id: Int = 0,
    val name: String = "",
    val price: Double = 0.0,
    val description: String = ""
)

// Catálogo inicial de respaldo local
val fallbackFlowers = mutableListOf(
    Flower(1, "Rosas Rojas Pasión", 250.0, "Hermoso ramo de 12 rosas rojas frescas y aromáticas."),
    Flower(2, "Girasoles Luminosos", 180.0, "Girasoles brillantes ideales para alegrar cualquier día."),
    Flower(3, "Tulipanes Holandeses", 320.0, "Elegantes tulipanes de importación en tonos variados."),
    Flower(4, "Orquídeas Imperiales", 450.0, "Finas orquídeas en maceta decorativa de alta calidad.")
)

// Función para obtener flores de Firebase Firestore
suspend fun getFlowersFromFirestore(): List<Flower> {
    return try {
        val db = Firebase.firestore
        val snapshot = db.collection("flowers").get().await()
        if (snapshot.isEmpty) {
            fallbackFlowers
        } else {
            var counter = 1
            snapshot.documents.map { document ->
                Flower(
                    id = document.getLong("id")?.toInt() ?: counter++,
                    name = document.getString("name") ?: "Sin nombre",
                    price = document.getDouble("price") ?: 0.0,
                    description = document.getString("description") ?: "Sin descripción"
                )
            }
        }
    } catch (e: Exception) {
        fallbackFlowers
    }
}

// Función para guardar una nueva flor en Firebase Firestore
suspend fun addFlowerToFirestore(flower: Flower): Boolean {
    return try {
        val db = Firebase.firestore
        val flowerData = hashMapOf(
            "id" to flower.id,
            "name" to flower.name,
            "price" to flower.price,
            "description" to flower.description
        )
        db.collection("flowers").add(flowerData).await()
        true
    } catch (e: Exception) {
        false
    }
}

// 2. Definición de Rutas de Navegación (Añadida la ruta de Configuración)
sealed class Screen(val route: String) {
    object Home : Screen("home_screen")
    object Catalog : Screen("catalog_screen")
    object AddBatch : Screen("add_batch_screen")
    object Inventory : Screen("inventory_screen")
    object Settings : Screen("settings_screen")
    object Detail : Screen("detail_screen/{flowerId}") {
        fun createRoute(flowerId: Int) = "detail_screen/$flowerId"
    }
}

// 3. Controlador Principal con Menú Desplegable (Drawer)
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FlowerApp(
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit
) {
    val navController = rememberNavController()
    var flowersList by remember { mutableStateOf<List<Flower>>(fallbackFlowers) }
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    LaunchedEffect(Unit) {
        val cloudFlowers = getFlowersFromFirestore()
        if (cloudFlowers.isNotEmpty()) {
            flowersList = cloudFlowers
        }
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            Surface(
                modifier = Modifier
                    .fillMaxHeight()
                    .width(300.dp),
                color = Color(0xFF1E1E1E)
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(vertical = 24.dp)
                ) {
                    Text(
                        text = "Florería POS",
                        modifier = Modifier.padding(16.dp),
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    HorizontalDivider(color = Color.Gray.copy(alpha = 0.3f))
                    Spacer(modifier = Modifier.height(8.dp))

                    NavigationDrawerItem(
                        label = { Text("Florería POS (Inicio)", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Home.route) {
                                popUpTo(Screen.Home.route) { inclusive = true }
                            }
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Gestionar Inventario", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Inventory.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Agregar Lote", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.AddBatch.route)
                        }
                    )
                    NavigationDrawerItem(
                        label = { Text("Ver Catálogo", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Catalog.route)
                        }
                    )
                    // Opción de Configuración añadida al menú lateral
                    NavigationDrawerItem(
                        label = { Text("⚙️ Configuración", color = Color.White) },
                        selected = false,
                        onClick = {
                            scope.launch { drawerState.close() }
                            navController.navigate(Screen.Settings.route)
                        }
                    )
                }
            }
        }
    ) {
        Scaffold(
            containerColor = Color.Transparent,
            topBar = {
                TopAppBar(
                    title = { Text("Florería POS", color = MaterialTheme.colorScheme.primary, fontWeight = FontWeight.Bold) },
                    navigationIcon = {
                        IconButton(onClick = {
                            scope.launch { drawerState.open() }
                        }) {
                            Icon(
                                imageVector = Icons.Default.Menu,
                                contentDescription = "Menú",
                                tint = MaterialTheme.colorScheme.primary
                            )
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(containerColor = MaterialTheme.colorScheme.background)
                )
            }
        ) { innerPadding ->
            Box(modifier = Modifier.padding(innerPadding)) {
                NavHost(
                    navController = navController,
                    startDestination = Screen.Home.route
                ) {
                    composable(route = Screen.Home.route) {
                        HomeScreen(
                            onNavigateToCatalog = { navController.navigate(Screen.Catalog.route) },
                            onNavigateToAddBatch = { navController.navigate(Screen.AddBatch.route) },
                            onNavigateToInventory = { navController.navigate(Screen.Inventory.route) }
                        )
                    }
                    composable(route = Screen.Catalog.route) {
                        CatalogScreen(
                            flowers = flowersList,
                            onFlowerClick = { flowerId -> navController.navigate(Screen.Detail.createRoute(flowerId)) },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(route = Screen.Inventory.route) {
                        InventoryScreen(
                            flowers = flowersList,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(route = Screen.AddBatch.route) {
                        AddBatchScreen(
                            onAddFlower = { newFlower ->
                                flowersList = flowersList + newFlower
                                navController.popBackStack()
                            },
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    // Pantalla de Configuración con Modo Oscuro
                    composable(route = Screen.Settings.route) {
                        SettingsScreen(
                            isDarkMode = isDarkMode,
                            onThemeChanged = onThemeChanged,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                    composable(
                        route = Screen.Detail.route,
                        arguments = listOf(navArgument("flowerId") { type = NavType.IntType })
                    ) { backStackEntry ->
                        val flowerId = backStackEntry.arguments?.getInt("flowerId") ?: 1
                        val flower = flowersList.find { it.id == flowerId } ?: flowersList.first()

                        DetailScreen(
                            flower = flower,
                            onBackClick = { navController.popBackStack() }
                        )
                    }
                }
            }
        }
    }
}

// 4. Pantallas de la Interfaz
@Composable
fun HomeScreen(
    onNavigateToCatalog: () -> Unit,
    onNavigateToAddBatch: () -> Unit,
    onNavigateToInventory: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier.fillMaxWidth()
        ) {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                modifier = Modifier.fillMaxWidth().padding(bottom = 24.dp)
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(text = "🌷✨🌿", fontSize = 28.sp)
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Bienvenido a Florería Flora",
                        fontSize = 22.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Donde cada detalle florece",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToInventory,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text("📦 Gestionar Inventario", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = onNavigateToAddBatch,
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text("🌿 Agregar Lote", fontSize = 16.sp, fontWeight = FontWeight.Medium)
            }
            Spacer(modifier = Modifier.height(16.dp))

            OutlinedButton(
                onClick = onNavigateToCatalog,
                shape = RoundedCornerShape(16.dp),
                border = ButtonDefaults.outlinedButtonBorder.copy(width = 2.dp),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.primary),
                modifier = Modifier.fillMaxWidth().height(54.dp)
            ) {
                Text("✨ Ver Catálogo", fontSize = 16.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

// Nueva Pantalla de Configuración con Interruptor de Modo Oscuro
@Composable
fun SettingsScreen(
    isDarkMode: Boolean,
    onThemeChanged: (Boolean) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "⚙️ Configuración",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Volver")
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        Card(
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Modo Oscuro",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.primary
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "Cambiar la apariencia de la app",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.secondary
                    )
                }
                Switch(
                    checked = isDarkMode,
                    onCheckedChange = { onThemeChanged(it) }
                )
            }
        }
    }
}

@Composable
fun InventoryScreen(
    flowers: List<Flower>,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "📦 Inventario Actual", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Volver")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(flowers) { flower ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = flower.name, fontWeight = FontWeight.Bold, fontSize = 18.sp, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "Stock disponible: 15 unidades", fontSize = 14.sp, color = MaterialTheme.colorScheme.secondary)
                        Text(text = "Precio unitario: $ ${flower.price} MXN", fontSize = 14.sp, color = Color.Gray)
                    }
                }
            }
        }
    }
}

@Composable
fun AddBatchScreen(
    onAddFlower: (Flower) -> Unit,
    onBackClick: () -> Unit
) {
    var name by remember { mutableStateOf("") }
    var priceText by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(text = "🌿 Nuevo Lote de Flores", fontSize = 24.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(24.dp))

            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre de la flor") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = priceText,
                onValueChange = { priceText = it },
                label = { Text("Precio (MXN)") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción del arreglo") },
                shape = RoundedCornerShape(12.dp),
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(24.dp))

            Button(
                onClick = {
                    val price = priceText.toDoubleOrNull() ?: 0.0
                    if (name.isNotBlank()) {
                        val newFlower = Flower(
                            id = (100..999).random(),
                            name = name,
                            price = price,
                            description = description.ifBlank { "Sin descripción" }
                        )
                        coroutineScope.launch {
                            addFlowerToFirestore(newFlower)
                            onAddFlower(newFlower)
                        }
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.fillMaxWidth().height(50.dp)
            ) {
                Text("Guardar Lote en la Nube", fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))
            TextButton(onClick = onBackClick) {
                Text("Cancelar", color = Color.Gray)
            }
        }
    }
}

@Composable
fun CatalogScreen(
    flowers: List<Flower>,
    onFlowerClick: (Int) -> Unit,
    onBackClick: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = "🌸 Catálogo Floral", fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
            Button(
                onClick = onBackClick,
                colors = ButtonDefaults.buttonColors(containerColor = Color.Gray),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Volver")
            }
        }
        Spacer(modifier = Modifier.height(16.dp))

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(flowers) { flower ->
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onFlowerClick(flower.id) }
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(text = flower.name, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(text = "$ ${flower.price} MXN", fontSize = 15.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.secondary)
                    }
                }
            }
        }
    }
}

@Composable
fun DetailScreen(
    flower: Flower,
    onBackClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(24.dp),
        contentAlignment = Alignment.Center
    ) {
        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "🌷", fontSize = 40.sp)
                Spacer(modifier = Modifier.height(8.dp))
                Text(text = flower.name, fontSize = 22.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Spacer(modifier = Modifier.height(12.dp))
                Text(text = "$ ${flower.price} MXN", fontSize = 20.sp, fontWeight = FontWeight.SemiBold, color = MaterialTheme.colorScheme.secondary)
                Spacer(modifier = Modifier.height(16.dp))
                Text(text = flower.description, fontSize = 14.sp, color = Color.Gray)
                Spacer(modifier = Modifier.height(32.dp))
                Button(
                    onClick = onBackClick,
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary),
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.fillMaxWidth().height(50.dp)
                ) {
                    Text("Regresar al Catálogo")
                }
            }
        }
    }
}