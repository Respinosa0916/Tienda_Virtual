package com.tienda.tienda_virtual.data.sample

import com.tienda.tienda_virtual.data.database.AppDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class DatabaseInitializer(private val database: AppDatabase) {
    
    fun initializeSampleData() {
        CoroutineScope(Dispatchers.IO).launch {
            // Insert categories
            SampleData.sampleCategories.forEach { category ->
                database.categoryDao().insertCategory(category)
            }
            
            // Insert users
            SampleData.sampleUsers.forEach { user ->
                database.userDao().insertUser(user)
            }
            
            // Insert products
            SampleData.sampleProducts.forEach { product ->
                database.productDao().insertProduct(product)
            }
        }
    }
}
