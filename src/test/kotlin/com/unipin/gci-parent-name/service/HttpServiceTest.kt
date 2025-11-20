package com.unipin.gci-parent-name.service

import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.KotlinModule
import com.unipin.gci-parent-name.configuration.properties.ApplicationProperties
import com.unipin.gci-parent-name.model.response.GraphqlResponse
import com.unipin.gci-parent-name.util.CommonUtil
import io.mockk.MockKAnnotations
import io.mockk.every
import io.mockk.impl.annotations.MockK
import io.mockk.junit5.MockKExtension
import io.mockk.mockkObject
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.assertThrows
import org.junit.jupiter.api.extension.ExtendWith
import org.springframework.http.*
import org.springframework.web.client.RestTemplate
import org.junit.jupiter.api.Assertions.*


@ExtendWith(MockKExtension::class)
class HttpServiceTest {
    @MockK
    private lateinit var restTemplate: RestTemplate


    @MockK
    private lateinit var applicationProperties: ApplicationProperties

    private lateinit var httpService: HttpService
    private val objectMapper = ObjectMapper().registerModule(KotlinModule.Builder().build())

    @BeforeEach
    fun setUp() {
        MockKAnnotations.init(this)
        mockkObject(CommonUtil)
        httpService = HttpService(
            restTemplate,
            objectMapper,
            applicationProperties
        )
    }

    data class TestResponse(
        val message: String,
    )

    @Test
    fun `sendHttpPost should successfully send POST request and return response`() {
        // given
        val url = "http://test.com/api"
        val requestBody = mapOf("key" to "value")
        val expectedResponse = TestResponse("success")
        val responseEntity = ResponseEntity(
            objectMapper.writeValueAsString(expectedResponse),
            HttpStatus.OK
        )

        every {
            restTemplate.exchange(
                url,
                HttpMethod.POST,
                any(),
                String::class.java
            )
        } returns responseEntity

        // when
        val result = httpService.sendHttpPost(url, requestBody, TestResponse::class.java)

        // then
        assertNotNull(result)
        assertEquals("success", result.message)
    }

    @Test
    fun `sendHttpPost should handle custom headers`() {
        // given
        val url = "http://test.com/api"
        val requestBody = mapOf("key" to "value")
        val customHeaders = HttpHeaders().apply {
            add("Custom-Header", "test-value")
        }
        val expectedResponse = TestResponse("success")
        val responseEntity = ResponseEntity(
            objectMapper.writeValueAsString(expectedResponse),
            HttpStatus.OK
        )

        every {
            restTemplate.exchange(
                url,
                HttpMethod.POST,
                match {
                    it.headers.containsKey("Custom-Header") &&
                            it.headers.getFirst("Custom-Header") == "test-value"
                },
                String::class.java
            )
        } returns responseEntity

        // when
        val result = httpService.sendHttpPost(url, requestBody, TestResponse::class.java, customHeaders)

        // then
        assertNotNull(result)
        assertEquals("success", result.message)
    }

    @Test
    fun `sendHttpGet should successfully send GET request and return response`() {
        // given
        val url = "http://test.com/api"
        val expectedResponse = TestResponse("success")
        val responseEntity = ResponseEntity(
            objectMapper.writeValueAsString(expectedResponse),
            HttpStatus.OK
        )

        every {
            restTemplate.exchange(
                url,
                HttpMethod.GET,
                any(),
                String::class.java
            )
        } returns responseEntity

        // when
        val result = httpService.sendHttpGet(url = url, responseType = TestResponse::class.java)

        // then
        assertNotNull(result)
        assertEquals("success", result.message)
    }

    @Test
    fun `sendHttpGet should handle custom headers`() {
        // given
        val url = "http://test.com/api"
        val customHeaders = HttpHeaders().apply {
            add("Custom-Header", "test-value")
        }
        val expectedResponse = TestResponse("success")
        val responseEntity = ResponseEntity(
            objectMapper.writeValueAsString(expectedResponse),
            HttpStatus.OK
        )

        every {
            restTemplate.exchange(
                url,
                HttpMethod.GET,
                match {
                    it.headers.containsKey("Custom-Header") &&
                            it.headers.getFirst("Custom-Header") == "test-value"
                },
                String::class.java
            )
        } returns responseEntity

        // when
        val result = httpService.sendHttpGet(url, TestResponse::class.java, customHeaders)

        // then
        assertNotNull(result)
        assertEquals("success", result.message)
    }

    @Test
    fun `sendGraphqlRequestWithBypass should successfully send GraphQL request and return response`() {
        // given
        val url = "http://test.com/graphql"
        val query = "query { test }"
        val variables = mapOf("key" to "value")
        val bypassHeaders = HttpHeaders()
        val expectedResponse = TestResponse("success")
        val graphqlResponse = GraphqlResponse(
            data = expectedResponse,
            errors = emptyList()
        )
        val responseEntity = ResponseEntity(
            objectMapper.writeValueAsString(graphqlResponse),
            HttpStatus.OK
        )

        every { applicationProperties.BYPASS_PERMISSION_KEY } returns "test-key"
        every { CommonUtil.getByPassHeader(any()) } returns bypassHeaders
        every {
            restTemplate.exchange(
                url,
                HttpMethod.POST,
                any(),
                String::class.java
            )
        } returns responseEntity

        // when
        val result = httpService.sendGraphqlRequestWithBypass(
            url,
            query,
            variables,
            TestResponse::class.java
        )

        // then
        assertNotNull(result)
        assertEquals("success", result?.message)
    }

    @Test
    fun `sendGraphqlRequestWithBypass should handle GraphQL errors`() {
        // given
        val url = "http://test.com/graphql"
        val query = "query { test }"
        val variables = mapOf("key" to "value")
        val bypassHeaders = HttpHeaders()
        val graphqlResponse = GraphqlResponse<TestResponse>(
            data = null,
            errors = listOf("GraphQL Error")
        )
        val responseEntity = ResponseEntity(
            objectMapper.writeValueAsString(graphqlResponse),
            HttpStatus.OK
        )

        every { applicationProperties.BYPASS_PERMISSION_KEY } returns "test-key"
        every { CommonUtil.getByPassHeader(any()) } returns bypassHeaders
        every {
            restTemplate.exchange(
                url,
                HttpMethod.POST,
                any(),
                String::class.java
            )
        } returns responseEntity

        // then
        val exception = assertThrows<Exception> {
            httpService.sendGraphqlRequestWithBypass(
                url,
                query,
                variables,
                TestResponse::class.java
            )
        }
        assertEquals("GraphQL Error", exception.message)
    }

    @Test
    fun `sendGraphqlRequestWithBypass should handle custom headers`() {
        // given
        val url = "http://test.com/graphql"
        val query = "query { test }"
        val variables = mapOf("key" to "value")
        val bypassHeaders = HttpHeaders()
        val customHeaders = HttpHeaders().apply {
            add("Custom-Header", "test-value")
        }
        val expectedResponse = TestResponse("success")
        val graphqlResponse = GraphqlResponse(
            data = expectedResponse,
            errors = emptyList()
        )
        val responseEntity = ResponseEntity(
            objectMapper.writeValueAsString(graphqlResponse),
            HttpStatus.OK
        )

        every { applicationProperties.BYPASS_PERMISSION_KEY } returns "test-key"
        every { CommonUtil.getByPassHeader(any()) } returns bypassHeaders
        every {
            restTemplate.exchange(
                url,
                HttpMethod.POST,
                match {
                    it.headers.containsKey("Custom-Header") &&
                            it.headers.getFirst("Custom-Header") == "test-value"
                },
                String::class.java
            )
        } returns responseEntity

        // when
        val result = httpService.sendGraphqlRequestWithBypass(
            url,
            query,
            variables,
            TestResponse::class.java,
            customHeaders
        )

        // then
        assertNotNull(result)
        assertEquals("success", result?.message)
    }

}
