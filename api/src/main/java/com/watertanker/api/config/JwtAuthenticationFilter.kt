package com.watertanker.api.config

import com.watertanker.api.entity.User
import jakarta.servlet.FilterChain
import jakarta.servlet.ServletException
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.lang.NonNull
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import java.io.IOException
import java.util.stream.Collectors

@Component
class JwtAuthenticationFilter(
    private val jwtConfig: JwtConfig,
    private val userRepository: UserRepository
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        @NonNull request: HttpServletRequest,
        @NonNull response: HttpServletResponse,
        @NonNull filterChain: FilterChain
    ) {
        val authHeader = request.getHeader("Authorization")
        if (authHeader == null || !authHeader.startsWith("Bearer ")) {
            filterChain.doFilter(request, response)
            return
        }

        val token = authHeader.substring(7)
        try {
            val username = jwtConfig.extractUsername(token)
            if (username != null && SecurityContextHolder.getContext().authentication == null) {
                val user = userRepository.findByUsername(username) ?: return
                if (jwtConfig.isTokenValid(token, user)) {
                    val authorities = jwtConfig.extractAuthorities(token)
                        .stream().map { SimpleGrantedAuthority(it) }
                        .collect(Collectors.toList())
                    val authToken = UsernamePasswordAuthenticationToken(
                        user, null, authorities
                    )
                    authToken.details = WebAuthenticationDetailsSource().buildDetails(request)
                    SecurityContextHolder.getContext().authentication = authToken
                }
            }
        } catch (e: Exception) {
            // Invalid token - continue without authentication
        }
        filterChain.doFilter(request, response)
    }
}