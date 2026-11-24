package com.watertanker.api

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.scheduling.annotation.EnableAsync
import org.springframework.transaction.annotation.EnableTransactionManagement

@SpringBootApplication
@EnableJpaRepositories("com.watertanker.api.repository")
@EnableTransactionManagement
@EnableAsync
class WaterTankerApiApplication

fun main(args: Array<String>) {
    runApplication<WaterTankerApiApplication>(*args)
}