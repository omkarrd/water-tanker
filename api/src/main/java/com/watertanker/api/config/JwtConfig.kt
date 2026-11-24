package com.watertanker.api.config

import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import jakarta.annotation.PostConstruct
import org.springframework.beans.factory.annotation.Value
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.stereotype.Component
import javax.crypto.SecretKey
import java.nio.charset.StandardCharsets
import java.util.Date

@Component
class JwtConfig(
    @Value("\${app.jwt.secret}") private val secret: String,
    @Value("\${app.jwt.expiration}") private val expiration: Long,
    @Value("\${app.jwt.refresh-expiration}") private val refreshExpiration: Long
) {

    private var key: SecretKey? = null

    @PostConstruct
    fun init() {
        key = Keys.hmacShaKeyFor(secret.toByteArray(StandardCharsets.UTF_8))
    }

    fun generateToken(userDetails: UserDetails): String {
        val now = Date()
        val expiry = Date(now.time + expiration)
        return Jwts.builder()
            .subject(userDetails.username)
            .issuedAt(now)
            .expiration(expiry)
            .claim("authorities", userDetails.authorities.map { it.authority })
            .signWith(key!!)
            .compact()
    }

    fun generateRefreshToken(userDetails: UserDetails): String {
        val now = Date()
        val expiry = Date(now.time + refreshExpiration)
        return Jwts.builder()
            .subject(userDetails.username)
            .issuedAt(now)
            .expiration(expiry)
            .signWith(key!!)
            .compact()
    }

    fun extractUsername(token: String): String {
        return extractAllClaims(token).subject
    }

    fun extractAuthorities(token: String): List<String> {
        return extractAllClaims(token).get("authorities", List::class.java) ?: emptyList()
    }

    fun isTokenValid(token: String, userDetails: UserDetails): Boolean {
        val username = extractUsername(token)
        return username == userDetails.username && !isTokenExpired(token)
    }

    fun isTokenExpired(token: String): Boolean {
        return extractAllClaims(token).expiration.before(Date())
    }

    private fun extractAllClaims(token: String): Claims {
        return Jwts.parser()
            .verifyWith(key!!)
            .build()
            .parseSignedClaims(token)
            .payload
    }
}