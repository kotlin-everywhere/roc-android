package com.github.kotlin_everywhere.rpc

import com.github.kittinunf.fuel.httpGet
import com.github.kittinunf.fuel.httpPost
import com.google.gson.Gson

private val defaultGson: Gson = Gson()

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
                .filter { Endpoint::class.java.isAssignableFrom(it.returnType) }
                .forEach {
                    val fetch = it(this) as Endpoint<*, *>
                    fetch.initialize(this, it.name.substring(3, 4).toLowerCase() + it.name.substring(4))
                }
    }


    inline fun <reified R : Any> get(url: String? = null): Producer<R> {
        return Producer(Method.GET, R::class.java, url)
    }

    inline fun <reified R : Any> post(url: String? = null): Producer<R> {
        return Producer(Method.POST, R::class.java, url)
    }
}

fun <T : Remote> T.init(base: String): T {
    initialize(base)
    return this
}

open class Endpoint<P, R>(private val method: Method, private val responseClass: Class<R>, url: String?) {
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

    protected fun call(parameter: P): R {
        val jsonParameter = if (parameter == Unit) null else remote.gson.toJson(parameter)
        val url = remote.base + url
        val request = when (method) {
            Method.GET -> {
                url.httpGet(jsonParameter?.let { listOf("data" to it) })
            }
            Method.POST -> {
                url.httpPost()
                        .let { r -> jsonParameter?.let { r.body(it) } ?: r }
                        .header("Content-Type" to "application/json; charset=UTF-8")
            }
        }
        val result = request.responseString().third
        return if (responseClass == Unit::class.java) {
            @Suppress("UNCHECKED_CAST")
            (Unit as R);
        } else {
            remote.gson.fromJson(result.get(), responseClass);
        }
    }

}

class Function<P, R>(method: Method, responseClass: Class<R>, url: String?) : Endpoint<P, R>(method, responseClass, url) {
    operator fun invoke(parameter: P): R {
        return call(parameter)
    }
}

class Producer<R>(method: Method, responseClass: Class<R>, url: String?) : Endpoint<Unit, R>(method, responseClass, url) {
    operator fun invoke(): R {
        return call(Unit)
    }
}

inline fun <P, reified R : Any> get(url: String? = null): Function<P, R> {
    return Function(Method.GET, R::class.java, url)
}

inline fun <P, reified R : Any> post(url: String? = null): Function<P, R> {
    return Function(Method.POST, R::class.java, url)
}
