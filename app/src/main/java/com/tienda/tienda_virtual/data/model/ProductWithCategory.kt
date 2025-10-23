package com.tienda.tienda_virtual.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ProductWithCategory(
    @Embedded val product: Product,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "id"
    )
    val category: Category
)