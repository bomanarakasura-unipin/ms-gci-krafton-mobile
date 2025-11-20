package com.unipin.gci-parent-name.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Configuration

@Configuration
@ConfigurationProperties("app")
data class ApplicationProperties(
    var APP_ENV: String = "local",
    var BYPASS_PERMISSION_KEY: String = "wVSgzQKrtxCPk1XMkfxdz6gYTFNGZrVQ",
    var MS_FEDERATION_BASE_URL: String = "https://ms-gateway.up.unipin.dev/graphql",
) {
}
