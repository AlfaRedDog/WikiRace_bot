package com.itmo.services.kafka.models

import kotlinx.serialization.*
import org.apache.kafka.common.serialization.Deserializer
import com.google.gson.Gson

@Serializable
data class AuthRequestMessage(
    val token: String,
    val authId: String
)

class AuthRequestMessageDeserializer : Deserializer<AuthRequestMessage> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        super.configure(configs, isKey)
    }
    override fun deserialize(topic: String, data: ByteArray): AuthRequestMessage? {
        val json = String(data)
        val p = Gson().fromJson(json, AuthRequestMessage::class.java)
        return p
    }

    override fun close() {
    }
}