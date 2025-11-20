package com.unipin.gci-parent-name.model.request

data class DeliveryRequest(
    val credentialGuid: String,
    val childCode: String,
    val txnNo: String,
    val userId: String,
    val productId: String,
)
