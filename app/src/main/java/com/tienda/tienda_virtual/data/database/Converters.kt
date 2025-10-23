package com.tienda.tienda_virtual.data.database

import androidx.room.TypeConverter
import com.tienda.tienda_virtual.data.model.UserType

class Converters {
    @TypeConverter
    fun fromUserType(userType: UserType): String = userType.name

    @TypeConverter
    fun toUserType(value: String): UserType = UserType.valueOf(value)
}


