package com.example.coupon.kafka

import com.example.coupon.redisonly.IssueCouponReq
import com.example.coupon.redisonly.RedisOnlyCouponService
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.boot.autoconfigure.kafka.KafkaProperties
import org.springframework.context.annotation.DependsOn
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
        val result = redisOnlyCouponService.issueCoupon(IssueCouponReq(id = message))
        val jsonStr = objectMapper.writeValueAsString(result)
        val kafkaResult = kafkaProducer.send("response-topic", jsonStr)
        val completed = kafkaResult.join()

        println("msg received. completed($completed)    <<$jsonStr>>    <<$result>>")
    }
}