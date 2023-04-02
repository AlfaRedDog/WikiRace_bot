import com.google.gson.Gson
import okhttp3.*
import java.io.IOException
import java.time.Duration
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

    suspend fun <T> executeRequest(request: Request, clazz: Class<T>): T = suspendCoroutine { cont ->
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call, response: Response) {
                try {
                    if (response.isSuccessful) {
                        val body = response.body()?.string()
                        if (body != null) {
                            cont.resume(mapper.fromJson(body, clazz))
                        } else {
                            cont.resumeWithException(Exception("Empty response body"))
                        }
                    } else {
                        cont.resumeWithException(Exception("Response code: ${response.code()}"))
                    }
                } catch (e: Exception) {
                    cont.resumeWithException(e)
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                cont.resumeWithException(e)
            }
        })
    }
}
