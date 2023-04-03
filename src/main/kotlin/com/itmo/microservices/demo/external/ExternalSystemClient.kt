package com.itmo.microservices.demo.external

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.*
import java.io.IOException
import java.time.Duration
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

class ExternalSystemClient(private val executor: ExecutorService) {
    private val client = OkHttpClient.Builder()
        .dispatcher(Dispatcher(executor))
        .callTimeout(Duration.ofSeconds(20))
        .build()

    private val mapper = Gson()

    suspend fun <T> executeRequest(request: Request, clazz: Class<T>): T = withContext(Dispatchers.IO) {
        val completableFuture = CompletableFuture<T>()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val body = response.body()?.string()
                        if (body != null) {
                            completableFuture.complete(mapper.fromJson(body, clazz))
                        } else {
                            completableFuture.completeExceptionally(Exception("Empty response body"))
                        }
                    } else {
                        completableFuture.completeExceptionally(Exception("Response code: ${response.code()}"))
                    }
                } catch (e: Exception) {
                    completableFuture.completeExceptionally(e)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                completableFuture.completeExceptionally(e)
            }
        })

        completableFuture.get()
    }

}
