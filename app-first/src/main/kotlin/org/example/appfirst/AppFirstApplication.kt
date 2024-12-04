package org.example.appfirst

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import reactor.core.publisher.Hooks

@SpringBootApplication
class AppFirstApplication

fun main(args: Array<String>) {
    runApplication<AppFirstApplication>(*args)
}
