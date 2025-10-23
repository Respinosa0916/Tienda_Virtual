package com.tienda.tienda_virtual.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey
    val id: String,
    val name: String,
    val description: String,
    val price: Double,
    val categoryId: String,
    val imageUrl: String? = null,
    val stock: Int = 0,
    val isActive: Boolean = true,
    val vendorId: String
)
