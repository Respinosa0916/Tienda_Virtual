package com.tienda.tienda_virtual.data.database

import androidx.room.*
import com.tienda.tienda_virtual.data.model.Product
import com.tienda.tienda_virtual.data.model.ProductWithCategory
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Transaction
    @Query("SELECT * FROM products WHERE isActive = 1 ORDER BY name ASC")
    fun getAllProductsWithCategory(): Flow<List<ProductWithCategory>>

    @Transaction
    @Query("SELECT * FROM products WHERE categoryId = :categoryId AND isActive = 1 ORDER BY name ASC")
    fun getProductsByCategory(categoryId: String): Flow<List<ProductWithCategory>>

    @Transaction
    @Query("SELECT * FROM products WHERE vendorId = :vendorId ORDER BY name ASC")
    fun getProductsByVendor(vendorId: String): Flow<List<ProductWithCategory>>

    @Query("SELECT * FROM products WHERE id = :productId")
    suspend fun getProductById(productId: String): Product?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET stock = :newStock WHERE id = :productId")
    suspend fun updateProductStock(productId: String, newStock: Int)
}
