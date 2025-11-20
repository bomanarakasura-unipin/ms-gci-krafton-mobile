package com.unipin.gci-parent-name.model.childcode

import com.unipin.gci-parent-name.util.CommonUtil

class ChildCodeDeliveryRq(
    val userId: String,
    val productId: String,
    val txnId: String,
    var signature:String = ""
) {
    /**
     * Generate request body that comply with the API Docs
     */
    fun toRequest(): Map<String, Any> {
        return mapOf(
            "userid" to userId,
            "productid" to productId,
            "transactionid" to txnId,
            "signature" to signature,
        )
    }

    /**
     * Generate signature using MD5 (refers to API Docs)
     */
    fun generateSignature(secretKey: String) {
        // Preserve variabel for signature
        var signString: String = ""

        // Loop each property of the object then append to signString
        // Use reflection
        signString += "$userId$productId$txnId"

        // append the secret key
        signString += secretKey

        // Hash signstring with md5 method
        signature = CommonUtil.md5(signString)
    }
}
