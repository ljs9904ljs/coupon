package com.example.coupon.redisonly

class IssueCouponRes(
    // response code.
    // 0: 성공
    // 1: 더블 클릭
    // -1: 실패
    val code: Int,
    val num: Int? = null
)