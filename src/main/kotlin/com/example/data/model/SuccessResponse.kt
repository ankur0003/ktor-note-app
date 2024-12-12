package com.example.data.model

import kotlinx.serialization.Serializable
import javax.annotation.processing.Messager

@Serializable
data class SuccessResponse(val success :Boolean=true,val message:String)
