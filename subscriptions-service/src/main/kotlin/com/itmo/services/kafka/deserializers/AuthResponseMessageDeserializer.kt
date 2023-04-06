package com.itmo.services.kafka.deserializers

import com.google.gson.Gson
import com.itmo.services.kafka.models.AuthResponseMessage
import org.apache.kafka.common.serialization.Deserializer

class AuthResponseMessageDeserializer : Deserializer<AuthResponseMessage> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        super.configure(configs, isKey)
    }

    override fun deserialize(topic: String, data: ByteArray): AuthResponseMessage? {
        val json = String(data)
        val p = Gson().fromJson(json, AuthResponseMessage::class.java)
        return p
    }

    override fun close() {
    }
}