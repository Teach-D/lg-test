package com.example.pointroulette.dashboard.service

import com.example.pointroulette.dashboard.dto.DashboardSummaryResponse
import com.example.pointroulette.order.repository.OrderRepository
import com.example.pointroulette.product.repository.ProductRepository
import com.example.pointroulette.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime

@Service
@Transactional(readOnly = true)
class DashboardService(
  private val orderRepository: OrderRepository,
  private val userRepository: UserRepository,
  private val productRepository: ProductRepository,
) {

  /** 대시보드 요약 데이터 조회 */
  fun getSummary(): DashboardSummaryResponse {
    val todayStart = LocalDate.now().atTime(LocalTime.MIN)

    val todayRevenue = orderRepository.sumPointCostAfter(todayStart)
    val todayOrderCount = orderRepository.countByCreatedAtAfter(todayStart)
    val totalUsers = userRepository.count()
    val totalProducts = productRepository.count()

    val orderStatusCounts = orderRepository.countGroupByStatus()
      .associate { row -> (row[0] as Enum<*>).name to (row[1] as Long) }

    return DashboardSummaryResponse(
      todayRevenue = todayRevenue,
      todayOrderCount = todayOrderCount,
      totalUsers = totalUsers,
      totalProducts = totalProducts,
      orderStatusCounts = orderStatusCounts,
    )
  }
}