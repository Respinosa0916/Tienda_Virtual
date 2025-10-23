package com.tienda.tienda_virtual.ui.screens.vendor

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.PhotoCamera
import androidx.compose.material.icons.filled.Image
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.tienda.tienda_virtual.data.model.ProductWithCategory
import com.tienda.tienda_virtual.ui.viewmodel.ProductViewModel
import com.tienda.tienda_virtual.utils.ImageUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProductScreen(
    productWithCategory: ProductWithCategory,
    onBackClick: () -> Unit,
    onProductUpdated: () -> Unit,
    onProductDeleted: () -> Unit,
    productViewModel: ProductViewModel
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    
    var name by remember { mutableStateOf(productWithCategory.product.name) }
    var description by remember { mutableStateOf(productWithCategory.product.description) }
    var price by remember { mutableStateOf(productWithCategory.product.price.toString()) }
    var stock by remember { mutableStateOf(productWithCategory.product.stock.toString()) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(
        ImageUtils.getUriFromPath(productWithCategory.product.imageUrl)
    ) }
    var currentImagePath by remember { mutableStateOf(productWithCategory.product.imageUrl) }
    var imageChanged by remember { mutableStateOf(false) }
    var isActive by remember { mutableStateOf(productWithCategory.product.isActive) }
    var selectedCategoryId by remember { mutableStateOf(productWithCategory.product.categoryId) }
    var isLoading by remember { mutableStateOf(false) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var showDeleteDialog by remember { mutableStateOf(false) }
    
    val categories by productViewModel.allCategories.collectAsState(initial = emptyList())
    
    // Launcher para seleccionar imagen de la galería
    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        selectedImageUri = uri
        imageChanged = true
    }
    
    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Editar Producto") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            },
            actions = {
                IconButton(onClick = { showDeleteDialog = true }) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Eliminar",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        )
        
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Product name
            OutlinedTextField(
                value = name,
                onValueChange = { name = it },
                label = { Text("Nombre del producto") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Description
            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Descripción") },
                modifier = Modifier.fillMaxWidth(),
                minLines = 3
            )
            
            // Price
            OutlinedTextField(
                value = price,
                onValueChange = { price = it },
                label = { Text("Precio") },
                modifier = Modifier.fillMaxWidth(),
                prefix = { Text("$") }
            )
            
            // Stock
            OutlinedTextField(
                value = stock,
                onValueChange = { stock = it },
                label = { Text("Stock") },
                modifier = Modifier.fillMaxWidth()
            )
            
            // Estado activo/inactivo
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Producto activo",
                        style = MaterialTheme.typography.titleMedium
                    )
                    Switch(
                        checked = isActive,
                        onCheckedChange = { isActive = it }
                    )
                }
            }
            
            // Selector de imagen
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Text(
                        text = "Imagen del Producto",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    
                    // Preview de la imagen
                    if (selectedImageUri != null) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.primary,
                                    RoundedCornerShape(12.dp)
                                )
                        ) {
                            AsyncImage(
                                model = selectedImageUri,
                                contentDescription = "Imagen del producto",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }
                        
                        TextButton(
                            onClick = { selectedImageUri = null }
                        ) {
                            Text("Quitar imagen")
                        }
                    } else {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(200.dp)
                                .clip(RoundedCornerShape(12.dp))
                                .border(
                                    2.dp,
                                    MaterialTheme.colorScheme.outline,
                                    RoundedCornerShape(12.dp)
                                )
                                .clickable { imagePickerLauncher.launch("image/*") },
                            contentAlignment = Alignment.Center
                        ) {
                            Column(
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Image,
                                    contentDescription = "Seleccionar imagen",
                                    modifier = Modifier.size(64.dp),
                                    tint = MaterialTheme.colorScheme.primary
                                )
                                Text(
                                    text = "Toca para seleccionar imagen",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                    
                    Button(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.fillMaxWidth(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.primary
                        )
                    ) {
                        Icon(
                            imageVector = Icons.Default.PhotoCamera,
                            contentDescription = null,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Cambiar Imagen")
                    }
                }
            }
            
            // Category selection
            Card(
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp)
                ) {
                    Text(
                        text = "Categoría",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    categories.forEach { category ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            RadioButton(
                                selected = selectedCategoryId == category.id,
                                onClick = { selectedCategoryId = category.id }
                            )
                            Text(
                                text = category.name,
                                modifier = Modifier.padding(start = 8.dp)
                            )
                        }
                    }
                }
            }
            
            // Update button
            Button(
                onClick = {
                    if (name.isNotBlank() && description.isNotBlank() && 
                        price.isNotBlank() && stock.isNotBlank()) {
                        
                        isLoading = true
                        errorMessage = null
                        
                        coroutineScope.launch {
                            try {
                                // Si se cambió la imagen, guardar la nueva
                                val finalImagePath = if (imageChanged && selectedImageUri != null) {
                                    withContext(Dispatchers.IO) {
                                        // Eliminar la imagen anterior si existe y no es URL
                                        if (!ImageUtils.isHttpUrl(currentImagePath)) {
                                            ImageUtils.deleteImageFromInternalStorage(currentImagePath)
                                        }
                                        // Guardar la nueva imagen
                                        ImageUtils.saveImageToInternalStorage(context, selectedImageUri!!)
                                    }
                                } else if (selectedImageUri == null) {
                                    // Si se quitó la imagen
                                    withContext(Dispatchers.IO) {
                                        if (!ImageUtils.isHttpUrl(currentImagePath)) {
                                            ImageUtils.deleteImageFromInternalStorage(currentImagePath)
                                        }
                                    }
                                    null
                                } else {
                                    // Mantener la imagen actual
                                    currentImagePath
                                }
                                
                                val updatedProduct = productWithCategory.product.copy(
                                    name = name,
                                    description = description,
                                    price = price.toDouble(),
                                    stock = stock.toInt(),
                                    categoryId = selectedCategoryId,
                                    imageUrl = finalImagePath,
                                    isActive = isActive
                                )
                                
                                // Aquí llamarías a productViewModel.updateProduct(updatedProduct)
                                // Por ahora simulamos el éxito
                                onProductUpdated()
                            } catch (e: Exception) {
                                errorMessage = "Error al actualizar el producto: ${e.message}"
                                isLoading = false
                            }
                        }
                    } else {
                        errorMessage = "Por favor completa todos los campos requeridos"
                    }
                },
                enabled = !isLoading && name.isNotBlank() && description.isNotBlank() && 
                         price.isNotBlank() && stock.isNotBlank(),
                modifier = Modifier.fillMaxWidth(),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.primary
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(16.dp),
                        color = Color.White
                    )
                } else {
                    Text("Guardar Cambios")
                }
            }
            
            // Error message
            errorMessage?.let { error ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(
                        containerColor = MaterialTheme.colorScheme.errorContainer
                    )
                ) {
                    Text(
                        text = error,
                        color = MaterialTheme.colorScheme.onErrorContainer,
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
    
    // Delete confirmation dialog
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Eliminar Producto") },
            text = { Text("¿Estás seguro de que deseas eliminar este producto? Esta acción no se puede deshacer.") },
            confirmButton = {
                Button(
                    onClick = {
                        showDeleteDialog = false
                        // Aquí llamarías a productViewModel.deleteProduct(product)
                        onProductDeleted()
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.error
                    )
                ) {
                    Text("Eliminar")
                }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) {
                    Text("Cancelar")
                }
            }
        )
    }
}

