package com.github.kotlin_everywhere.rpc.testRemote

import com.github.kotlin_everywhere.rpc.*

class TestRemote : Remote() {
    val methods = TestMethodRemote()

    val name = get<String>()
    val a = TestRemoteA()

    val async = TestAsync()
}

class TestMethodRemote : Remote() {
    val testGet = get<Int>()
    val testGetFun = get<Int, Int>()

    val testPost = post<Int>()
    val testPostSup = post<Int, Int>()

    val testPut = put<Int>()
    val testPutSup = put<Int, Int>()

    val testDelete = delete<Int>()
    val testDeleteSup = delete<Int, Int>()
}


class TestRemoteA() : Remote() {
    val name = get<String>()
    val b = TestRemoteB()
}

class TestRemoteB() : Remote() {
    val name = get<String>()
}

class TestAsync() : Remote() {
    val name = get<String>()
    val sum = get<Array<Int>, Int>()
}