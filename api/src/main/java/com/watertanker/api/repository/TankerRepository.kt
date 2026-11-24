package com.watertanker.api.repository

import com.watertanker.api.entity.Tanker
import com.watertanker.shared.enums.TankerStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository

@Repository
interface TankerRepository : JpaRepository<Tanker, Long> {

    fun findByRegistrationNumber(registrationNumber: String): Tanker?

    fun findByStatus(status: TankerStatus): List<Tanker>

    fun findByStatus(status: TankerStatus, pageable: Pageable): Page<Tanker>

    @Query("SELECT t FROM Tanker t WHERE t.status = :status AND t.currentLatitude IS NOT NULL AND t.currentLongitude IS NOT NULL")
    fun findAvailableWithLocation(@Param("status") status: TankerStatus): List<Tanker>

    fun existsByRegistrationNumber(registrationNumber: String): Boolean
}