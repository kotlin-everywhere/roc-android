package com.github.kotlin_everywhere.rpc.testRemote

val testRemote = TestRemote().apply {
    methods.apply {
        testGet { 1 }
        testGetFun { it + 1 }

        testPost { 2 }
        testPostSup { it + 2 }

        testPut { 3 }
        testPutSup { it + 3 }

        testDelete { 4 }
        testDeleteSup { it + 4 }
    }

    name { javaClass.simpleName }

    a.apply {
        name { javaClass.simpleName }
    }

    a.b.apply {
        name { javaClass.simpleName }
    }

    async.apply {
        name { javaClass.simpleName }
        sum { it.sum() }
    }
}

fun main(args: Array<String>) {
    testRemote.runServer(port = 9999)
}

