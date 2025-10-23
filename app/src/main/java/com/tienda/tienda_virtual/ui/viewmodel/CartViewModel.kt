package com.tienda.tienda_virtual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tienda.tienda_virtual.data.model.CartItemWithProduct
import com.tienda.tienda_virtual.data.repository.CartRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch

class CartViewModel(private val cartRepository: CartRepository) : ViewModel() {
    
    fun getCartItemsWithProduct(userId: String): Flow<List<CartItemWithProduct>> {
        return cartRepository.getCartItemsWithProduct(userId)
    }
    
    fun addToCart(userId: String, productId: String, quantity: Int = 1) {
        viewModelScope.launch {
            cartRepository.addToCart(userId, productId, quantity)
        }
    }
    
    fun updateCartItemQuantity(userId: String, productId: String, quantity: Int) {
        viewModelScope.launch {
            cartRepository.updateCartItemQuantity(userId, productId, quantity)
        }
    }
    
    fun removeFromCart(userId: String, productId: String) {
        viewModelScope.launch {
            cartRepository.removeFromCart(userId, productId)
        }
    }
    
    fun clearCart(userId: String) {
        viewModelScope.launch {
            cartRepository.clearCart(userId)
        }
    }
    
    suspend fun getCartItemCount(userId: String): Int {
        return cartRepository.getCartItemCount(userId)
    }
}
