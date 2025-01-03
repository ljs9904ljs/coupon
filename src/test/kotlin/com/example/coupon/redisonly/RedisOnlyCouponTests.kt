package com.example.coupon.redisonly

import com.fasterxml.jackson.databind.ObjectMapper
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.MediaType
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders
import org.springframework.test.web.servlet.result.MockMvcResultMatchers
import java.util.UUID
import kotlin.reflect.KClass
import kotlin.test.assertEquals

@SpringBootTest
@AutoConfigureMockMvc
class RedisOnlyCouponTests(
    @Autowired
    private val mockMvc: MockMvc,
    @Autowired
    private val objectMapper: ObjectMapper
) {

    fun toJsonStr(obj: Any): String {
        return objectMapper.writeValueAsString(obj)
    }

    private inline fun <reified T> toObj(jsonStr: String): T {
        return objectMapper.readValue(jsonStr, T::class.java)
    }

    @Test
    fun test_POST_IssueCoupon_one_click() {
        val endpoint = "/redisonly/coupon"
        val id = UUID.randomUUID().toString()
        val content = IssueCouponReq(id = id)
        val responseBody = IssueCouponRes(code = 0)

        val result = mockMvc.perform(
            MockMvcRequestBuilders.post(endpoint)
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectMapper.writeValueAsString(content))
        ).andExpect(MockMvcResultMatchers.status().isOk)
        .andReturn()

        toObj<IssueCouponRes>(result.response.contentAsString).also {
            assertEquals(0,  it.code)
        }
    }

    @Test
    fun test_POST_IssueCoupon_double_click() {
        val endpoint = "/redisonly/coupon"
        val id = UUID.randomUUID().toString()
        val content = IssueCouponReq(id = id)
        val oneClickResponseBody = IssueCouponRes(code = 0)
        val doubleClickResponseBody = IssueCouponRes(code = 1)
        val request = MockMvcRequestBuilders.post(endpoint)
            .contentType(MediaType.APPLICATION_JSON)
            .content(objectMapper.writeValueAsString(content))

        mockMvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk)

        val result = mockMvc.perform(request)
            .andExpect(MockMvcResultMatchers.status().isOk)
            .andReturn()

        toObj<IssueCouponRes>(result.response.contentAsString).also {
            assertEquals(doubleClickResponseBody.code, it.code)
        }
    }

}