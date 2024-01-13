package com.social.legoverse.util

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update

@Dao
interface UserDao {
    @Query("SELECT * FROM users")
    fun getAll(): List<Users>

    @Query("SELECT * FROM users WHERE user_name = :userNameOrMail OR mail = :userNameOrMail AND password = :password LIMIT 1")
    fun getSpecificUser(userNameOrMail: String?,  password: String?): Users?

    @Insert
    fun insert(folder: Users)

    @Delete
    fun delete(folder: Users)

    @Update
    fun update(user: Users)

    @Query("SELECT * FROM users WHERE user_name = :userNameOrMail OR mail = :userNameOrMail LIMIT 1")
    fun findUserByUsernameOrEmail(userNameOrMail: String): Users?

    @Query("SELECT * FROM users WHERE id = :id  LIMIT 1")
    fun findUserWithId(id: Int): Users?

    @Query("UPDATE Users SET keep_logged = :keepLogged")
    fun updateAllUsersKeepLogged(keepLogged: Boolean)

    @Query("SELECT * FROM Users WHERE keep_logged = 1 LIMIT 1")
    fun getLoggedInUser(): Users?
}