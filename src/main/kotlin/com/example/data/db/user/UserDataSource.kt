package com.example.data.db.user

import com.example.data.model.User

interface UserDataSource {
    suspend fun insertUser(user: User):Boolean
    suspend fun getUserByUsername(username:String):User?
}