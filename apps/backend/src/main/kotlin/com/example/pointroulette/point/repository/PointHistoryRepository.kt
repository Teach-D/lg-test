package com.example.pointroulette.point.repository

import com.example.pointroulette.point.entity.PointHistory
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface PointHistoryRepository : JpaRepository<PointHistory, Long> {

  fun findByUserIdOrderByCreatedAtDesc(userId: Long, pageable: Pageable): Page<PointHistory>
}
