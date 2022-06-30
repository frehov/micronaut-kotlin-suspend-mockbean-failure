package com.example.micronaut.controllers

import io.micronaut.health.HealthStatus
import io.micronaut.http.MediaType.APPLICATION_JSON
import io.micronaut.http.MediaType.APPLICATION_JSON_STREAM
import io.micronaut.http.annotation.Controller
import io.micronaut.http.annotation.Get
import io.micronaut.http.annotation.Produces
import io.micronaut.security.annotation.Secured
import io.micronaut.security.rules.SecurityRule
import kotlinx.coroutines.reactor.flux
import com.example.micronaut.services.StatusService
import reactor.core.publisher.Flux

@Secured(SecurityRule.IS_ANONYMOUS)
@Controller("/connection-status")
class StatusController(
    private val connections: StatusService
) {

    @Get
    @Produces(APPLICATION_JSON, APPLICATION_JSON_STREAM)
    fun getAllConnections(): Flux<HealthStatus> {
        return flux {
            for (status in connections.getConnectionStatus()) {
                send(status)
            }
        }
    }
}