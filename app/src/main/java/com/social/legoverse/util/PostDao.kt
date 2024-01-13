package com.social.legoverse.util

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface PostDao {

    @Query("SELECT * FROM post ORDER BY postId DESC")
    fun getAll(): List<Post>

    @Query("SELECT * FROM post WHERE userId = :userId")
    fun getPostsByUser(userId: Int): List<Post>


    @Query("SELECT * FROM post WHERE postId = :postId")
    fun getPostsById(postId: Int): Post

    @Query("SELECT * FROM post WHERE userId = :userId ORDER BY postId DESC")
    fun getPostsListById(userId: Int): List<Post>

    @Query("UPDATE post SET likes = :likeCount WHERE postId= :postId")
    fun updatePostLikes(postId: Int,likeCount: Int)

    @Insert
    fun insertPost(post: Post)

    @Update
    fun updatePost(post: Post)

    @Delete
    fun deletePost(post: Post)
}
