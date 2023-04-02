import com.itmo.microservices.demo.external.ExternalSystemApi
import com.itmo.microservices.demo.external.ExternalSystemClient
import com.itmo.microservices.demo.external.models.AnswerMethod
import com.itmo.microservices.demo.external.models.ClientSecretRequestDto
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.springframework.stereotype.Component
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


    private suspend fun generateClientSecret(projectId: String, answerMethod: AnswerMethod, externalSystemApi: ExternalSystemApi): String =
        withContext(Dispatchers.IO) {
            val clientSecretRequestDto = ClientSecretRequestDto(
                "name",
                "callback",
                projectId,
                answerMethod,
                0
            )
            externalSystemApi.getClientSecret(clientSecretRequestDto).clientSecret
        }

    @PostConstruct
    private fun init() {
        val executor: ExecutorService = Executors.newCachedThreadPool()
        val client = ExternalSystemClient(executor)
        val externalSystem = ExternalSystemApi(client);
        val projectId = runBlocking { externalSystem.getProjectId("name").id }

        val callbackFuture = CompletableFuture.supplyAsync(Supplier { runBlocking { generateClientSecret(projectId, AnswerMethod.CALLBACK, externalSystem) } }, executor)
        val pollingFuture = CompletableFuture.supplyAsync(Supplier { runBlocking { generateClientSecret(projectId, AnswerMethod.POLLING, externalSystem) } }, executor)
        val transactionFuture = CompletableFuture.supplyAsync(Supplier { runBlocking { generateClientSecret(projectId, AnswerMethod.TRANSACTION, externalSystem) } }, executor)

        try {
            _callbackClientSecret = callbackFuture.get()
            _pollingClientSecret = pollingFuture.get()
            _transactionClientSecret = transactionFuture.get()
        } catch (e: Exception) {
            println("Unable to get client secrets from external system")
        }
    }
}
