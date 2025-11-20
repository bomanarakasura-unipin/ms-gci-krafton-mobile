package com.unipin.gci-parent-name.model.request

class CheckoutRequest(
    val credentialGuid: String,
    val childCode: String,
    val userId: String,
    val productId: String,
) {
}
