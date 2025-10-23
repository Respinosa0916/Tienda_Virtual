package com.tienda.tienda_virtual.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "cart_items")
data class CartItem(
    @PrimaryKey
    val id: String,
    val userId: String,
    val productId: String,
    val quantity: Int,
    val addedAt: Long = System.currentTimeMillis()
)
