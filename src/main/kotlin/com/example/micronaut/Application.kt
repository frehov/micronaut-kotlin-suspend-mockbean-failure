package com.example.micronaut

import io.micronaut.runtime.Micronaut

object Application {

    @JvmStatic
    fun main(vararg args: String) {
        Micronaut.build(*args)
            .packages("com.example.micronaut")
            .mainClass(Application.javaClass)
            .start()
    }
}