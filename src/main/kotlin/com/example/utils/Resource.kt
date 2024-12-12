package com.example.utils

sealed class Resource<T>(val data:T?=null,val msg:String){
    class Success<T>( data:T, msg:String):Resource<T>(data,msg)
    class Error<T>(msg: String):Resource<T>(msg=msg)
}