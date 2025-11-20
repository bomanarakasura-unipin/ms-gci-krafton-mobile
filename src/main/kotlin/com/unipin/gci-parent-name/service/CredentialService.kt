package com.unipin.gci-parent-name.service

import com.unipin.gci-parent-name.model.response.CredentialResponse
import com.unipin.gci-parent-name.configuration.properties.ApplicationProperties
import com.unipin.gci-parent-name.model.bloodkiss.ChildCodeCredentials
import org.springframework.stereotype.Service
import java.util.*

@Service
class CredentialService(
    private val httpService: HttpService,
    private val applicationProperties: ApplicationProperties,
) {

    fun getCredential(credentialGuid: UUID): CredentialResponse? {
        try {
            val query = """
            query SecretMsCredentials(${"$"}input: CredentialFilter) {
                    secretMsCredentials(input: ${"$"}input) {
                        data{
                            guid
                            code
                            credentialDetail{
                                guid
                                name
                                value
                            }
                        }
                    }
                }
        """
            val variables = mapOf(
                "input" to mapOf(
                    "guids" to credentialGuid.toString(),
                    "isDecrypt" to true
                )
            )
            return httpService.sendGraphqlRequestWithBypass(
                applicationProperties.MS_FEDERATION_BASE_URL,
                query,
                variables,
                CredentialResponse::class.java
            )
        } catch (e: Exception) {
            throw e
        }
    }

    fun getCredentialDetail(credentialGuid: UUID): List<CredentialResponse.CredentialDetail>? {
        return getCredential(credentialGuid)?.secretMsCredentials?.data?.first()?.credentialDetail
    }

     fun getChildCodeCredentials(credentialGuid: UUID): ChildCodeCredentials {
         val credentials = ChildCodeCredentials()

         getCredentialDetail(credentialGuid)
             ?.forEach {
                 when (it.name) {
                     "SECRET_KEY" -> credentials.secretKey = it.value
                     "INQUIRY_URL" -> credentials.inquiryUrl = it.value
                     "CHECKOUT_URL" -> credentials.checkoutUrl = it.value
                     "DELIVERY_URL" -> credentials.deliveryUrl = it.value
                 }
             }

         return credentials
     }
}
