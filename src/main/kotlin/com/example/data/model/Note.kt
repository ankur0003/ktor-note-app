package com.example.data.model

import kotlinx.serialization.Serializable
import org.bson.codecs.pojo.annotations.BsonId
import org.bson.types.ObjectId

@Serializable
data class Note(
    @BsonId val id: String = ObjectId().toString(),
    val title: String,
    val body: String,
    val timestamp: Long = System.currentTimeMillis()
)