package com.github.kotlin_everywhere.rpc.testServer


private val postServer = PostServer().apply {
    var posts = arrayOf<PostServer.Post>()
    var pkSeq = 0

    list {
        posts
    }

    add {
        it.copy(pk = ++pkSeq).apply {
            posts += this
        }.pk
    }

    edit { post ->
        posts = posts
                .map {
                    if (it.pk == post.pk) post
                    else it
                }.toTypedArray()
    }

    delete { pk ->
        posts = posts
                .filter { it.pk != pk }
                .toTypedArray()
    }

    truncate {
        posts = arrayOf()
        pkSeq = 0
    }

    sum {
        if (it.isNotEmpty()) {
            it.reduce { sum, i -> sum + i }
        } else {
            0
        }
    }
}

fun main(args: Array<String>) {
    postServer.runServer(port = 9999)
}

