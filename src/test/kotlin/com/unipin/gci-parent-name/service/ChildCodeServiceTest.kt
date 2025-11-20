package com.unipin.gci-parent-name.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.github.f4b6a3.uuid.UuidCreator
import com.ninjasquad.springmockk.MockkBean
import com.unipin.gci-parent-name.configuration.variable.ErrorCode
import com.unipin.gci-parent-name.exception.GameProviderException
import com.unipin.gci-parent-name.model.childcode.ChildCodeCredentials
import com.unipin.gci-parent-name.model.request.CheckoutRequest
import com.unipin.gci-parent-name.model.request.DeliveryRequest
import com.unipin.gci-parent-name.model.request.InquiryRequest
import com.unipin.gci-parent-name.model.response.DataResponse
import com.unipin.gci-parent-name.util.CommonUtil

import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.assertThrows
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.http.HttpStatus
import org.springframework.test.context.ActiveProfiles
import java.util.UUID
import kotlin.String
import kotlin.collections.get

@SpringBootTest(properties = ["spring.cloud.config.enabled=false"])
@ActiveProfiles("test")
class ChildCodeServiceTest {
    @MockkBean
    private lateinit var credentialService: CredentialService

    @MockkBean
    private lateinit var httpService: HttpService

    @Autowired
    private lateinit var childcodeService: ChildCodeService

    private val objectMapper = ObjectMapper()

    // Prepare predefined data to be initialized
    companion object {
        private val TEST_CHILD_CODE:String = "child_code"
        private lateinit var TEST_CREDENTIAL_GUID:String
        private lateinit var TEST_USER_ID :String
        private lateinit var TEST_PRODUCT_ID :String
        private lateinit var TEST_AMOUNT :String
        private lateinit var TEST_CURRENCY :String
        private lateinit var TEST_CHANNEL_CODE :String
        private lateinit var TEST_FLOW_ID :String
        private lateinit var TEST_TXN_NO :String
        private lateinit var TEST_CUSTOMER_ID :String
        private lateinit var TEST_APP_ID :String
        private lateinit var TEST_USERNAME :String
        private lateinit var TEST_MAIL_ID :String
    }

    @BeforeEach
    fun setUp(){
        // Assign predefined data
        TEST_CREDENTIAL_GUID = UuidCreator.getTimeOrderedEpoch().toString()
        TEST_USER_ID = "111111"
        TEST_PRODUCT_ID = "unipingem_01"
        TEST_AMOUNT = "50000"
        TEST_CURRENCY = "IDR"
        TEST_CHANNEL_CODE = "GOPAY_ID"
        TEST_FLOW_ID = "123e4567-e89b-12d3-a456-426614174000"
        TEST_TXN_NO = "S251062713005"
        TEST_CUSTOMER_ID = "9999"
        TEST_USERNAME = "unipin"
        TEST_APP_ID = "12345"
        TEST_MAIL_ID = "MAILID"

        // Mock utirl
        mockkStatic(CommonUtil::class)
        mockkObject(CommonUtil)
    }

    fun setupCredential(): ChildCodeCredentials {
        return ChildCodeCredentials(
            secretKey = "test",
            checkoutUrl = "test",
            inquiryUrl = "test",
            deliveryUrl = "test",
            serverVersion = 222,
        )
    }

    fun createInquiryRequest(): InquiryRequest {
        return InquiryRequest(
            credentialGuid = TEST_CREDENTIAL_GUID,
            childCode = TEST_CHILD_CODE,
            userId = TEST_USER_ID,
            productId = TEST_PRODUCT_ID,
        )
    }

    fun createInquiryResponse() : JsonNode {
        val response =  """
         {
              "status": "valid"
        }
        """.trimIndent()

        return objectMapper.readTree(response.toByteArray())
    }

    fun createFailedInquiryResponse() : JsonNode {
        val response =  """
            {
                  "status": "error"
            }
        """.trimIndent()

        return objectMapper.readTree(response.toByteArray())
    }

    fun createCheckoutResponse() : JsonNode {
        val response =  """
            {"1712345678901":{"MAIL_ID":1712345678901,"TYPE":11,"TRANSACTION_ID":"$TEST_FLOW_ID","SHOP_INDEX":318,"REWARD":[{"ITEM_CODE":50,"QTY":200}]}}
        """.trimIndent()

        return objectMapper.readTree(response.toByteArray())
    }

    fun createFailedCheckoutResponse() : JsonNode {
        val response =  """
            {
                  "status": "error"
            }
        """.trimIndent()

        return objectMapper.readTree(response.toByteArray())
    }

    fun createCheckoutRequest(): CheckoutRequest {
        return CheckoutRequest(
            credentialGuid = TEST_CREDENTIAL_GUID,
            childCode = TEST_CHILD_CODE,
            userId = TEST_USER_ID,
            productId = TEST_PRODUCT_ID,
        )
    }

    fun createDeliveryRequest(): DeliveryRequest {
        return DeliveryRequest(
            credentialGuid= TEST_CREDENTIAL_GUID,
            childCode= TEST_CHILD_CODE,
            txnNo= TEST_TXN_NO,
            userId = TEST_USER_ID,
            productId = TEST_PRODUCT_ID,
        )
    }

    fun createDeliveryResponse(): String {
        val response = """
            {"1712345678901":{"MAIL_ID":"$TEST_MAIL_ID","TYPE":11,"TRANSACTION_ID":"ABC123XYZ","SHOP_INDEX":318,"REWARD":[{"ITEM_CODE":50,"QTY":200}]}}
        """.trimIndent()

        return response
    }

    fun createFailedDeliveryResponse(): String {
        val response = """
           {"ErrorCode":"1001","status":"error"}
        """.trimIndent()

        return response
    }

    @Test
    fun `requiredField should return success`() {
        // Action
        // Call API programatically
        val result = childcodeService.requiredField()

        // Assert
        // Assert status code
        assertEquals(HttpStatus.OK, result.statusCode)

        // Assert the field
        assertNotNull(result.body)
        val response = result.body as DataResponse
        // Assert status
        assertEquals(1, response.status)

        // Assert data
        val data = response.data as Map<*,*>
        assertEquals(listOf("userId"), data["field"])
        assertEquals(listOf("alphanumeric"), data["type"])
    }

    @Test
    fun `inquiry should return success`(){
        // Prepare request & credential
        val request = createInquiryRequest()
        val credential = setupCredential()

        // mock call to credential and HTTP service
        every { credentialService.getChildCodeCredentials(any()) } returns credential
        every { httpService.sendHttpPost(any(),any(), JsonNode::class.java) } returns createInquiryResponse()

        // action
        val result = childcodeService.inquiry(request)

        assertNotNull(result.body)
        // assert
        val response = result.body as DataResponse

        // assert status
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(1, response.status)
    }

    @Test
    fun `inquiry should throw GameProviderError if GP not return valid`(){
        // Prepare request & credential
        val request = createInquiryRequest()
        val credential = setupCredential()

        // mock call to credential and HTTP service
        every { credentialService.getChildCodeCredentials(any()) } returns credential
        every { httpService.sendHttpPost(any(),any(), JsonNode::class.java) } returns createFailedInquiryResponse()

        // assert exception
        val exception = assertThrows<GameProviderException> {
            childcodeService.inquiry(request)
        }

        assertEquals(ErrorCode.INVALID_PARAM, exception.errorCode)
    }

    @Test
    fun `checkout should return success with all fields`()  {
        // prepare request
        // Prepare request & credential
        val request = createCheckoutRequest()
        val credential = setupCredential()

        // mock call to credential and HTTP service
        every { credentialService.getChildCodeCredentials(any()) } returns credential
        every { httpService.sendHttpPost(any(),any(), JsonNode::class.java) } returns createInquiryResponse()

        // action
        val result = childcodeService.checkout(request)

        assertNotNull(result.body)
        // assert
        val response = result.body as DataResponse

        // assert status
        assertEquals(HttpStatus.OK, result.statusCode)
        assertEquals(1, response.status)

        // assert data
        val data = response.data as Map<*, *>
        assertNotNull(data["flowId"])
        assertTrue(data["flowId"].toString().isNotEmpty())
    }

    @Test
    fun `checkout should throw error if GP does not return flowId`(){
        // Prepare request & credential
        val request = createCheckoutRequest()
        val credential = setupCredential()

        // mock call to credential and HTTP service
        every { credentialService.getChildCodeCredentials(any()) } returns credential
        every { httpService.sendHttpPost(any(),any(), JsonNode::class.java) } returns createFailedCheckoutResponse()

        // assert exception
        val exception = assertThrows<GameProviderException> {
            childcodeService.checkout(request)
        }

        assertEquals(ErrorCode.INVALID_PARAM, exception.errorCode)
    }

    @Test
    fun `delivery should return success`() {
        // prepare request
        val request = createDeliveryRequest()
        val credential = setupCredential()

        // mock call to credential and HTTP service
        every { credentialService.getChildCodeCredentials(any()) } returns credential
        every { httpService.sendHttpPost(any(),any(), String::class.java) } returns createDeliveryResponse()

        // action
        val result = childcodeService.delivery(request)

        assertNotNull(result.body)

        // assert
        val response = result.body as DataResponse
        val data = objectMapper.convertValue(response.data, object: TypeReference<Map<String, Any>>(){})

        // assert status
        assertEquals(HttpStatus.OK, result.statusCode)

        // assert result
        assertEquals( TEST_MAIL_ID,data["extTxnNo"])
    }

    @Test
    fun `delivery should return error if status not 1`() {
        // prepare request
        val request = createDeliveryRequest()
        val credential = setupCredential()

        // mock call to credential and HTTP service
        every { credentialService.getChildCodeCredentials(any()) } returns credential
        every { httpService.sendHttpPost(any(),any(), String::class.java) } returns createFailedDeliveryResponse()

        // action
        val exception = assertThrows<GameProviderException> {
            childcodeService.delivery(request)
        }

        assertEquals(ErrorCode.INVALID_PARAM, exception.errorCode)
    }

     @Test
    fun `delivery should return error if GP response is not valid JSON`() {
        // prepare request
        val request = createDeliveryRequest()
        val credential = setupCredential()

        // mock call to credential and HTTP service
        every { credentialService.getChildCodeCredentials(any()) } returns credential
        every { httpService.sendHttpPost(any(),any(), String::class.java) } returns "INVALID JSON"

        // action
        val exception = assertThrows<GameProviderException> {
            childcodeService.delivery(request)
        }

        assertEquals(ErrorCode.INVALID_PARAM, exception.errorCode)
    }
}
