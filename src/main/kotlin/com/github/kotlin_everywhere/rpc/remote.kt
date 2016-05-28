package com.github.kotlin_everywhere.rpc

import com.google.gson.Gson
import com.loopj.android.http.AsyncHttpClient
import com.loopj.android.http.AsyncHttpResponseHandler
import com.loopj.android.http.RequestParams
import cz.msebera.android.httpclient.Header
import cz.msebera.android.httpclient.entity.ByteArrayEntity
import java.nio.charset.Charset

private val defaultGson: Gson = Gson()

private val utf8: Charset
    get() = charset("UTF-8")

enum class Method {
    GET, POST
}

open class Remote(internal val gson: Gson = defaultGson) {
    lateinit var base: String

    internal fun initialize(base: String) {
        this.base = base
        javaClass.methods
                .filter { it.parameterTypes.size == 0 }
                .filter { it.name.startsWith("get") }
                .filter { it.returnType == Fetch::class.java }
                .forEach {
                    val fetch = it(this) as Fetch<*, *>
                    fetch.initialize(this, it.name.substring(3, 4).toLowerCase() + it.name.substring(4))
                }
    }
}

fun <T : Remote> T.init(base: String): T {
    initialize(base)
    return this
}


class Fetch<P, R>(private val method: Method, private val responseClass: Class<R>, url: String?) {

    private lateinit var remote: Remote
    private lateinit var url: String

    init {
        if (url != null) {
            this.url = url;
        }
    }

    internal fun initialize(remote: Remote, name: String) {
        this.remote = remote
        try {
            url.length
        } catch(e: UninitializedPropertyAccessException) {
            url = "/" + name;
        }
    }

    operator fun invoke(parameter: P, callback: (R) -> Unit) {
        val client = AsyncHttpClient()
        val responseHandler = ResponseHandler(responseClass, remote.gson, callback)
        val jsonParameter = if (parameter == Unit) null else remote.gson.toJson(parameter)
        val url = remote.base + url
        when (method) {
            Method.GET -> {
                client.get(url, jsonParameter?.let { RequestParams("data", it) }, responseHandler)
            }
            Method.POST -> {
                client.post(null, url, jsonParameter?.let { ByteArrayEntity(it.toByteArray(utf8)) },
                        "application/json; charset=UTF-8", responseHandler)
            }
        }
    }
}

inline fun <P, reified R : Any> get(url: String? = null): Fetch<P, R> {
    return Fetch(Method.GET, R::class.java, url)
}

inline fun <P, reified R : Any> post(url: String? = null): Fetch<P, R> {
    return Fetch(Method.POST, R::class.java, url)
}

class ResponseHandler<R>(private val responseClass: Class<R>, private val gson: Gson, private val callback: (R) -> Unit) : AsyncHttpResponseHandler() {
    override fun onSuccess(p0: Int, p1: Array<out Header>?, p2: ByteArray?) {
        val result = if (responseClass == Unit::class.java) {
            @Suppress("UNCHECKED_CAST")
            (Unit as R)
        } else {
            gson.fromJson(p2!!.toString(utf8), responseClass)
        }
        callback(result)
    }

    override fun onFailure(p0: Int, p1: Array<out Header>?, p2: ByteArray?, p3: Throwable?) {
        throw p3!!
    }
}
