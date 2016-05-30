package com.github.kotlin_everywhere.rpc.test

import android.os.Handler
import android.test.AndroidTestCase
import com.github.kotlin_everywhere.rpc.init
import com.github.kotlin_everywhere.rpc.testServer.PostServer
import org.junit.Assert.assertArrayEquals
import java.util.concurrent.CountDownLatch

private val postServer = PostServer().init("http://192.168.56.1:9999")

class RemoteTest : AndroidTestCase() {
    fun testPostServer() {
        sync<Unit> { postServer.truncate(Unit, it) }
        val postServerList = {
            sync<Array<PostServer.Post>> { postServer.list(Unit, it) }
        }

        // ensure empty posts.
        assertArrayEquals(arrayOf<PostServer.Post>(), postServerList());
        // add a post.
        assertEquals(1, sync { postServer.add(PostServer.Post(0, "title", "content"), it) });
        // check server has posts.
        assertArrayEquals(arrayOf(PostServer.Post(1, "title", "content")), postServerList());
        // edit post
        assertEquals(Unit, sync { postServer.edit(PostServer.Post(1, "제목", "내용"), it) })
        assertArrayEquals(arrayOf(PostServer.Post(1, "제목", "내용")), postServerList())
        // delete post
        assertEquals(Unit, sync { postServer.delete(1, it) })
        assertArrayEquals(arrayOf(), postServerList())
    }
}

private fun <T> AndroidTestCase.sync(body: ((T) -> Unit) -> Unit): T {
    val latch = CountDownLatch(1)
    var result: T? = null;
    Handler(context.mainLooper).post {
        body {
            result = it
            latch.countDown()
        }
    }
    latch.await()
    return result!!
}
