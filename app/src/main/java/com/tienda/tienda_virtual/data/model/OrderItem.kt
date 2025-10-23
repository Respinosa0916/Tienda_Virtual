package com.tienda.tienda_virtual.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "order_items")
data class OrderItem(
    @PrimaryKey
    val id: String,
    val orderId: String,
    val productId: String,
    val quantity: Int,
    val price: Double
)
