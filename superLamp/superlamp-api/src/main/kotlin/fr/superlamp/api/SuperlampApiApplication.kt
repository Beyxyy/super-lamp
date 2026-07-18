package fr.superlamp.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication

@SpringBootApplication
class SuperlampApiApplication

fun main(args: Array<String>) {
    SpringApplication.run(SuperlampApiApplication::class.java, *args)
}
