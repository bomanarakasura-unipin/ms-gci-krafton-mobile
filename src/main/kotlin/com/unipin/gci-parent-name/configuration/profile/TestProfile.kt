package com.unipin.gci-parent-name.configuration.profile

import com.unipin.gci-parent-name.configuration.properties.ApplicationProperties
import com.unipin.gci-parent-name.configuration.variable.ApplicationConstant
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Configuration
@Profile("test")
class TestProfile(val applicationProperties: ApplicationProperties) {

    @Bean(ApplicationConstant.APP_CONF)
    fun initProperties(): ApplicationProperties {
        return this.applicationProperties
    }
}
