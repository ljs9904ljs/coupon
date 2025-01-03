package com.example.coupon.redisonly

import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.beans.factory.annotation.Value
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service

@Service
class RedisOnlyCouponService(
    @Autowired
    private val redisTemplate: StringRedisTemplate
) {

    @PostConstruct
    fun initRedis() {
//        redisTemplate.opsForValue().set("fcfs", "10000")


        redisTemplate.opsForValue().set("{1}fcfs", "2000")
        redisTemplate.opsForValue().set("{2}fcfs", "2000")
        redisTemplate.opsForValue().set("{3}fcfs", "2000")
        redisTemplate.opsForValue().set("{4}fcfs", "2000")
        redisTemplate.opsForValue().set("{5}fcfs", "2000")
    }

    fun randomNodeNum(): Int {
        return (1..5).random()
    }

    fun issueCoupon(req: IssueCouponReq): IssueCouponRes {

        Thread.sleep(5000)
        if (redisTemplate.opsForValue().getAndSet(req.id, "1") == null) {

//            val decrementedValue = redisTemplate.opsForValue().decrement("fcfs")
//                ?: return IssueCouponRes(code = -1)

            val decrementedValue = redisTemplate.opsForValue().decrement("{${randomNodeNum()}}fcfs")
                ?: return IssueCouponRes(code = -1)

            if (decrementedValue >= 0) {
                return IssueCouponRes(code = 0, num = decrementedValue.toInt())
            } else {
                return IssueCouponRes(code = -1)
            }
        } else {
            // 더블 클릭에 대한 예외 처리
            return IssueCouponRes(code = 1)
        }
    }

}