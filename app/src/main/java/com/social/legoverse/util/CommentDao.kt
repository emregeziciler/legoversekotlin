package com.social.legoverse.util

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface CommentDao {

    @Query("SELECT * FROM comment WHERE postId = :postId AND userId = :userId")
    fun getAll(postId: Int,userId: Int): List<Comment>

    @Insert
    fun insert(comment: Comment)

    @Query("DELETE FROM comment WHERE postId = :postId")
    fun deleteCommentWithPostId(postId: Int)
}
