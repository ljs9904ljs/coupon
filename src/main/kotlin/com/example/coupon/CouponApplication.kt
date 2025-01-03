package com.example.coupon

import com.example.coupon.common.RedisClusterConfiguration
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication(exclude = [DataSourceAutoConfiguration::class])
@EnableConfigurationProperties(RedisClusterConfiguration::class)
class CouponApplication

fun main(args: Array<String>) {
	runApplication<CouponApplication>(*args)
}
