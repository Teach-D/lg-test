package com.example.pointroulette.dashboard.service

import com.example.pointroulette.budget.entity.PeriodType
import com.example.pointroulette.budget.repository.BudgetRepository
import com.example.pointroulette.dashboard.dto.DashboardSummaryResponse
import com.example.pointroulette.order.repository.OrderRepository
import com.example.pointroulette.product.repository.ProductRepository
import com.example.pointroulette.roulette.repository.SpinHistoryRepository
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
  private val budgetRepository: BudgetRepository,
  private val spinHistoryRepository: SpinHistoryRepository,
) {

  /** 대시보드 요약 데이터 조회 */
  fun getSummary(): DashboardSummaryResponse {
    val today = LocalDate.now()
    val todayStart = today.atTime(LocalTime.MIN)
    val todayEnd = today.atTime(LocalTime.MAX)

    val todayRevenue = orderRepository.sumPointCostAfter(todayStart)
    val todayOrderCount = orderRepository.countByCreatedAtAfter(todayStart)
    val totalUsers = userRepository.count()
    val totalProducts = productRepository.count()

    val orderStatusCounts = orderRepository.countGroupByStatus()
      .associate { row -> (row[0] as Enum<*>).name to (row[1] as Long) }

    // 예산 현황
    val dailyBudget = budgetRepository.findByPeriodTypeAndPeriodDate(PeriodType.DAILY, today)
    val todayBudgetLimit = dailyBudget?.limitAmount ?: 0L
    val todayBudgetSpent = dailyBudget?.spentAmount ?: 0L
    val todayBudgetRemaining = dailyBudget?.remainingAmount ?: 0L

    // 룰렛 참여 현황
    val todaySpinCount = spinHistoryRepository.countByCancelledFalseAndCreatedAtBetween(todayStart, todayEnd)
    val todayPointsDistributed = spinHistoryRepository.sumRewardPointByCreatedAtBetween(todayStart, todayEnd)

    return DashboardSummaryResponse(
      todayRevenue = todayRevenue,
      todayOrderCount = todayOrderCount,
      totalUsers = totalUsers,
      totalProducts = totalProducts,
      orderStatusCounts = orderStatusCounts,
      todayBudgetLimit = todayBudgetLimit,
      todayBudgetSpent = todayBudgetSpent,
      todayBudgetRemaining = todayBudgetRemaining,
      todaySpinCount = todaySpinCount,
      todayPointsDistributed = todayPointsDistributed,
    )
  }
}