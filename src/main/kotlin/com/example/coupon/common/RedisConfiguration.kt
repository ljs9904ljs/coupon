package com.example.coupon.common

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.*
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate

@Configuration
class RedisConfiguration(
    @Value("\${spring.data.redis.host}")
    private val host: String,

    @Value("\${spring.data.redis.port}")
    private val port: Int,

    @Autowired
    private val redisClusterConfiguration: RedisClusterConfiguration
) {

//    @Bean
//    fun redisConnectionFactory(): RedisConnectionFactory {
//        val redisStandaloneConfiguration = RedisStandaloneConfiguration()
//        redisStandaloneConfiguration.hostName = host
//        redisStandaloneConfiguration.port = port
//
//        return LettuceConnectionFactory(redisStandaloneConfiguration)
//    }

    @Bean
    fun redisConnectionFactory(): RedisConnectionFactory {
        println("nodes: ${redisClusterConfiguration.nodes}")

        val clusterConfig = RedisClusterConfiguration(
            redisClusterConfiguration.nodes
        )
        return LettuceConnectionFactory(clusterConfig)
    }

    @Bean
    fun redisTemplate(): RedisTemplate<*, *>? {
        val redisTemplate = RedisTemplate<ByteArray, ByteArray>()
        redisTemplate.connectionFactory = redisConnectionFactory()
        return redisTemplate
    }

}