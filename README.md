
# Annotation processing @Mockbean issue with  suspend function
In short, using `@MockBean` to "replace" an implementation with a mock fails in test, if there are kotlin suspend functions present.
This repository is intentionally made to not compile as to showcase the problem.

The expected compilation failure is `Cannot apply AOP advice to final class. Class must be made non-final to support proxying: com.example.micronaut.services.StatusService`

## Workarounds

### 1. @Replaces and @Singleton instead of @Mockbean
The following in `StatusControllerTest` works by replacing the `@Mockbean`-annotation with the annotations it is an alias for.

Orignal code:
```kotlin
@MockBean(StatusService::class)
fun connectionStatusService(): StatusService {
    return mockk()
}
```

Replacement code:
```kotlin
@Singleton
@Replaces(StatusService::class)
fun connectionStatusService(): StatusService {
    return mockk()
}
```

### 2. Adding @Around on the Service implementation
Adding the `@Around`-annontation to the service implementation will work.

Orignal code:
```kotlin
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
```

Replacement code:
```kotlin
@Around
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
```

### 3. Adding `open` on the Service class and methods
Adding the `open`-keyword to the service class and methods will work.

Orignal code:
```kotlin
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
```

Replacement code:
```kotlin
@Singleton
open class StatusService {

    open suspend fun getConnectionStatus(): List<HealthStatus> {
        return coroutineScope {
            listOf(
                HealthStatus.UNKNOWN
            )
        }
    }
}
```

However, if you omit `open` on the method level, actual service is constructed and invoked. As such the `coVerify` call in the test will fail, and the body of the response will contain `[ "UNKNOWN" ]` as the default response. 

## System information
OS: Windows 11 21H2 (22000.739)  
Java: Adoptium/temurin 17
