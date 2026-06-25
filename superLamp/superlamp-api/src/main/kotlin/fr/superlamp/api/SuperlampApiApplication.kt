package fr.superlamp.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity

@SpringBootApplication
@EnableMethodSecurity
class SuperlampApiApplication

fun main(args: Array<String>) {
    runApplication<SuperlampApiApplication>(*args)
}
