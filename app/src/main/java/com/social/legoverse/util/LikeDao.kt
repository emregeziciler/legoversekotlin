package com.social.legoverse.util

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface LikeDao {
    @Query("SELECT * FROM `Like`")
    fun getAll(): List<Like>

    @Query("SELECT * FROM `Like` WHERE userId = :userId AND postId = :postId")
    fun findLikeByUserAndPost(userId: Int, postId: Int): Like?

    @Insert
    fun insertLike(like: Like)

    @Delete
    fun deleteLike(like: Like)

    @Query("DELETE FROM `like` WHERE postId = :postId")
    fun deleteLikeWithPostId(postId: Int)
}