package com.unq.rapiempleo

import org.springframework.boot.CommandLineRunner
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile

@Profile("!test")
@Configuration
class DataBaseInitializer(private val seeder: DataBaseSeeder) {

    @Bean
    fun initializeDatabase() = CommandLineRunner { seeder.seed() }
}
