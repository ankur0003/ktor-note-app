package com.example.routes

import io.ktor.resources.*

@Resource("/example")
class Example()
@Resource("/nested/example")
class NestedExample(){
    @Resource("new")
    class New(val parent:NestedExample = NestedExample())
}

@Resource("/login")
class UserLogin