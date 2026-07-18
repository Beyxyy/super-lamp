package fr.superlamp.api

import org.springframework.boot.SpringApplication
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.persistence.autoconfigure.EntityScan
import org.springframework.context.annotation.ComponentScan
import org.springframework.data.jpa.repository.config.EnableJpaRepositories

@SpringBootApplication
@ComponentScan("fr.superlamp.api", "fr.superlamp.data")
@EntityScan("fr.superlamp.data.entity")
@EnableJpaRepositories("fr.superlamp.data.repository")
class SuperlampApiApplication

fun main(args: Array<String>) {
    SpringApplication.run(SuperlampApiApplication::class.java, *args)
}
