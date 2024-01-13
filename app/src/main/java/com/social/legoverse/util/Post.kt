package com.social.legoverse.util

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(foreignKeys = [ForeignKey(entity = Users::class,
    parentColumns = arrayOf("id"),
    childColumns = arrayOf("userId"),
    onDelete = ForeignKey.CASCADE)],
    indices = [Index(value = ["userId"])])
data class Post (
    @PrimaryKey(autoGenerate = true) val postId: Int = 0,
    @ColumnInfo(name = "userId") val userId: Int, // Users tablosundaki id ile ilişkilendirildi
    @ColumnInfo(name = "image") val image: ByteArray?, // Gönderinin resmi
    @ColumnInfo(name = "content") val content: String?, // Gönderi metni
    @ColumnInfo(name = "likes") val likes: Int = 0, // Beğeni sayısı
    @ColumnInfo(name = "project_name") val projectName: String? = null // Proje ismi

)
