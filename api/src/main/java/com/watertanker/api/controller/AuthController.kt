package com.watertanker.api.controller

import com.watertanker.api.config.JwtConfig
import com.watertanker.api.entity.User
import com.watertanker.api.repository.UserRepository
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import jakarta.validation.constraints.Email
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import org.springframework.http.ResponseEntity
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.Authentication
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.web.bind.annotation.*
import java.util.HashMap

@RestController
@RequestMapping("/api/auth")
@Tag(name = "Authentication", description = "User authentication and authorization")
class AuthController(
    private val authenticationManager: AuthenticationManager,
    private val userRepository: UserRepository,
    private val passwordEncoder: PasswordEncoder,
    private val jwtConfig: JwtConfig
) {

    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    fun register(@Valid @RequestBody request: RegisterRequest): ResponseEntity<AuthResponse> {
        if (userRepository.existsByUsername(request.username)) {
            return ResponseEntity.badRequest().body(AuthResponse(null, null, "Username already exists"))
        }
        if (userRepository.existsByEmail(request.email)) {
            return ResponseEntity.badRequest().body(AuthResponse(null, null, "Email already exists"))
        }

        val user = User(
            username = request.username,
            email = request.email,
            passwordHash = passwordEncoder.encode(request.password),
            fullName = request.fullName,
            phone = request.phone,
            roles = listOf("ROLE_USER")
        )

        val saved = userRepository.save(user)
        val accessToken = jwtConfig.generateToken(saved)
        val refreshToken = jwtConfig.generateRefreshToken(saved)

        return ResponseEntity.ok(AuthResponse(accessToken, refreshToken, "User registered successfully"))
    }

    @PostMapping("/login")
    @Operation(summary = "Authenticate user and return tokens")
    fun login(@Valid @RequestBody request: LoginRequest): ResponseEntity<AuthResponse> {
        val authToken = UsernamePasswordAuthenticationToken(request.username, request.password)
        val authentication: Authentication = authenticationManager.authenticate(authToken)
        val userDetails = authentication.principal as UserDetails

        val accessToken = jwtConfig.generateToken(userDetails)
        val refreshToken = jwtConfig.generateRefreshToken(userDetails)

        return ResponseEntity.ok(AuthResponse(accessToken, refreshToken, "Login successful"))
    }

    @PostMapping("/refresh")
    @Operation(summary = "Refresh access token")
    fun refresh(@RequestBody request: RefreshRequest): ResponseEntity<AuthResponse> {
        try {
            val username = jwtConfig.extractUsername(request.refreshToken)
            val user = userRepository.findByUsername(username)
                ?: return ResponseEntity.badRequest().body(AuthResponse(null, null, "User not found"))

            if (!jwtConfig.isTokenValid(request.refreshToken, user)) {
                return ResponseEntity.badRequest().body(AuthResponse(null, null, "Invalid refresh token"))
            }

            val accessToken = jwtConfig.generateToken(user)
            val newRefreshToken = jwtConfig.generateRefreshToken(user)

            return ResponseEntity.ok(AuthResponse(accessToken, newRefreshToken, "Token refreshed"))
        } catch (e: Exception) {
            return ResponseEntity.badRequest().body(AuthResponse(null, null, "Invalid refresh token"))
        }
    }

    @GetMapping("/me")
    @Operation(summary = "Get current user info")
    fun me(authentication: Authentication): ResponseEntity<Map<String, Any>> {
        val userDetails = authentication.principal as UserDetails
        val user = userRepository.findByUsername(userDetails.username)!!
        val map = HashMap<String, Any>()
        map["id"] = user.id
        map["username"] = user.username
        map["email"] = user.email
        map["fullName"] = user.fullName
        map["phone"] = user.phone
        map["roles"] = user.roles
        return ResponseEntity.ok(map)
    }
}

data class RegisterRequest(
    @field:NotBlank @field:Size(min = 3, max = 50)
    val username: String,

    @field:Email @field:NotBlank
    val email: String,

    @field:NotBlank @field:Size(min = 8, max = 100)
    val password: String,

    @field:Size(max = 100)
    val fullName: String? = null,

    @field:Size(max = 20)
    val phone: String? = null
)

data class LoginRequest(
    @field:NotBlank
    val username: String,

    @field:NotBlank
    val password: String
)

data class RefreshRequest(
    @field:NotBlank
    val refreshToken: String
)

data class AuthResponse(
    val accessToken: String?,
    val refreshToken: String?,
    val message: String
)