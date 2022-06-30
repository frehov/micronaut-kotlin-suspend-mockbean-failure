package com.example.micronaut.services

import io.micronaut.health.HealthStatus
import kotlinx.coroutines.coroutineScope
import jakarta.inject.Singleton

@Singleton
class StatusService {

    suspend fun getConnectionStatus(): List<HealthStatus> {
        return coroutineScope {
            listOf(
                HealthStatus.UNKNOWN
            )
        }
    }
}