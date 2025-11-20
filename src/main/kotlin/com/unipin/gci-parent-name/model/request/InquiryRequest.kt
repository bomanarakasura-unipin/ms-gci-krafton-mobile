package com.unipin.gci-parent-name.model.request

data class InquiryRequest(
    val credentialGuid: String,
    val childCode: String,
    val userId: String,
    val productId: String,
)
