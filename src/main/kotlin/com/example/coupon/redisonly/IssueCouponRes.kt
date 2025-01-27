package com.example.coupon.redisonly

import com.fasterxml.jackson.annotation.JsonProperty

data class IssueCouponRes(
    // response code.
    // 0: 성공
    // 1: 더블 클릭
    // -1: 실패
    val code: Int,
    val clientId: String? = null,
    val num: Int? = null
)