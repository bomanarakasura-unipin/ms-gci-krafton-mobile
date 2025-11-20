package com.unipin.gci-parent-name.controller

import com.unipin.gci-parent-name.configuration.variable.ErrorCode
import com.unipin.gci-parent-name.model.request.CheckoutRequest
import com.unipin.gci-parent-name.model.request.DeliveryRequest
import com.unipin.gci-parent-name.model.request.InquiryRequest
import com.unipin.gci-parent-name.model.request.RequiredFieldRequest
import com.unipin.gci-parent-name.model.response.DataResponse
import com.unipin.gci-parent-name.service.ChildCodeService
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("api/v1/parent-name")
class integration-nameController(
    private val childCodeService: ChildCodeService
) {

    @PostMapping("/required-field")
    fun getRequiredField(@RequestBody requiredFieldRq: RequiredFieldRequest): ResponseEntity<*> {
        // Check for the child code
        // Route to corresponding service base on the child code
        when(requiredFieldRq.childCode) {
            "child_code" -> return childCodeService.requiredField()
        }

        // return error if no matching child code
        return ResponseEntity.ok(
            DataResponse(
                0,
                mapOf(
                    "errorCode" to ErrorCode.INVALID_PARAM,
                    "errorMsg" to "Invalid game code"
                )
            )
        )
    }

    @PostMapping("/inquiry")
    fun inquiry(@RequestBody inquiryRequest: InquiryRequest): ResponseEntity<*> {
        // Check for the child code
        // Route to corresponding service base on the child code
        when(inquiryRequest.childCode) {
            "child_code" -> return childCodeService.inquiry(inquiryRequest)
        }

        // return error if no matching child code
        return ResponseEntity.ok(
            DataResponse(
                0,
                mapOf(
                    "errorCode" to ErrorCode.INVALID_PARAM,
                    "errorMsg" to "Invalid game code"
                )
            )
        )
    }

    @PostMapping("/checkout")
    fun checkout(@RequestBody checkoutRequest: CheckoutRequest): ResponseEntity<*> {
        // Check for the child code
        // Route to corresponding service base on the child code
        when(checkoutRequest.childCode) {
            "child_code" -> return childCodeService.checkout(checkoutRequest)
        }

        // return error if no matching child code
        return ResponseEntity.ok(
            DataResponse(
                0,
                mapOf(
                    "errorCode" to ErrorCode.INVALID_PARAM,
                    "errorMsg" to "Invalid game code"
                )
            )
        )
    }

    @PostMapping("/delivery")
    fun delivery(@RequestBody deliveryRequest: DeliveryRequest): ResponseEntity<*> {
        // Check for the child code
        // Route to corresponding service base on the child code
        when(deliveryRequest.childCode) {
            "child_code" -> return childCodeService.delivery(deliveryRequest)
        }

        // return error if no matching child code
        return ResponseEntity.ok(
            DataResponse(
                0,
                mapOf(
                    "errorCode" to ErrorCode.INVALID_PARAM,
                    "errorMsg" to "Invalid game code"
                )
            )
        )
    }
}
