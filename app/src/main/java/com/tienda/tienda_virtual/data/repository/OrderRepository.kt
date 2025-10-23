package com.tienda.tienda_virtual.data.repository

import com.tienda.tienda_virtual.data.database.CartDao
import com.tienda.tienda_virtual.data.database.OrderDao
import com.tienda.tienda_virtual.data.database.ProductDao
import com.tienda.tienda_virtual.data.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import java.util.UUID

class OrderRepository(
    private val orderDao: OrderDao,
    private val cartDao: CartDao,
    private val productDao: ProductDao
) {
    
    fun getOrdersByUser(userId: String): Flow<List<Order>> {
        return orderDao.getOrdersByUser(userId)
    }
    
    fun getAllOrders(): Flow<List<Order>> {
        return orderDao.getAllOrders()
    }
    
    fun getOrdersByVendor(vendorId: String): Flow<List<Order>> {
        return orderDao.getOrdersByVendor(vendorId)
    }
    
    fun getOrdersByVendorWithProducts(vendorId: String): Flow<List<OrderWithProducts>> {
        return orderDao.getOrdersByVendor(vendorId).map { orders ->
            orders.map { order ->
                val items = orderDao.getOrderItemsByOrderId(order.id)
                val itemsWithProducts = items.mapNotNull { item ->
                    val product = orderDao.getProductById(item.productId)
                    if (product != null && product.vendorId == vendorId) {
                        OrderItemWithProduct(item, product)
                    } else null
                }
                OrderWithProducts(order, itemsWithProducts)
            }.filter { it.itemsWithProducts.isNotEmpty() }
        }
    }
    
    suspend fun getOrderById(orderId: String): Order? {
        return orderDao.getOrderById(orderId)
    }
    
    suspend fun getOrderWithItems(orderId: String): OrderWithItems? {
        return orderDao.getOrderWithItems(orderId)
    }
    
    suspend fun createOrderFromCart(
        userId: String,
        cartItems: List<com.tienda.tienda_virtual.data.model.CartItemWithProduct>,
        shippingAddress: String,
        notes: String? = null
    ): Order {
        val orderId = UUID.randomUUID().toString()
        val totalAmount = cartItems.sumOf { it.cartItem.quantity * it.product.price }
        
        val order = Order(
            id = orderId,
            userId = userId,
            totalAmount = totalAmount,
            status = OrderStatus.PENDING,
            shippingAddress = shippingAddress,
            notes = notes
        )
        
        orderDao.insertOrder(order)
        
        // Create order items
        cartItems.forEach { cartItemWithProduct ->
            val orderItem = OrderItem(
                id = UUID.randomUUID().toString(),
                orderId = orderId,
                productId = cartItemWithProduct.cartItem.productId,
                quantity = cartItemWithProduct.cartItem.quantity,
                price = cartItemWithProduct.product.price
            )
            orderDao.insertOrderItem(orderItem)
        }
        
        // Clear cart after creating order
        cartDao.clearCart(userId)
        
        return order
    }
    
    suspend fun updateOrderStatus(orderId: String, status: OrderStatus) {
        val order = orderDao.getOrderById(orderId)
        if (order != null) {
            // Si el estado cambia a SHIPPED, reducir el stock de los productos
            if (status == OrderStatus.SHIPPED && order.status != OrderStatus.SHIPPED) {
                val orderItems = orderDao.getOrderItemsByOrderId(orderId)
                
                // Reducir el stock de cada producto en la orden
                orderItems.forEach { orderItem ->
                    val product = productDao.getProductById(orderItem.productId)
                    if (product != null) {
                        val newStock = (product.stock - orderItem.quantity).coerceAtLeast(0)
                        productDao.updateProductStock(orderItem.productId, newStock)
                    }
                }
            }
            
            val updatedOrder = order.copy(status = status)
            orderDao.updateOrder(updatedOrder)
        }
    }
    
    suspend fun deleteOrder(order: Order) {
        orderDao.deleteOrder(order)
    }
}
