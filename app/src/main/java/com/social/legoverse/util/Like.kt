package com.social.legoverse.util

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class Like(
    @PrimaryKey(autoGenerate = true) val likeId: Int = 0,
    @ColumnInfo(name = "userId") val userId: Int,
    @ColumnInfo(name = "postId") val postId: Int
)
