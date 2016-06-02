package com.github.kotlin_everywhere.rpc.testServer

import com.github.kotlin_everywhere.rpc.Remote
import com.github.kotlin_everywhere.rpc.post

class PostServer : Remote() {
    val list = get<Array<Post>>("/")
    val add = post<Post, Int>()
    val edit = post<Post, Unit>()
    val delete = post<Int, Unit>()

    val truncate = post<Unit>()

    data class Post(val pk: Int, val title: String, val content: String)
}


