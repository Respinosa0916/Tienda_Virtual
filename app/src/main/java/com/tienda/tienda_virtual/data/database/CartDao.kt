package com.tienda.tienda_virtual.data.database

import androidx.room.*
import com.tienda.tienda_virtual.data.model.CartItem
import com.tienda.tienda_virtual.data.model.CartItemWithProduct
import kotlinx.coroutines.flow.Flow

@Dao
interface CartDao {
    @Transaction
    @Query("SELECT * FROM cart_items WHERE userId = :userId ORDER BY addedAt DESC")
    fun getCartItemsWithProduct(userId: String): Flow<List<CartItemWithProduct>>

    @Query("SELECT * FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun getCartItem(userId: String, productId: String): CartItem?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCartItem(cartItem: CartItem)

    @Update
    suspend fun updateCartItem(cartItem: CartItem)

    @Delete
    suspend fun deleteCartItem(cartItem: CartItem)

    @Query("DELETE FROM cart_items WHERE userId = :userId")
    suspend fun clearCart(userId: String)

    @Query("DELETE FROM cart_items WHERE userId = :userId AND productId = :productId")
    suspend fun removeFromCart(userId: String, productId: String)

    @Query("SELECT COUNT(*) FROM cart_items WHERE userId = :userId")
    suspend fun getCartItemCount(userId: String): Int
}
