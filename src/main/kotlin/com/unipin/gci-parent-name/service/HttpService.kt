package com.unipin.gci-parent-name.service

import com.fasterxml.jackson.core.type.TypeReference
import com.fasterxml.jackson.databind.JsonNode
import com.fasterxml.jackson.databind.ObjectMapper
import com.unipin.gci-parent-name.exception.GenericException
import com.unipin.gci-parent-name.model.response.GraphqlResponse
import com.unipin.gci-parent-name.configuration.properties.ApplicationProperties
import com.unipin.gci-parent-name.configuration.variable.ErrorCode
import com.unipin.gci-parent-name.util.CommonUtil
import logging.lib.util.JsonLogFactory
import org.slf4j.Logger
import org.springframework.http.*
import org.springframework.stereotype.Service
import org.springframework.web.client.HttpStatusCodeException
import org.springframework.web.client.RestTemplate


@Service
class HttpService(
    private val restTemplate: RestTemplate,
    private val objectMapper: ObjectMapper,
    private val applicationProperties: ApplicationProperties,
) {

    private val log: Logger = JsonLogFactory.getLogger(HttpService::class.java)

    fun <T> sendHttpPost(
        url: String,
        body: Any,
        responseType: Class<T>,
        headers: HttpHeaders? = null,
    ): T {
        // Set HTTP Header
        val header = HttpHeaders()
        header.contentType = MediaType.APPLICATION_JSON
        headers?.let { header.putAll(it) }

        // Send request
        val request = HttpEntity(body, header)
        log.info("Send http post request to $url, body: ${objectMapper.writeValueAsString(body)}")

        val response = try {
            restTemplate.exchange(
                url,
                HttpMethod.POST,
                request,
                String::class.java
            )
        } catch (e: HttpStatusCodeException) {
            // GP can return other than 200 status code, need to handle it so that we can get the response body
            // restTemplate.exchange will throw exception if status code is not 200
            ResponseEntity.status(e.statusCode).headers(e.responseHeaders)
                .body<String>(e.responseBodyAsString)
        }
        log.info("Finish send http post request to $url, response: ${objectMapper.writeValueAsString(response.body)}")

        return objectMapper.readValue(response.body, responseType)
    }

    fun <T> sendHttpGet(
        url: String,
        responseType: Class<T>,
        headers: HttpHeaders? = null,
    ): T {
        // Set header
        val header = HttpHeaders()
        header.contentType = MediaType.APPLICATION_JSON
        headers?.let { header.putAll(it) }

        // Create an HttpEntity with the headers
        val entity = HttpEntity<String>(header)
        log.info("Send http get request to $url")

        // Send request
        val response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            entity,
            String::class.java
        )
        log.info("Finish send http get request to $url, response: ${objectMapper.writeValueAsString(response.body)}")

        return objectMapper.readValue(response.body, responseType)
    }

    fun <T> sendGraphqlRequestWithBypass(
        url: String,
        query: String,
        variables: Map<String, Any>,
        responseType: Class<T>,
        hdr: HttpHeaders? = null,
    ): T? {
        // Set query
        val payload: Map<String, Any> = mapOf(
            "query" to query,
            "variables" to variables
        )

        // Set header
        val headers = CommonUtil.getByPassHeader(applicationProperties.BYPASS_PERMISSION_KEY)
        headers.contentType = MediaType.APPLICATION_JSON
        hdr?.let { headers.putAll(it) }

        // Send request
        val request = HttpEntity(payload, headers)
        log.info("Send graphql request to $url, body: ${objectMapper.writeValueAsString(payload)}")
        val response = restTemplate.exchange(
            url,
            HttpMethod.POST,
            request,
            String::class.java
        )
        val typeReference = object : TypeReference<GraphqlResponse<T>>() {}
        val graphqlResponse =
            response.body.let { objectMapper.readValue(it, typeReference) } ?: throw GenericException(
                "Graphql Request",
                ErrorCode.SYSTEM_ERROR, "Invalid response"
            )
        log.info("Finish send graphql request to $url, response: ${objectMapper.writeValueAsString(graphqlResponse)}")
        if (graphqlResponse.errors.isNotEmpty()) {
            throw GenericException("Graphql Request", ErrorCode.SYSTEM_ERROR, graphqlResponse.errors.joinToString())
        }
        return objectMapper.convertValue(graphqlResponse.data, responseType)
    }

}
