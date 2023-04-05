package com.itmo.services.external

import com.itmo.services.external.models.AnswerMethod
import com.itmo.services.external.models.ClientSecretRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
import java.util.concurrent.Callable
import java.util.concurrent.CompletableFuture
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.function.Supplier
import javax.annotation.PostConstruct

@Component
object ExternalClientSecretConfig {

    private lateinit var _transactionClientSecret: String
    val transactionClientSecret: String get() { return _transactionClientSecret }

    private lateinit var _pollingClientSecret: String
    val pollingClientSecret: String get() { return _pollingClientSecret }

    private lateinit var _callbackClientSecret: String
    val callbackClientSecret: String get() { return _callbackClientSecret }


    private fun generateClientSecret(projectId: String, answerMethod: AnswerMethod, externalSystemApi: ExternalSystemApi): String {
        val clientSecretRequestDto = ClientSecretRequestDto(
            "name",
            "callback",
            projectId,
            answerMethod,
            0
        )
        return runBlocking {
            externalSystemApi.getClientSecret(clientSecretRequestDto).clientSecret
        }
    }


    @PostConstruct
    private fun init() {
        val executor: ExecutorService = Executors.newCachedThreadPool()
        val client = ExternalSystemClient(executor)
        val externalSystem = ExternalSystemApi(client)

        try {
            val projectId = runBlocking { externalSystem.getProjectId("name").id }
            _callbackClientSecret = executor.submit(Callable { generateClientSecret(projectId, AnswerMethod.CALLBACK, externalSystem) }).get()
            _pollingClientSecret = executor.submit(Callable { generateClientSecret(projectId, AnswerMethod.POLLING, externalSystem) }).get()
            _transactionClientSecret = executor.submit(Callable { generateClientSecret(projectId, AnswerMethod.TRANSACTION, externalSystem) }).get()
        } catch (e: Exception) {
            println("Unable to get client secrets from external system")
        }

        executor.shutdown()
    }

}
