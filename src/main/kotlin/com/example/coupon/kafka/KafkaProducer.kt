package com.example.coupon.kafka

import org.springframework.kafka.core.KafkaTemplate
import org.springframework.kafka.support.SendResult
import org.springframework.stereotype.Component
import java.util.concurrent.CompletableFuture


@Component
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, String>
) {

    fun send(topic: String, message: String): CompletableFuture<SendResult<String, String>> {
        return kafkaTemplate.send(topic, message)
    }

}