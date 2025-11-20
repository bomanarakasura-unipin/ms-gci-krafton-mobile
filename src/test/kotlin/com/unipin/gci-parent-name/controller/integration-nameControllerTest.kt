package com.unipin.gci-parent-name.controller

import com.ninjasquad.springmockk.MockkBean
import com.unipin.gci-parent-name.model.response.DataResponse
import com.unipin.gci-parent-name.service.ChildCodeService
import io.mockk.every
import org.junit.jupiter.api.Test
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.test.context.TestPropertySource
import org.springframework.test.web.servlet.MockMvc
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath
import org.springframework.test.web.servlet.result.MockMvcResultMatchers.status

@WebMvcTest(integration-nameController::class)
@TestPropertySource(properties = ["spring.cloud.config.enabled=false"])
class integration-nameControllerTest {
    @Autowired
    private lateinit var mockMvc: MockMvc

    @MockkBean
    private lateinit var childCodeService: ChildCodeService

    private companion object {
        private const val BASE_URL = "/api/v1/storytaco"
        private const val SUCCESS_STATUS = 1
        private const val ERROR_STATUS = 0
        private const val VALID_CHILD_CODE = "child_code"
        private const val TEST_CREDENTIAL_GUID = "0196edd3-789d-76a5-8dee-943c61f796a0"
        private const val TEST_USER_ID = "111111"
        private const val TEST_USERNAME = "unipin"
        private const val TEST_PRODUCT_ID = "unipingem_01"
        private const val TEST_AMOUNT = "50000"
        private const val TEST_CURRENCY = "IDR"
        private const val TEST_CHANNEL_CODE = "GOPAY_ID"
        private const val TEST_FLOW_ID = "1111111"
        private const val TEST_TXN_NO = "S251062713005"
        private const val TEST_CUSTOMER_ID = "9999"
    }

    @Test
    fun `requiredFields should return success from valid childCode`() {
        // Setup expected response for mock
        val expectedResponse = ResponseEntity.ok(
            DataResponse(
                status = SUCCESS_STATUS,
                data = mapOf(
                    "field" to listOf("userId"),
                    "type" to listOf("alphanumeric")
                )
            )
        )

        // Mock service
        every { childCodeService.requiredField() } returns expectedResponse

        // Action
        mockMvc.perform(
            post("${BASE_URL}/required-field")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                        "childCode": "${VALID_CHILD_CODE}",
                        "credentialGuid": "${TEST_CREDENTIAL_GUID}"
                    }
                """.trimIndent())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.data.field[0]").value("userId") )
            .andExpect(jsonPath("$.data.type[0]").value("alphanumeric"))

    }

    @Test
    fun `requiredFields should return error from invalid childCode`() {
        // Action
        mockMvc.perform(
            post("${BASE_URL}/required-field")
                .contentType(MediaType.APPLICATION_JSON)
                // Use random childCode
                .content("""
                    {
                        "childCode": "random",
                        "credentialGuid": "${TEST_CREDENTIAL_GUID}"
                    }
                """.trimIndent())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(ERROR_STATUS) )
            .andExpect(jsonPath("$.data.errorCode").value("INVALID_PARAM"))
    }

    @Test
    fun `inquiry should return success from valid childCode`() {
        // Setup expected response for mock
        val expectedResponse = ResponseEntity.ok(
            DataResponse(
                status = SUCCESS_STATUS,
                data = emptyMap<String, Any>()
            )
        )

        // Mock service
        every { childCodeService.inquiry(any()) } returns expectedResponse

        // Action
        mockMvc.perform(
            post("${BASE_URL}/inquiry")
                .contentType(MediaType.APPLICATION_JSON)
                // Use random childCode
                .content("""
                    {
                            "credentialGuid" : "${TEST_CREDENTIAL_GUID}",
                            "childCode" : "${VALID_CHILD_CODE}",
                            "userId" : "${TEST_USER_ID}",
                            "productId" : "${TEST_PRODUCT_ID}"
                    }
                """.trimIndent())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(1))
    }

    @Test
    fun `checkout should return all fields from valid childCode`() {
        // Setup expected response for mock
        val expectedResponse = ResponseEntity.ok(
            DataResponse(
                status = SUCCESS_STATUS,
                data = mapOf(
                    "flowId" to TEST_FLOW_ID,
                )
            )
        )

        // Mock service
        every { childCodeService.checkout(any()) } returns expectedResponse

        // Action
        mockMvc.perform(
            post("${BASE_URL}/checkout")
                .contentType(MediaType.APPLICATION_JSON)
                // Use random childCode
                .content("""
                    {
                            "credentialGuid" : "${TEST_CREDENTIAL_GUID}",
                            "childCode" : "${VALID_CHILD_CODE}",
                            "userId" : "${TEST_USER_ID}",
                            "productId" : "${TEST_PRODUCT_ID}"
                    }
                """.trimIndent())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(1))
            .andExpect(jsonPath("$.data.flowId").value(TEST_FLOW_ID))
    }

    @Test
    fun `delivery should return success from valid childCode`() {
        // Setup expected response for mock
        val expectedResponse = ResponseEntity.ok(
            DataResponse(
                status = SUCCESS_STATUS,
                data = mapOf<String,Any>()
            )
        )

        // Mock service
        every { childCodeService.delivery(any()) } returns expectedResponse

        // Action
        mockMvc.perform(
            post("${BASE_URL}/delivery")
                .contentType(MediaType.APPLICATION_JSON)
                // Use random childCode
                .content("""
                    {
                            "credentialGuid" : "${TEST_CREDENTIAL_GUID}",
                            "childCode" : "${VALID_CHILD_CODE}",
                              "txnNo": "${TEST_TXN_NO}",
                              "userId": "${TEST_USER_ID}",
                              "productId": "${TEST_PRODUCT_ID}"
                    }
                """.trimIndent())
        )
            .andExpect(status().isOk)
            .andExpect(jsonPath("$.status").value(1))
    }
}
