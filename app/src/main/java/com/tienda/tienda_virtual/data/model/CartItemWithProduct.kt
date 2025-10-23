package com.tienda.tienda_virtual.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class CartItemWithProduct(
    @Embedded val cartItem: CartItem,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product
)