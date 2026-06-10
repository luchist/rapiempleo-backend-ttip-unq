package com.unq.rapiempleo.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter


@Configuration
class SecurityConfig (
    private val jwtTokenProvider: JwtTokenProvider
) {


    @Bean
    fun passwordEncoder(): PasswordEncoder {
        return BCryptPasswordEncoder()
    }

    @Bean
    fun filterChain(http: HttpSecurity): SecurityFilterChain {
        http
            .cors{}
            .csrf { it.disable() }
            .authorizeHttpRequests {
                it.requestMatchers("/login", "/ofertante/registrar", "/postulante/registrar", "/verificarEmpresa").permitAll()
                    .requestMatchers(HttpMethod.POST, "/postulante/*/cv").hasRole("POSTULANTE")
                    .requestMatchers(HttpMethod.POST, "/postulante/*/foto").hasRole("POSTULANTE")
                    .requestMatchers(HttpMethod.PATCH, "/postulante/*/cv/favorito").hasRole("POSTULANTE")
                    .requestMatchers(HttpMethod.POST, "/ofertante/*/foto").hasRole("OFERTANTE")
                    .requestMatchers(HttpMethod.POST, "/ofertante/*/oferta").hasRole("OFERTANTE")
                    .requestMatchers("/postulante/*/board/**").hasRole("POSTULANTE")
                    .anyRequest().authenticated()
            }
            .sessionManagement {
                it.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            }
            .addFilterBefore(
                JwtAuthenticationFilter(jwtTokenProvider),
            UsernamePasswordAuthenticationFilter::class.java
        )

        return http.build()
    }
}