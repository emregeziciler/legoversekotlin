package com.social.legoverse.util

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import androidx.room.TypeConverter
import java.util.Date

@Entity(indices = [
    Index(value = ["user_name"], unique = true),
    Index(value = ["mail"], unique = true)
])
data class Users (
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    @ColumnInfo(name = "user_name") val userName: String? = null,
    @ColumnInfo(name = "mail") val mail: String?,
    @ColumnInfo(name = "password") val password: String?,
    @ColumnInfo(name = "real_name") val name: String?= null,
    @ColumnInfo(name = "phone_number") val phoneNumber: String?= null,
    @ColumnInfo(name = "birthday") val birthday: Date?= null,
    @ColumnInfo(name = "profile_image") val profileImage: ByteArray? = null,
    @ColumnInfo(name = "keep_logged") val keepLogged: Boolean? = null

)

