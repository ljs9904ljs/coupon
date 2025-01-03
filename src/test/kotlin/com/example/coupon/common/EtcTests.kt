package com.example.coupon.common

import org.junit.jupiter.api.Test
import kotlin.system.measureTimeMillis

class EtcTests {


    fun getRandomInt(): Int {
        return (0..5).random()
    }

    @Test
    fun testRandomInt() {
        val totalTime = measureTimeMillis {
            repeat(10000) {
                getRandomInt()
            }
        }

        println("totalTime: $totalTime")
    }
}