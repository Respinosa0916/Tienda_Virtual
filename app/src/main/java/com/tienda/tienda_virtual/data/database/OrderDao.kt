package com.tienda.tienda_virtual.data.database

import androidx.room.*
import com.tienda.tienda_virtual.data.model.Order
import com.tienda.tienda_virtual.data.model.OrderItem
import com.tienda.tienda_virtual.data.model.OrderWithItems
import com.tienda.tienda_virtual.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders WHERE userId = :userId ORDER BY createdAt DESC")
    fun getOrdersByUser(userId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("""
        SELECT DISTINCT o.* FROM orders o
        INNER JOIN order_items oi ON o.id = oi.orderId
        INNER JOIN products p ON oi.productId = p.id
        WHERE p.vendorId = :vendorId
        ORDER BY o.createdAt DESC
    """)
    fun getOrdersByVendor(vendorId: String): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderById(orderId: String): Order?

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItemsByOrderId(orderId: String): List<OrderItem>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order)

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrderItem(orderItem: OrderItem)

    @Query("SELECT * FROM order_items WHERE orderId = :orderId")
    suspend fun getOrderItems(orderId: String): List<OrderItem>

    @Transaction
    @Query("SELECT * FROM orders WHERE id = :orderId")
    suspend fun getOrderWithItems(orderId: String): OrderWithItems?
}
