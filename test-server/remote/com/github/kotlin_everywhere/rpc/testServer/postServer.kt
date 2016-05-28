package com.github.kotlin_everywhere.rpc.testServer

import com.github.kotlin_everywhere.rpc.*

class PostServer : Remote() {
    val list = get<Unit, Array<Post>>("/")
    val add = post<Post, Int>()
    val edit = post<Post, Unit>()
    val delete = post<Int, Unit>()

    val truncate = post<Unit, Unit>()

    data class Post(val pk: Int, val title: String, val content: String)
}


