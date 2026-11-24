package com.watertanker.api.entity

import jakarta.persistence.*
import lombok.*
import org.hibernate.annotations.CreationTimestamp
import org.hibernate.annotations.UpdateTimestamp
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails
import java.time.Instant
import java.util.Collection
import java.util.stream.Collectors

@Entity
@Table(name = "users", indexes = [
    Index(name = "idx_user_email", columnList = "email", unique = true),
    Index(name = "idx_user_username", columnList = "username", unique = true)
])
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EntityListeners(AuditingEntityListener::class)
class User(
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long? = null,

    @Column(name = "username", nullable = false, unique = true, length = 50)
    val username: String,

    @Column(name = "email", nullable = false, unique = true, length = 100)
    val email: String,

    @Column(name = "password_hash", nullable = false, length = 255)
    val passwordHash: String,

    @Column(name = "full_name", length = 100)
    val fullName: String? = null,

    @Column(name = "phone", length = 20)
    val phone: String? = null,

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "user_roles", joinColumns = [JoinColumn(name = "user_id")])
    @Column(name = "role", length = 50)
    val roles: List<String> = listOf("ROLE_USER"),

    @Column(name = "enabled", nullable = false)
    val enabled: Boolean = true,

    @Column(name = "account_non_expired", nullable = false)
    val accountNonExpired: Boolean = true,

    @Column(name = "account_non_locked", nullable = false)
    val accountNonLocked: Boolean = true,

    @Column(name = "credentials_non_expired", nullable = false)
    val credentialsNonExpired: Boolean = true,

    @CreationTimestamp
    @Column(name = "created_at", nullable = false, updatable = false)
    val createdAt: Instant? = null,

    @UpdateTimestamp
    @Column(name = "updated_at", nullable = false)
    val updatedAt: Instant? = null
) : UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority> {
        return roles.stream().map { SimpleGrantedAuthority(it) }.collect(Collectors.toList())
    }

    override fun getPassword(): String = passwordHash

    override fun getUsername(): String = username

    override fun isAccountNonExpired(): Boolean = accountNonExpired

    override fun isAccountNonLocked(): Boolean = accountNonLocked

    override fun isCredentialsNonExpired(): Boolean = credentialsNonExpired

    override fun isEnabled(): Boolean = enabled
}