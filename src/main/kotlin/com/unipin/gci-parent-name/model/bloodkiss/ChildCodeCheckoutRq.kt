package com.unipin.gci-parent-name.model.childcode

import com.unipin.gci-parent-name.util.CommonUtil

// TODO: Adjust as per your requirement
class ChildCodeCheckoutRq(
    val userid: String,
    val productid: String,
    val transactionid: String,
    var signature: String = "",
) {

    fun generateSignature(secretKey: String) {
        // Preserve variabel for signature
        var signString: String = ""

        // Loop each property of the object then append to signString
        // Use reflection
        signString += "$userid$productid$transactionid"

        // append the secret key
        signString += secretKey

        // Hash signstring with md5 method
        signature = CommonUtil.md5(signString)
    }
}
