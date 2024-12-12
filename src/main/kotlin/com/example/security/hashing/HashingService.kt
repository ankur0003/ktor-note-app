package com.example.security.hashing

import com.example.data.model.salt.SaltedHash

interface HashingService {
    fun generateSaltedHash(value:String,saltLength :Int=32):SaltedHash
    fun verify(value:String,salted: SaltedHash):Boolean
}