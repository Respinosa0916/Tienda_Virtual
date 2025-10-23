package com.tienda.tienda_virtual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tienda.tienda_virtual.data.model.CartItemWithProduct
import com.tienda.tienda_virtual.data.model.Order
import com.tienda.tienda_virtual.data.model.OrderStatus
import com.tienda.tienda_virtual.data.model.OrderWithProducts
import com.tienda.tienda_virtual.data.repository.OrderRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class OrderViewModel(private val orderRepository: OrderRepository) : ViewModel() {
    
    private val _allOrders = MutableStateFlow<List<Order>>(emptyList())
    val allOrders: StateFlow<List<Order>> = _allOrders.asStateFlow()
    
    private val _ordersWithProducts = MutableStateFlow<List<OrderWithProducts>>(emptyList())
    val ordersWithProducts: StateFlow<List<OrderWithProducts>> = _ordersWithProducts.asStateFlow()
    
    init {
        loadOrders()
    }
    
    private fun loadOrders() {
        viewModelScope.launch {
            orderRepository.getAllOrders().collect { orders ->
                _allOrders.value = orders
            }
        }
    }
    
    fun getOrdersByUser(userId: String) {
        viewModelScope.launch {
            orderRepository.getOrdersByUser(userId).collect { orders ->
                _allOrders.value = orders
            }
        }
    }
    
    fun getOrdersByVendor(vendorId: String) {
        viewModelScope.launch {
            orderRepository.getOrdersByVendor(vendorId).collect { orders ->
                _allOrders.value = orders
            }
        }
    }
    
    fun getOrdersByVendorWithProducts(vendorId: String) {
        viewModelScope.launch {
            orderRepository.getOrdersByVendorWithProducts(vendorId).collect { orders ->
                _ordersWithProducts.value = orders
            }
        }
    }
    
    fun updateOrderStatus(orderId: String, status: OrderStatus) {
        viewModelScope.launch {
            orderRepository.updateOrderStatus(orderId, status)
        }
    }
    
    fun getOrderById(orderId: String) {
        viewModelScope.launch {
            val order = orderRepository.getOrderById(orderId)
            // Handle order details if needed
        }
    }
    
    fun createOrder(
        userId: String,
        cartItems: List<CartItemWithProduct>,
        shippingAddress: String,
        notes: String?
    ) {
        viewModelScope.launch {
            orderRepository.createOrderFromCart(
                userId = userId,
                cartItems = cartItems,
                shippingAddress = shippingAddress,
                notes = notes
            )
        }
    }
}
