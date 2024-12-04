package org.example.appfirst.configuration.properties

abstract class WebClientProperties(
    open val registrationId: String?,
    open val url: String,
    open val username: String?,
    open val password: String?,
    open val isDebug: Boolean,
    open val retry: Long,
    open val responseTimeout: Long
)