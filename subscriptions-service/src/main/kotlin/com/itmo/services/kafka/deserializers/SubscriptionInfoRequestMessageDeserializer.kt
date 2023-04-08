package com.itmo.services.kafka.deserializers

import com.google.gson.Gson
import com.itmo.services.kafka.models.SubscriptionInfoRequestMessage
import org.apache.kafka.common.serialization.Deserializer

class SubscriptionInfoRequestMessageDeserializer : Deserializer<SubscriptionInfoRequestMessage> {
    override fun configure(configs: MutableMap<String, *>?, isKey: Boolean) {
        super.configure(configs, isKey)
    }
    override fun deserialize(topic: String, data: ByteArray): SubscriptionInfoRequestMessage? {
        val json = String(data)
        val p = Gson().fromJson(json, SubscriptionInfoRequestMessage::class.java)
        return p
    }

    override fun close() {
    }
}