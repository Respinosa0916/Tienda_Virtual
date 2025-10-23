package com.tienda.tienda_virtual.data.repository

import com.tienda.tienda_virtual.data.database.UserDao
import com.tienda.tienda_virtual.data.model.User
import com.tienda.tienda_virtual.data.model.UserType
import kotlinx.coroutines.flow.Flow
import java.util.UUID

class AuthRepository(private val userDao: UserDao) {
    
    suspend fun login(email: String, password: String): User? {
        return userDao.getUserByCredentials(email, password)
    }
    
    suspend fun register(
        name: String,
        email: String,
        password: String,
        userType: UserType,
        phone: String? = null,
        address: String? = null
    ): User {
        val user = User(
            id = UUID.randomUUID().toString(),
            name = name,
            email = email,
            password = password,
            userType = userType,
            phone = phone,
            address = address
        )
        userDao.insertUser(user)
        return user
    }
    
    suspend fun getUserById(userId: String): User? {
        return userDao.getUserById(userId)
    }
    
    suspend fun getUserByEmail(email: String): User? {
        return userDao.getUserByEmail(email)
    }
    
    suspend fun updateUser(user: User) {
        userDao.updateUser(user)
    }
    
    fun getUsersByType(userType: String): Flow<List<User>> {
        return userDao.getUsersByType(userType)
    }
}
