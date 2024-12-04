package org.example.appfirst.configuration.properties

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties("org")
class ApplicationProperties

@ConfigurationProperties("org.web-client.app-second")
data class AppSecondWebClientProperties(
    override val registrationId: String? = null,
    override val url: String,
    override val username: String? = null,
    override val password: String? = null,
    override val isDebug: Boolean = false,
    override val retry: Long = 0,
    override val responseTimeout: Long = 3
) : WebClientProperties(registrationId, url, username, password, isDebug, retry, responseTimeout)