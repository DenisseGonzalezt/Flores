package com.example.flores.ui.screens.inventory

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.flores.R
import com.example.flores.data.AppContainer
import com.example.flores.data.ProductModel
import com.example.flores.data.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class ProductFormUiState(
    val nameInput: String = "",
    val descriptionInput: String = "",
    val selectedCategory: String = "Ramos",
    val priceInput: String = "",
    val stockInput: String = "10",
    val minStockInput: String = "5",
    val isSaving: Boolean = false
)

class ProductFormViewModel(
    private val repository: ProductRepository = AppContainer.productRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(ProductFormUiState())
    val uiState: StateFlow<ProductFormUiState> = _uiState.asStateFlow()

    fun onNameChanged(name: String) {
        _uiState.value = _uiState.value.copy(nameInput = name)
    }

    fun onDescriptionChanged(description: String) {
        _uiState.value = _uiState.value.copy(descriptionInput = description)
    }

    fun onCategorySelected(category: String) {
        _uiState.value = _uiState.value.copy(selectedCategory = category)
    }

    fun onPriceChanged(price: String) {
        if (price.count { it == '.' } <= 1 && price.all { it.isDigit() || it == '.' }) {
            _uiState.value = _uiState.value.copy(priceInput = price)
        }
    }

    fun onStockChanged(stock: String) {
        if (stock.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(stockInput = stock)
        }
    }

    fun onMinStockChanged(minStock: String) {
        if (minStock.all { it.isDigit() }) {
            _uiState.value = _uiState.value.copy(minStockInput = minStock)
        }
    }

    fun saveProduct(onSuccess: () -> Unit) {
        val state = _uiState.value
        if (state.nameInput.isBlank()) return

        _uiState.value = state.copy(isSaving = true)

        viewModelScope.launch {
            try {
                val product = ProductModel(
                    id = 0,
                    name = state.nameInput.trim(),
                    description = state.descriptionInput.trim(),
                    category = state.selectedCategory,
                    price = state.priceInput.toDoubleOrNull() ?: 0.0,
                    stock = state.stockInput.toIntOrNull() ?: 0,
                    minStock = state.minStockInput.toIntOrNull() ?: 5
                )
                repository.insertProduct(product)
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                _uiState.value = _uiState.value.copy(isSaving = false)
                onSuccess()
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProductFormScreen(
    onBack: () -> Unit,
    viewModel: ProductFormViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val scrollState = rememberScrollState()
    val productCategories = listOf("Ramos", "Flores sueltas", "Plantas", "Arreglos especiales")

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Registrar Producto en Catálogo") },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = stringResource(id = R.string.btn_back)
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .verticalScroll(scrollState)
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            OutlinedTextField(
                value = uiState.nameInput,
                onValueChange = { viewModel.onNameChanged(it) },
                label = { Text("Nombre de la flor o arreglo") },
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.descriptionInput,
                onValueChange = { viewModel.onDescriptionChanged(it) },
                label = { Text(stringResource(id = R.string.label_description)) },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.priceInput,
                onValueChange = { viewModel.onPriceChanged(it) },
                label = { Text(stringResource(id = R.string.inv_input_price)) },
                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Decimal),
                singleLine = true,
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedTextField(
                    value = uiState.stockInput,
                    onValueChange = { viewModel.onStockChanged(it) },
                    label = { Text("Lote inicial (Stock)") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )

                OutlinedTextField(
                    value = uiState.minStockInput,
                    onValueChange = { viewModel.onMinStockChanged(it) },
                    label = { Text(stringResource(id = R.string.inv_input_min_stock)) },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    singleLine = true,
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                text = stringResource(id = R.string.label_category),
                style = MaterialTheme.typography.titleMedium,
                modifier = Modifier.align(Alignment.Start)
            )

            productCategories.forEach { category ->
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { viewModel.onCategorySelected(category) }
                        .padding(vertical = 4.dp)
                ) {
                    RadioButton(
                        selected = (uiState.selectedCategory == category),
                        onClick = { viewModel.onCategorySelected(category) }
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = category)
                }
            }

            Spacer(modifier = Modifier.height(24.dp))

            if (uiState.isSaving) {
                CircularProgressIndicator()
            } else {
                Button(
                    onClick = { viewModel.saveProduct(onSuccess = onBack) },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(text = stringResource(id = R.string.btn_save))
                }
            }
        }
    }
}