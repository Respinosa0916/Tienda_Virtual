package com.tienda.tienda_virtual.data.database

import androidx.room.Database
import androidx.room.TypeConverters
import androidx.room.Room
import androidx.room.RoomDatabase
import android.content.Context
import com.tienda.tienda_virtual.data.model.*

@Database(
    entities = [
        User::class,
        Category::class,
        Product::class,
        CartItem::class,
        Order::class,
        OrderItem::class
    ],
    version = 2,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {
    abstract fun userDao(): UserDao
    abstract fun categoryDao(): CategoryDao
    abstract fun productDao(): ProductDao
    abstract fun cartDao(): CartDao
    abstract fun orderDao(): OrderDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "tienda_virtual_database"
                )
                    .fallbackToDestructiveMigration()
                    .allowMainThreadQueries() // Temporary for debugging - remove in production
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
