package com.unipin.gci-parent-name.service

import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.unipin.gci-parent-name.exception.GameProviderException
import com.unipin.gci-parent-name.model.request.DeliveryRequest
import com.unipin.gci-parent-name.configuration.variable.ErrorCode
import com.unipin.gci-parent-name.model.childcode.ChildCodeCheckoutRq
import com.unipin.gci-parent-name.model.childcode.ChildCodeDeliveryRq
import com.unipin.gci-parent-name.model.request.InquiryRequest
import com.unipin.gci-parent-name.model.childcode.ChildCodeInquiryRq
import com.unipin.gci-parent-name.model.request.CheckoutRequest
import com.unipin.gci-parent-name.util.CommonUtil
import logging.lib.annotation.LogExecution
import logging.lib.util.JsonLogFactory
import org.springframework.http.ResponseEntity
import org.springframework.stereotype.Service
import java.util.*

@Service
class ChildCodeService
    (
    private val credentialService: CredentialService,
    private val httpService: HttpService,
    private val objectMapper: ObjectMapper,
) {
    // Logger
    private val log = JsonLogFactory.getLogger(ChildCodeService::class.java)

    @LogExecution(enableSpan = false, eventName = "required field")
    fun requiredField(): ResponseEntity<*> {
        return CommonUtil.successReturn(
            mapOf(
                "field" to listOf("userId"), "type" to listOf("alphanumeric")
            )
        )
    }


    @LogExecution
    fun inquiry(req: InquiryRequest): ResponseEntity<*> {
        // Get credential and inquiry URL
        val credential = credentialService.getChildCodeCredentials(UUID.fromString(req.credentialGuid))

        // Construct inquiry request
        // TODO: Change this code
        // val inquiryRequest = ChildCodeInquiryRq(
        //     userid = req.userId,
        //     productId = req.productId,
        // )
        // Generate signature
        // inquiryRequest.generateSignature(credential.secretKey)

        // Send HTTP request
        // TODO: Adjust it as per of your needs
        // val inquiryResponse = httpService.sendHttpPost(
        //     url = credential.inquiryUrl,
        //     objectMapper.writeValueAsString(inquiryRequest),
        //     JsonNode::class.java
        // )
        // val gpResult = inquiryResponse.get("status").asText()
        //
        // // Check if status is equal to valid
        // if (gpResult != "valid") {
        //     throw GameProviderException("Invalid request",ErrorCode.INVALID_PARAM)
        // }

        // Return username on inquiry
        return CommonUtil.successReturn(
            mapOf(
                "username" to req.userId
            )
        )
    }

    @LogExecution
    fun checkout(req: CheckoutRequest): ResponseEntity<*> {
        // Get credential and inquiry URL
        val credential = credentialService.getChildCodeCredentials(UUID.fromString(req.credentialGuid))

        // TODO: Adjust below code as per your needs
        // // Generate transaction ID
        // val txnId = UUID.randomUUID().toString()
        //
        // // Construct inquiry request
        // val checkoutRequest = ChildCodeCheckoutRq(
        //     userid = req.userId,
        //     productid = req.productId,
        //     transactionid = txnId,
        // )
        //
        // // Generate signature
        // checkoutRequest.generateSignature(credential.secretKey)

        // Send HTTP request
        // val checkoutResponse = httpService.sendHttpPost(
        //     url = credential.inquiryUrl,
        //     objectMapper.writeValueAsString(checkoutRequest),
        //     JsonNode::class.java
        // )
        //
        // // Return early if has error (error code is exists)
        // // Handle and throw error
        // if (checkoutResponse.get("status").asText() != "valid") {
        //     throw GameProviderException("Invalid request",ErrorCode.INVALID_PARAM)
        // }

        // Return flowID if any
        return CommonUtil.successReturn(
            mapOf(
                "flowId" to txnId, // Transaction ID as flow ID
            )
        )
    }

    @LogExecution
    fun delivery(req: DeliveryRequest): ResponseEntity<*> {
        // Get credential
        val credential = credentialService.getChildCodeCredentials(UUID.fromString(req.credentialGuid))

        // TODO: Adjust as per your needs
        // // Construct delivery request
        // val deliveryRequest = ChildCodeDeliveryRq(
        //     userId = req.userId,
        //     productId = req.productId,
        //     txnId = req.txnNo,
        // )
        //
        // // Generate signature
        // deliveryRequest.generateSignature(credential.secretKey)
        //
        // // Send request
        // val deliveryResponse = httpService
        //     .sendHttpPost(
        //         credential.deliveryUrl,
        //         objectMapper.writeValueAsString(deliveryRequest),
        //         String::class.java
        //     )
        //
        // // Check if GP response is not a valid JSON
        // // Try to parse string and return null if not success
        // val deliveryResultObj = runCatching { objectMapper.readTree(deliveryResponse) }.getOrNull()
        //
        // if (deliveryResultObj == null)  {
        //     throw GameProviderException("Invalid GP Response",ErrorCode.INVALID_PARAM)
        // }
        //
        // // Return early if has error (error code is exists)
        // // Handle and throw error
        // // Status always containing "error" value
        // if (deliveryResultObj.has("status")) {
        //     throw GameProviderException("Invalid GP Response",ErrorCode.INVALID_PARAM)
        // }
        //
        // // Get external transaction ID and display transaction ID
        // val firstDeliveryResponseObj = deliveryResultObj.first()

        return CommonUtil.successReturn(
            mapOf(
                "message" to "Success",
            )
        )
    }

    private fun handleErrorResponse(code: Int) {
        when (code) {
          // TODO: Adjust error code based on your game specifications
            1001 -> throw GameProviderException("Invalid Param", ErrorCode.INVALID_PARAM)
            4000 -> throw GameProviderException("User can not purchase", ErrorCode.NOT_ELIGIBLE)
            else -> throw GameProviderException(
                "Unable to Contact Game Provider", ErrorCode.GP_NOT_RESPOND
            )
        }
    }
}
