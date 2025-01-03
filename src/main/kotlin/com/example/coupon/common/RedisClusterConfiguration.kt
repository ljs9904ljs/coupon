package com.example.coupon.common

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.stereotype.Component

@Component
@ConfigurationProperties(prefix = "spring.data.redis.cluster")
class RedisClusterConfiguration {
    lateinit var nodes: List<String>
}