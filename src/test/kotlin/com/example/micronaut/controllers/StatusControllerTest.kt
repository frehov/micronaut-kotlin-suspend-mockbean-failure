package com.example.micronaut.controllers

import io.micronaut.core.type.Argument
import io.micronaut.health.HealthStatus.DOWN
import io.micronaut.health.HealthStatus.UP
import io.micronaut.http.HttpRequest
import io.micronaut.http.HttpResponse
import io.micronaut.http.MediaType
import io.micronaut.http.client.annotation.Client
import io.micronaut.reactor.http.client.ReactorHttpClient
import io.micronaut.security.authentication.Authentication
import io.micronaut.security.token.jwt.validator.JwtTokenValidator
import io.micronaut.security.token.validator.TokenValidator
import io.micronaut.test.annotation.MockBean
import io.micronaut.test.extensions.junit5.annotation.MicronautTest
import com.example.micronaut.services.StatusService
import io.mockk.*
import org.assertj.core.api.Assertions.assertThat
import org.junit.jupiter.api.Test
import reactor.core.publisher.Flux
import jakarta.inject.Inject
import java.util.UUID

@MicronautTest
class StatusControllerTest {

    @MockBean(JwtTokenValidator::class)
    fun tokenvalidator(): JwtTokenValidator {
        return mockk()
    }

    @MockBean(StatusService::class)
    fun connectionStatusService(): StatusService {
        return mockk()
    }

    @Inject
    private lateinit var tokenValidator: TokenValidator

    @Inject
    private lateinit var connections: StatusService

    @Inject
    @field:Client("/connection-status")
    lateinit var client: ReactorHttpClient

    @Test
    fun `if no connections are available, should return 404 or empty list`() {
        every {
            tokenValidator.validateToken(or(ofType(String::class), isNull()), ofType(HttpRequest::class))
        } returns Flux.just(Authentication.build("user", emptyList(), emptyMap()))
        coEvery { connections.getConnectionStatus() } returns listOf(UP, DOWN)

        val result: HttpResponse<List<String>> = client.toBlocking().exchange(
            HttpRequest.GET<Any>("")
                .accept(MediaType.APPLICATION_JSON_TYPE)
                .bearerAuth(UUID.randomUUID().toString()),
            Argument.listOf(String::class.java)
        )

        coVerify(exactly = 1) { connections.getConnectionStatus() }

        assertThat(result.body())
            .isNotNull
            .containsExactlyInAnyOrder(UP.name, DOWN.name)
    }
}