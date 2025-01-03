package com.example.coupon.redisonly

class IssueCouponReq(
    val id: String  // idempotent key
)