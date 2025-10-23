package com.tienda.tienda_virtual.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.tienda.tienda_virtual.data.model.Category
import com.tienda.tienda_virtual.data.model.Product
import com.tienda.tienda_virtual.data.model.ProductWithCategory
import com.tienda.tienda_virtual.data.repository.ProductRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.Flow

class ProductViewModel(private val productRepository: ProductRepository) : ViewModel() {
    
    private val _allProductsWithCategory = MutableStateFlow<List<ProductWithCategory>>(emptyList())
    val allProductsWithCategory: StateFlow<List<ProductWithCategory>> = _allProductsWithCategory.asStateFlow()
    
    private val _allCategories = MutableStateFlow<List<Category>>(emptyList())
    val allCategories: StateFlow<List<Category>> = _allCategories.asStateFlow()
    
    private val _selectedProduct = MutableStateFlow<Product?>(null)
    val selectedProduct: StateFlow<Product?> = _selectedProduct.asStateFlow()
    
    init {
        loadProducts()
        loadCategories()
    }
    
    private fun loadProducts() {
        viewModelScope.launch {
            productRepository.getAllProductsWithCategory().collect { products ->
                _allProductsWithCategory.value = products
            }
        }
    }
    
    private fun loadCategories() {
        viewModelScope.launch {
            productRepository.getAllCategories().collect { categories ->
                _allCategories.value = categories
            }
        }
    }
    
    fun getProductById(productId: String) {
        viewModelScope.launch {
            val product = productRepository.getProductById(productId)
            _selectedProduct.value = product
        }
    }
    
    fun getProductsByCategory(categoryId: String) {
        viewModelScope.launch {
            productRepository.getProductsByCategory(categoryId).collect { products ->
                _allProductsWithCategory.value = products
            }
        }
    }
    
    fun getProductsByVendor(vendorId: String): Flow<List<ProductWithCategory>> {
        return productRepository.getProductsByVendor(vendorId)
    }

    fun addProduct(
        name: String,
        description: String,
        price: Double,
        categoryId: String,
        imageUrl: String?,
        stock: Int,
        vendorId: String
    ) {
        viewModelScope.launch {
            productRepository.addProduct(
                name = name,
                description = description,
                price = price,
                categoryId = categoryId,
                imageUrl = imageUrl,
                stock = stock,
                vendorId = vendorId
            )
            // Refresh lists after adding
            loadProducts()
        }
    }
}
