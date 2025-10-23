package com.tienda.tienda_virtual.data.repository

import com.tienda.tienda_virtual.data.database.CartDao
import com.tienda.tienda_virtual.data.model.CartItem
import com.tienda.tienda_virtual.data.model.CartItemWithProduct
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class CartRepository(private val cartDao: CartDao) {
    
    fun getCartItemsWithProduct(userId: String): Flow<List<CartItemWithProduct>> {
        return cartDao.getCartItemsWithProduct(userId)
    }
    
    suspend fun addToCart(userId: String, productId: String, quantity: Int = 1) {
        val existingItem = cartDao.getCartItem(userId, productId)
        if (existingItem != null) {
            val updatedItem = existingItem.copy(quantity = existingItem.quantity + quantity)
            cartDao.updateCartItem(updatedItem)
        } else {
            val cartItem = CartItem(
                id = UUID.randomUUID().toString(),
                userId = userId,
                productId = productId,
                quantity = quantity
            )
            cartDao.insertCartItem(cartItem)
        }
    }
    
    suspend fun updateCartItemQuantity(userId: String, productId: String, quantity: Int) {
        val existingItem = cartDao.getCartItem(userId, productId)
        if (existingItem != null) {
            if (quantity <= 0) {
                cartDao.removeFromCart(userId, productId)
            } else {
                val updatedItem = existingItem.copy(quantity = quantity)
                cartDao.updateCartItem(updatedItem)
            }
        }
    }
    
    suspend fun removeFromCart(userId: String, productId: String) {
        cartDao.removeFromCart(userId, productId)
    }
    
    suspend fun clearCart(userId: String) {
        cartDao.clearCart(userId)
    }
    
    suspend fun getCartItemCount(userId: String): Int {
        return cartDao.getCartItemCount(userId)
    }
}
