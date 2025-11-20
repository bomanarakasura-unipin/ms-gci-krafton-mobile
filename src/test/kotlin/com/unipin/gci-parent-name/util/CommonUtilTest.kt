package com.unipin.gci-parent-name.util

import org.junit.jupiter.api.Test
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import org.junit.jupiter.api.Assertions.*


@SpringBootTest(properties = ["spring.cloud.config.enabled=false"])
@ActiveProfiles("test")
class CommonUtilTest {

    @Test
    fun md5() {
        // Prepare the expected hashed string
        val expected:String = "81dc9bdb52d04dc20036dbd8313ed055"
        val input:String = "1234"

        assertEquals(expected, CommonUtil.md5(input))
    }
}
