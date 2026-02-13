package com.example.pointroulette.order.repository

import com.example.pointroulette.order.entity.Order
import com.example.pointroulette.order.entity.OrderStatus
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import java.time.LocalDateTime

interface OrderRepository : JpaRepository<Order, Long> {
  fun findByUserIdOrderByCreatedAtDesc(userId: Long, pageable: Pageable): Page<Order>
  fun findByUserIdAndStatusOrderByCreatedAtDesc(
    userId: Long,
    status: OrderStatus,
    pageable: Pageable,
  ): Page<Order>
  fun countByUserId(userId: Long): Long
  fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Order>
  fun findByStatusOrderByCreatedAtDesc(status: OrderStatus, pageable: Pageable): Page<Order>
  fun countByStatus(status: OrderStatus): Long
  fun countByCreatedAtAfter(after: LocalDateTime): Long

  @Query("SELECT COALESCE(SUM(o.pointCost), 0) FROM Order o WHERE o.createdAt >= :after")
  fun sumPointCostAfter(after: LocalDateTime): Long

  @Query(
    "SELECT o.status, COUNT(o) FROM Order o GROUP BY o.status",
  )
  fun countGroupByStatus(): List<Array<Any>>
}