package com.tienda.tienda_virtual.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class User(
    @PrimaryKey
    val id: String,
    val name: String,
    val email: String,
    val password: String,
    val userType: UserType,
    val phone: String? = null,
    val address: String? = null
)

enum class UserType {
    CLIENTE, VENDEDOR
}
