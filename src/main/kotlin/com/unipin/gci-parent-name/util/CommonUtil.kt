package com.unipin.gci-parent-name.util

import com.unipin.gci-parent-name.model.response.DataResponse
import org.springframework.http.HttpHeaders
import org.springframework.http.ResponseEntity
import org.springframework.util.DigestUtils
import java.security.MessageDigest
import java.time.Instant
import java.time.ZoneId

class CommonUtil {

    companion object {
        @JvmStatic
        fun getByPassHeader(secretKey: String): HttpHeaders {
            val currentTimeMillis = System.currentTimeMillis()
            val rawSign = currentTimeMillis.toString() + secretKey
            val headers = HttpHeaders()
            headers.set("header-request-time", currentTimeMillis.toString())
            headers.set("header-request-sign", sha256(rawSign))
            headers.set("header-ms-source", "ms-gci-storytaco")
            return headers
        }

        @JvmStatic
        fun sha256(input: String): String {
            val bytes = input.toByteArray()
            val md = MessageDigest.getInstance("SHA-256")
            val digest = md.digest(bytes)
            return digest.joinToString("") { "%02x".format(it) }
        }

        fun md5(input:String): String {
            return DigestUtils.md5DigestAsHex(input.toByteArray())
        }

        /**
         * Returns the current UNIX timestamp in the specified timezone.
         *
         * @param timezone The desired timezone for conversion (default: "Asia/Jakarta" for UTC+7).
         * @return A string representing the current timestamp in seconds (UNIX format).
         *
         * Example Usage:
         * val timestamp = getCurrentTimestampInUTC7()  // Gets current timestamp in UTC+7 (Jakarta)
         */

        fun getCurrentTimestamp(timezone: String = "Asia/Jakarta"): String {
            val instant = Instant.now() // Get current UTC time

            return instant.atZone(ZoneId.of(timezone))
                .toEpochSecond()
                .toString() // Convert to UNIX timestamp format
        }

        fun successReturn(
            data: Map<String, Any>,
        ): ResponseEntity<*> {
            return ResponseEntity.ok(
                DataResponse(
                    1, data = data
                )
            )
        }
    }
}
