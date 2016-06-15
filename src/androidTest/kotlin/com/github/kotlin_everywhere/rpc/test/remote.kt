package com.github.kotlin_everywhere.rpc.test

import android.test.AndroidTestCase
import com.github.kotlin_everywhere.rpc.init
import com.github.kotlin_everywhere.rpc.testRemote.TestRemote
import org.junit.Assert
import java.util.concurrent.CountDownLatch

private val testRemote = TestRemote().init("http://192.168.56.1:9999")

class RemoteTest : AndroidTestCase() {
    fun testNested() {
        Assert.assertEquals("TestRemote", testRemote.name())
        Assert.assertEquals("TestRemoteA", testRemote.a.name())
        Assert.assertEquals("TestRemoteB", testRemote.a.b.name())
    }

    fun testMethods() {
        testRemote.methods.apply {
            Assert.assertEquals(1, testGet())
            Assert.assertEquals(2, testGetFun(1))

            Assert.assertEquals(2, testPost())
            Assert.assertEquals(3, testPostSup(1))

            Assert.assertEquals(3, testPut())
            Assert.assertEquals(4, testPutSup(1))

            Assert.assertEquals(4, testDelete())
            Assert.assertEquals(5, testDeleteSup(1))
        }
    }

    fun testAsync() {
        Assert.assertEquals("TestAsync", sync<String> { testRemote.async.name(it) })
        Assert.assertEquals(55, sync<Int> { testRemote.async.sum((1..10).toList().toTypedArray(), it) })
    }
}

fun <T> sync(block: ((T) -> Unit) -> Unit): T {
    val latch = CountDownLatch(1)
    var value: T? = null
    block { value = it; latch.countDown() }
    latch.await()
    return value!!
}

