package com.unipin.gci-parent-name.service

import com.unipin.gci-parent-name.configuration.properties.ApplicationProperties
import com.unipin.gci-parent-name.model.response.CredentialResponse
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import java.util.*

@ExtendWith(MockKExtension::class)
class CredentialServiceTest {

    @MockK
    private lateinit var httpService: HttpService

    @MockK
    private lateinit var applicationProperties: ApplicationProperties

    private lateinit var credentialService: CredentialService

    private companion object {
        private const val TEST_CREDENTIAL_GUID = "0196edd3-789d-76a5-8dee-943c61f796a0"
        private const val TEST_CODE = "PUT_CHILD_CODE_HERE"
        private const val TEST_CREDENTIAL_DETAIL_GUID = "01955b14-fa88-7007-b01e-29df5d1b4384"
        private const val TEST_INQUIRY_URL = "https://api-inquiry.com"
        private const val TEST_DELIVERY_URL = "https://api-delivery.com"
        private const val TEST_SECRET_KEY = "test-secret-key"
        private const val TEST_MS_FEDERATION_URL = "https://test-federation-url.com"
    }

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        credentialService = CredentialService(httpService, applicationProperties)
        every { applicationProperties.MS_FEDERATION_BASE_URL } returns TEST_MS_FEDERATION_URL
    }

    private fun createMockCredentialResponse(): CredentialResponse {
        return CredentialResponse(
            secretMsCredentials = CredentialResponse.SecretMsCredentials(
                data = listOf(
                    CredentialResponse.Credential(
                        guid = TEST_CREDENTIAL_GUID,
                        code = TEST_CODE,
                        credentialDetail = listOf(
                            CredentialResponse.CredentialDetail(
                                guid = TEST_CREDENTIAL_DETAIL_GUID,
                                name = "INQUIRY_URL",
                                value = TEST_INQUIRY_URL
                            ),
                            CredentialResponse.CredentialDetail(
                                guid = TEST_CREDENTIAL_DETAIL_GUID,
                                name = "DELIVERY_URL",
                                value = TEST_DELIVERY_URL
                            ),
                            CredentialResponse.CredentialDetail(
                                guid = TEST_CREDENTIAL_DETAIL_GUID,
                                name = "SECRET_KEY",
                                value = TEST_SECRET_KEY
                            )
                        )
                    )
                )
            )
        )
    }

    @Test
    fun `getCredential should return credential response`() {
        val credentialResponse = createMockCredentialResponse()
        val expectedQuery = """
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
        val expectedVariables = mapOf(
            "input" to mapOf(
                "guids" to TEST_CREDENTIAL_GUID,
                "isDecrypt" to true
            )
        )

        every {
            httpService.sendGraphqlRequestWithBypass(
                TEST_MS_FEDERATION_URL,
                expectedQuery,
                expectedVariables,
                CredentialResponse::class.java
            )
        } returns credentialResponse

        val result = credentialService.getCredential(UUID.fromString(TEST_CREDENTIAL_GUID))

        assertNotNull(result)
        assertEquals(TEST_CREDENTIAL_GUID, result?.secretMsCredentials?.data?.first()?.guid)
        assertEquals(TEST_CODE, result?.secretMsCredentials?.data?.first()?.code)
        Assertions.assertEquals(3, result?.secretMsCredentials?.data?.first()?.credentialDetail?.size)
    }

    @Test
    fun `getCredentialDetail should return list of credential details`() {
        val credentialResponse = createMockCredentialResponse()

        every {
            httpService.sendGraphqlRequestWithBypass(
                any(),
                any(),
                any(),
                CredentialResponse::class.java
            )
        } returns credentialResponse

        val result = credentialService.getCredentialDetail(UUID.fromString(TEST_CREDENTIAL_GUID))

        assertNotNull(result)
        Assertions.assertEquals(3, result?.size)
        assertTrue(result?.any { it.name == "INQUIRY_URL" && it.value == TEST_INQUIRY_URL } == true)
        assertTrue(result?.any { it.name == "DELIVERY_URL" && it.value == TEST_DELIVERY_URL } == true)
        assertTrue(result?.any { it.name == "SECRET_KEY" && it.value == TEST_SECRET_KEY } == true)
    }

    @Test
    fun `getChildCodeCredential should return ChildCodeCredential with correct values`() {
        val credentialResponse = createMockCredentialResponse()

        every {
            httpService.sendGraphqlRequestWithBypass(
                any(),
                any(),
                any(),
                CredentialResponse::class.java
            )
        } returns credentialResponse

        val result = credentialService.getChildCodeCredentials(UUID.fromString(TEST_CREDENTIAL_GUID))

        assertNotNull(result)
        assertEquals(TEST_INQUIRY_URL, result.inquiryUrl)
        assertEquals(TEST_DELIVERY_URL, result.deliveryUrl)
        assertEquals(TEST_SECRET_KEY, result.secretKey)
    }

    @Test
    fun `getChildCodeCredential should return empty values when credential details are empty`() {
        val emptyCredentialResponse = CredentialResponse(
            secretMsCredentials = CredentialResponse.SecretMsCredentials(
                data = listOf(
                    CredentialResponse.Credential(
                        guid = TEST_CREDENTIAL_GUID,
                        code = TEST_CODE,
                        credentialDetail = emptyList()
                    )
                )
            )
        )

        every {
            httpService.sendGraphqlRequestWithBypass(
                any(),
                any(),
                any(),
                CredentialResponse::class.java
            )
        } returns emptyCredentialResponse

        val result = credentialService.getChildCodeCredentials(UUID.fromString(TEST_CREDENTIAL_GUID))

        assertNotNull(result)
        assertEquals("", result.inquiryUrl)
        assertEquals("", result.deliveryUrl)
        assertEquals("", result.secretKey)
    }

    @Test
    fun `getCredential should propagate exceptions`() {
        val expectedException = RuntimeException("Test exception")

        every {
            httpService.sendGraphqlRequestWithBypass(
                any(),
                any(),
                any(),
                CredentialResponse::class.java
            )
        } throws expectedException

        val exception = assertThrows<RuntimeException> {
            credentialService.getCredential(UUID.fromString(TEST_CREDENTIAL_GUID))
        }

        assertEquals("Test exception", exception.message)
    }
}
