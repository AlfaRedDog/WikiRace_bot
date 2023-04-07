package com.itmo.services.common.metrics

import com.itmo.microservices.commonlib.metrics.CommonMetricsCollector
import org.springframework.stereotype.Component

@Component
class SubscriptionsServiceMetricsCollector(serviceName: String): CommonMetricsCollector(serviceName) {
    constructor() : this(SERVICE_NAME)

    companion object {
        const val SERVICE_NAME = "subscriptions_service"
    }
}
