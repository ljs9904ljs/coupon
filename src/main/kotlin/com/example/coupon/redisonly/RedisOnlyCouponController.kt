package com.example.coupon.redisonly

import org.springframework.beans.factory.annotation.Autowired
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RestController


@RestController
class RedisOnlyCouponController(
    @Autowired
    private val redisOnlyCouponService: RedisOnlyCouponService
) {

    @PostMapping("/redisonly/coupon")
    fun issueCoupon(@RequestBody req: IssueCouponReq): IssueCouponRes {
        return redisOnlyCouponService.issueCoupon(req)
    }

}