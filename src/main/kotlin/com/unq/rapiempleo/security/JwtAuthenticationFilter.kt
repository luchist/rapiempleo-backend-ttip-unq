package com.unq.rapiempleo.security

import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.web.filter.OncePerRequestFilter

class JwtAuthenticationFilter (
    private val jwtTokenProvider: JwtTokenProvider // you should already have this
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {

        val header = request.getHeader("Authorization")

        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = header.substring(7)

        //val username = jwtTokenProvider.extractUsername(token)

        val username = try {
            jwtTokenProvider.extractUsername(token)
        } catch (e: Exception) {
            null
        }

        if (username != null && SecurityContextHolder.getContext().authentication == null) {

            if (jwtTokenProvider.isTokenValid(token)) {

                val auth = UsernamePasswordAuthenticationToken(
                    username,
                    null,
                    emptyList() // roles later if you want
                )

                SecurityContextHolder.getContext().authentication = auth
            }
        }

        filterChain.doFilter(request, response)
    }
}