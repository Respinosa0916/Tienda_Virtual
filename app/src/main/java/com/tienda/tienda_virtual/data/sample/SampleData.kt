package com.tienda.tienda_virtual.data.sample

import com.tienda.tienda_virtual.data.model.Category
import com.tienda.tienda_virtual.data.model.Product
import com.tienda.tienda_virtual.data.model.User
import com.tienda.tienda_virtual.data.model.UserType

object SampleData {
    
    val sampleCategories = listOf(
        Category(
            id = "cat1",
            name = "Electrónicos",
            description = "Dispositivos electrónicos y tecnología",
            imageUrl = "https://images.unsplash.com/photo-1498049794561-7780c723c765?w=400"
        ),
        Category(
            id = "cat2",
            name = "Ropa",
            description = "Ropa para hombres, mujeres y niños",
            imageUrl = "https://images.unsplash.com/photo-1441986300917-64674bd600d8?w=400"
        ),
        Category(
            id = "cat3",
            name = "Hogar",
            description = "Artículos para el hogar y decoración",
            imageUrl = "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400"
        ),
        Category(
            id = "cat4",
            name = "Deportes",
            description = "Artículos deportivos y fitness",
            imageUrl = "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400"
        )
    )
    
    val sampleUsers = listOf(
        User(
            id = "user1",
            name = "Juan Pérez",
            email = "cliente@test.com",
            password = "123456",
            userType = UserType.CLIENTE,
            phone = "+1234567890",
            address = "Calle Principal 123"
        ),
        User(
            id = "user2",
            name = "María García",
            email = "vendedor@test.com",
            password = "123456",
            userType = UserType.VENDEDOR,
            phone = "+1234567891",
            address = "Avenida Central 456"
        )
    )
    
    val sampleProducts = listOf(
        Product(
            id = "prod1",
            name = "Smartphone Samsung Galaxy",
            description = "Smartphone de última generación con pantalla AMOLED de 6.1 pulgadas, cámara triple de 64MP y batería de 4000mAh.",
            price = 599.99,
            categoryId = "cat1",
            imageUrl = "https://images.unsplash.com/photo-1511707171637-5d897cc32aa9?w=400",
            stock = 15,
            vendorId = "user2"
        ),
        Product(
            id = "prod2",
            name = "Laptop HP Pavilion",
            description = "Laptop con procesador Intel i5, 8GB RAM, 256GB SSD y pantalla de 15.6 pulgadas.",
            price = 799.99,
            categoryId = "cat1",
            imageUrl = "https://images.unsplash.com/photo-1496181133206-80ce9b88a853?w=400",
            stock = 8,
            vendorId = "user2"
        ),
        Product(
            id = "prod3",
            name = "Camiseta Básica",
            description = "Camiseta de algodón 100% en varios colores. Corte clásico y cómodo.",
            price = 19.99,
            categoryId = "cat2",
            imageUrl = "https://images.unsplash.com/photo-1521572163474-6864f9cf17ab?w=400",
            stock = 50,
            vendorId = "user2"
        ),
        Product(
            id = "prod4",
            name = "Jeans Clásicos",
            description = "Jeans de mezclilla con corte clásico. Disponible en varios tallas.",
            price = 49.99,
            categoryId = "cat2",
            imageUrl = "https://images.unsplash.com/photo-1542272604-787c3835535d?w=400",
            stock = 25,
            vendorId = "user2"
        ),
        Product(
            id = "prod5",
            name = "Sofá 3 Plazas",
            description = "Sofá moderno de 3 plazas en tela gris. Perfecto para sala de estar.",
            price = 299.99,
            categoryId = "cat3",
            imageUrl = "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400",
            stock = 5,
            vendorId = "user2"
        ),
        Product(
            id = "prod6",
            name = "Mesa de Centro",
            description = "Mesa de centro de madera maciza con diseño moderno.",
            price = 149.99,
            categoryId = "cat3",
            imageUrl = "https://images.unsplash.com/photo-1586023492125-27b2c045efd7?w=400",
            stock = 10,
            vendorId = "user2"
        ),
        Product(
            id = "prod7",
            name = "Pelota de Fútbol",
            description = "Pelota de fútbol oficial de cuero sintético. Tamaño 5.",
            price = 29.99,
            categoryId = "cat4",
            imageUrl = "https://images.unsplash.com/photo-1571019613454-1cb2f99b2d8b?w=400",
            stock = 30,
            vendorId = "user2"
        ),
        Product(
            id = "prod8",
            name = "Zapatillas Deportivas",
            description = "Zapatillas deportivas para running con tecnología de amortiguación.",
            price = 89.99,
            categoryId = "cat4",
            imageUrl = "https://images.unsplash.com/photo-1542291026-7eec264c27ff?w=400",
            stock = 20,
            vendorId = "user2"
        )
    )
}
