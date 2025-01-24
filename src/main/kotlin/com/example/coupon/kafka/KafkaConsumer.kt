package com.example.coupon.kafka

import com.example.coupon.redisonly.IssueCouponReq
import com.example.coupon.redisonly.RedisOnlyCouponService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.kafka.annotation.KafkaListener
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Component

@Component
class KafkaConsumer(
    private val redisOnlyCouponService: RedisOnlyCouponService,
    private val kafkaProducer: KafkaProducer,
    private val objectMapper: ObjectMapper
) {

    @KafkaListener(topics = ["mytopic"])
    fun listen(message: String) {
        val result = redisOnlyCouponService.issueCoupon(IssueCouponReq(message))
        val jsonStr = objectMapper.writeValueAsString(result)
        kafkaProducer.send("response-topic", jsonStr)
        println("msg received. code: ${result.code}, num: ${result.num}")
    }
}