package com.example.data.db.user

import com.example.data.model.User
import org.litote.kmongo.coroutine.CoroutineDatabase
import org.litote.kmongo.eq

class UserDataSourceImpl(db:CoroutineDatabase) : UserDataSource {
    val database = db.getCollection<User>()
    override suspend fun insertUser(user: User): Boolean {
       return database.insertOne(user).wasAcknowledged()
    }

    override suspend fun getUserByUsername(username: String): User? {
        return database.findOne(User::username eq username)
    }
}