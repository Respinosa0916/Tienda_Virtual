package com.tienda.tienda_virtual.data.repository

import com.tienda.tienda_virtual.data.database.CategoryDao
import com.tienda.tienda_virtual.data.database.ProductDao
import com.tienda.tienda_virtual.data.model.Category
import com.tienda.tienda_virtual.data.model.Product
import com.tienda.tienda_virtual.data.model.ProductWithCategory
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class ProductRepository(
    private val productDao: ProductDao,
    private val categoryDao: CategoryDao
) {
    
    fun getAllProductsWithCategory(): Flow<List<ProductWithCategory>> {
        return productDao.getAllProductsWithCategory()
    }
    
    fun getProductsByCategory(categoryId: String): Flow<List<ProductWithCategory>> {
        return productDao.getProductsByCategory(categoryId)
    }
    
    fun getProductsByVendor(vendorId: String): Flow<List<ProductWithCategory>> {
        return productDao.getProductsByVendor(vendorId)
    }
    
    suspend fun getProductById(productId: String): Product? {
        return productDao.getProductById(productId)
    }
    
    suspend fun addProduct(
        name: String,
        description: String,
        price: Double,
        categoryId: String,
        imageUrl: String? = null,
        stock: Int = 0,
        vendorId: String
    ): Product {
        val product = Product(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            price = price,
            categoryId = categoryId,
            imageUrl = imageUrl,
            stock = stock,
            vendorId = vendorId
        )
        productDao.insertProduct(product)
        return product
    }
    
    suspend fun updateProduct(product: Product) {
        productDao.updateProduct(product)
    }
    
    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
    }
    
    suspend fun updateProductStock(productId: String, newStock: Int) {
        productDao.updateProductStock(productId, newStock)
    }
    
    // Category operations
    fun getAllCategories(): Flow<List<Category>> {
        return categoryDao.getAllCategories()
    }
    
    suspend fun getCategoryById(categoryId: String): Category? {
        return categoryDao.getCategoryById(categoryId)
    }
    
    suspend fun addCategory(
        name: String,
        description: String? = null,
        imageUrl: String? = null
    ): Category {
        val category = Category(
            id = UUID.randomUUID().toString(),
            name = name,
            description = description,
            imageUrl = imageUrl
        )
        categoryDao.insertCategory(category)
        return category
    }
    
    suspend fun updateCategory(category: Category) {
        categoryDao.updateCategory(category)
    }
    
    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }
}
