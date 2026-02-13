package com.example.pointroulette.point.repository

import com.example.pointroulette.point.entity.PointHistory
import com.example.pointroulette.point.entity.PointType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PointHistoryRepository : JpaRepository<PointHistory, Long> {

  fun findByUserIdOrderByCreatedAtDesc(userId: Long, pageable: Pageable): Page<PointHistory>

  @Query(
    "SELECT COALESCE(SUM(p.amount), 0) FROM PointHistory p " +
      "WHERE p.userId = :userId AND p.amount > 0 " +
      "AND p.expiresAt IS NOT NULL AND p.expiresAt BETWEEN :now AND :threshold"
  )
  fun sumExpiringPoints(userId: Long, now: LocalDateTime, threshold: LocalDateTime): Int

  fun findByExpiresAtBeforeAndAmountGreaterThanAndTypeNot(
    expiresAt: LocalDateTime,
    amount: Int,
    type: PointType,
  ): List<PointHistory>
}
