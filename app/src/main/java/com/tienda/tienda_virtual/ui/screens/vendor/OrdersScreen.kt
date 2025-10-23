package com.tienda.tienda_virtual.ui.screens.vendor

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.tienda.tienda_virtual.data.model.Order
import com.tienda.tienda_virtual.data.model.OrderStatus
import com.tienda.tienda_virtual.data.model.OrderWithProducts
import com.tienda.tienda_virtual.ui.viewmodel.OrderViewModel
import com.tienda.tienda_virtual.utils.ImageUtils
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun OrdersScreen(
    vendorId: String,
    onBackClick: () -> Unit,
    orderViewModel: OrderViewModel
) {
    // Load orders with products for this vendor
    LaunchedEffect(vendorId) {
        orderViewModel.getOrdersByVendorWithProducts(vendorId)
    }
    
    val ordersWithProducts by orderViewModel.ordersWithProducts.collectAsState(initial = emptyList())
    
    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // Top App Bar
        TopAppBar(
            title = { Text("Órdenes") },
            navigationIcon = {
                IconButton(onClick = onBackClick) {
                    Icon(
                        imageVector = Icons.Default.ArrowBack,
                        contentDescription = "Volver"
                    )
                }
            }
        )
        
        if (ordersWithProducts.isEmpty()) {
            // Empty orders
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(
                        imageVector = Icons.Default.Receipt,
                        contentDescription = "Sin órdenes",
                        modifier = Modifier.size(64.dp),
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "No hay órdenes disponibles",
                        style = MaterialTheme.typography.titleLarge,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(ordersWithProducts) { orderWithProducts ->
                    OrderCardWithProducts(
                        orderWithProducts = orderWithProducts,
                        onStatusChange = { newStatus ->
                            orderViewModel.updateOrderStatus(orderWithProducts.order.id, newStatus)
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun OrderCardWithProducts(
    orderWithProducts: OrderWithProducts,
    onStatusChange: (OrderStatus) -> Unit
) {
    val order = orderWithProducts.order
    val dateFormat = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
    val formattedDate = dateFormat.format(Date(order.createdAt))
    
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Order header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Orden #${order.id.take(8)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                
                StatusChip(status = order.status)
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Products list
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(
                    containerColor = MaterialTheme.colorScheme.surfaceVariant
                )
            ) {
                Column(
                    modifier = Modifier.padding(12.dp)
                ) {
                    Text(
                        text = "Productos:",
                        style = MaterialTheme.typography.titleSmall,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                    
                    orderWithProducts.itemsWithProducts.forEach { itemWithProduct ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Product image
                            AsyncImage(
                                model = ImageUtils.getUriFromPath(itemWithProduct.product.imageUrl) 
                                    ?: "https://via.placeholder.com/50x50",
                                contentDescription = itemWithProduct.product.name,
                                modifier = Modifier
                                    .size(50.dp),
                                contentScale = ContentScale.Crop
                            )
                            
                            Spacer(modifier = Modifier.width(12.dp))
                            
                            // Product details
                            Column(
                                modifier = Modifier.weight(1f)
                            ) {
                                Text(
                                    text = itemWithProduct.product.name,
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Medium
                                )
                                Text(
                                    text = "Cantidad: ${itemWithProduct.orderItem.quantity}",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            
                            // Price
                            Text(
                                text = "$${String.format("%.2f", itemWithProduct.orderItem.price * itemWithProduct.orderItem.quantity)}",
                                style = MaterialTheme.typography.bodyMedium,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.primary
                            )
                        }
                        
                        if (itemWithProduct != orderWithProducts.itemsWithProducts.last()) {
                            Divider(modifier = Modifier.padding(vertical = 4.dp))
                        }
                    }
                }
            }
            
            Spacer(modifier = Modifier.height(12.dp))
            
            // Order details
            val vendorTotal = orderWithProducts.itemsWithProducts.sumOf { 
                it.orderItem.price * it.orderItem.quantity 
            }
            
            Text(
                text = "Total de tus productos: $${String.format("%.2f", vendorTotal)}",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            Text(
                text = "Fecha: $formattedDate",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = "Dirección: ${order.shippingAddress}",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            order.notes?.let { notes ->
                Text(
                    text = "Notas: $notes",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
            
            Spacer(modifier = Modifier.height(16.dp))
            
            // Status change buttons
            if (order.status != OrderStatus.DELIVERED && order.status != OrderStatus.CANCELLED) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    when (order.status) {
                        OrderStatus.PENDING -> {
                            Button(
                                onClick = { onStatusChange(OrderStatus.CONFIRMED) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Confirmar")
                            }
                            OutlinedButton(
                                onClick = { onStatusChange(OrderStatus.CANCELLED) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Cancelar")
                            }
                        }
                        OrderStatus.CONFIRMED -> {
                            Button(
                                onClick = { onStatusChange(OrderStatus.SHIPPED) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Enviar")
                            }
                        }
                        OrderStatus.SHIPPED -> {
                            Button(
                                onClick = { onStatusChange(OrderStatus.DELIVERED) },
                                modifier = Modifier.weight(1f)
                            ) {
                                Text("Entregado")
                            }
                        }
                        else -> {}
                    }
                }
            }
        }
    }
}

@Composable
fun StatusChip(status: OrderStatus) {
    val (backgroundColor, textColor) = when (status) {
        OrderStatus.PENDING -> MaterialTheme.colorScheme.surfaceVariant to MaterialTheme.colorScheme.onSurfaceVariant
        OrderStatus.CONFIRMED -> MaterialTheme.colorScheme.primaryContainer to MaterialTheme.colorScheme.onPrimaryContainer
        OrderStatus.SHIPPED -> MaterialTheme.colorScheme.secondaryContainer to MaterialTheme.colorScheme.onSecondaryContainer
        OrderStatus.DELIVERED -> MaterialTheme.colorScheme.tertiaryContainer to MaterialTheme.colorScheme.onTertiaryContainer
        OrderStatus.CANCELLED -> MaterialTheme.colorScheme.errorContainer to MaterialTheme.colorScheme.onErrorContainer
    }
    
    val statusText = when (status) {
        OrderStatus.PENDING -> "Pendiente"
        OrderStatus.CONFIRMED -> "Confirmado"
        OrderStatus.SHIPPED -> "Enviado"
        OrderStatus.DELIVERED -> "Entregado"
        OrderStatus.CANCELLED -> "Cancelado"
    }
    
    Card(
        colors = CardDefaults.cardColors(containerColor = backgroundColor)
    ) {
        Text(
            text = statusText,
            color = textColor,
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            style = MaterialTheme.typography.labelMedium
        )
    }
}
