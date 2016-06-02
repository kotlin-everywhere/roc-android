package com.github.kotlin_everywhere.rpc.test

import android.test.AndroidTestCase
import com.github.kotlin_everywhere.rpc.init
import com.github.kotlin_everywhere.rpc.testServer.PostServer
import org.junit.Assert.assertArrayEquals
import java.util.concurrent.CountDownLatch

private val postServer = PostServer().init("http://192.168.56.1:9999")

class RemoteTest : AndroidTestCase() {
    fun testPostServer() {
        postServer.truncate()

        // ensure empty posts.
        assertArrayEquals(arrayOf<PostServer.Post>(), postServer.list());
        // add a post.
        assertEquals(1, postServer.add(PostServer.Post(0, "title", "content")));
        // check server has posts.
        assertArrayEquals(arrayOf(PostServer.Post(1, "title", "content")), postServer.list());
        // edit post
        assertEquals(Unit, postServer.edit(PostServer.Post(1, "제목", "내용")))
        assertArrayEquals(arrayOf(PostServer.Post(1, "제목", "내용")), postServer.list())
        // delete post
        assertEquals(Unit, postServer.delete(1))
        assertArrayEquals(arrayOf(), postServer.list())
    }

    fun testAsync() {
        assertEquals(3, sync<Int> { postServer.sum(arrayOf(1, 2), it) })
    }
}

fun <T> sync(block: ((T) -> Unit) -> Unit): T {
    val latch = CountDownLatch(1)
    var value: T? = null
    block { value = it; latch.countDown() }
    latch.await()
    return value!!
}

