package org.example.appsecond.configuration

import org.example.common.configuration.RabbitConfiguration
import org.springframework.boot.context.properties.ConfigurationPropertiesScan
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Import

@Configuration
@ConfigurationPropertiesScan
@Import(value = [RabbitConfiguration::class])
class ApplicationConfiguration