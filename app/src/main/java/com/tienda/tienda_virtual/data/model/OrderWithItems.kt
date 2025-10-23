package com.tienda.tienda_virtual.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class OrderWithItems(
    @Embedded val order: Order,
    @Relation(
        parentColumn = "id",
        entityColumn = "orderId"
    )
    val items: List<OrderItem>
)

data class OrderItemWithProduct(
    @Embedded val orderItem: OrderItem,
    @Relation(
        parentColumn = "productId",
        entityColumn = "id"
    )
    val product: Product
)

// Modelo que combina la orden con items y productos completos
data class OrderWithProducts(
    val order: Order,
    val itemsWithProducts: List<OrderItemWithProduct>
)