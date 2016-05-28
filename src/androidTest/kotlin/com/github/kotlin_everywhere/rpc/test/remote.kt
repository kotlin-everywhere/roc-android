package com.github.kotlin_everywhere.rpc.test

import android.os.Handler
import android.test.AndroidTestCase
import com.github.kotlin_everywhere.rpc.init
import com.github.kotlin_everywhere.rpc.testServer.PostServer
import org.junit.Assert
import java.util.concurrent.CountDownLatch

private val postServer = PostServer().init("http://192.168.1.26:9999")

class RemoteTest : AndroidTestCase() {
    fun testPostServer() {
        sync<Unit> { postServer.truncate(Unit, it) }

        Assert.assertArrayEquals(arrayOf<PostServer.Post>(), sync { postServer.list(Unit, it) });
        assertEquals(1, sync { postServer.add(PostServer.Post(0, "title", "content"), it) });
        Assert.assertArrayEquals(arrayOf(PostServer.Post(1, "title", "content")), sync { postServer.list(Unit, it) });
    }
}

fun <T> AndroidTestCase.sync(body: ((T) -> Unit) -> Unit): T {
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
