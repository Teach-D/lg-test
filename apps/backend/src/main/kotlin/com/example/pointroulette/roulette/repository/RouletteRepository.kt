package com.example.pointroulette.roulette.repository

import com.example.pointroulette.roulette.entity.RouletteSegment
import com.example.pointroulette.roulette.entity.SpinHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface RouletteSegmentRepository : JpaRepository<RouletteSegment, Long> {
  fun findAllByOrderByDisplayOrderAsc(): List<RouletteSegment>
}

interface SpinHistoryRepository : JpaRepository<SpinHistory, Long> {
  fun existsByUserIdAndCancelledFalseAndCreatedAtBetween(
    userId: Long,
    start: LocalDateTime,
    end: LocalDateTime,
  ): Boolean

  fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<SpinHistory>

  fun countByCancelledFalseAndCreatedAtBetween(
    start: LocalDateTime,
    end: LocalDateTime,
  ): Long

  @Query(
    "SELECT COALESCE(SUM(s.rewardPoint), 0) FROM SpinHistory s " +
      "WHERE s.cancelled = false AND s.createdAt BETWEEN :start AND :end",
  )
  fun sumRewardPointByCreatedAtBetween(start: LocalDateTime, end: LocalDateTime): Long
}