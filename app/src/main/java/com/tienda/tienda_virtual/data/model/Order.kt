package com.tienda.tienda_virtual.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey
    val id: String,
    val userId: String,
    val totalAmount: Double,
    val status: OrderStatus,
    val createdAt: Long = System.currentTimeMillis(),
    val shippingAddress: String,
    val notes: String? = null
)

enum class OrderStatus {
    PENDING, CONFIRMED, SHIPPED, DELIVERED, CANCELLED
}
