package com.itmo.services.kafka.deserializers

import com.google.gson.Gson
import com.itmo.services.kafka.models.SubscriptionInfoResponseMessage
import org.apache.kafka.common.serialization.Deserializer

class SubscriptionInfoResponseMessageDeserializer : Deserializer<SubscriptionInfoResponseMessage> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        super.configure(configs, isKey)
    }

    override fun deserialize(topic: String, data: ByteArray): SubscriptionInfoResponseMessage? {
        val json = String(data)
        val p = Gson().fromJson(json, SubscriptionInfoResponseMessage::class.java)
        return p
    }

    override fun close() {
    }
}