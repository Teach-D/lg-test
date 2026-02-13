package com.example.pointroulette.budget.service

import com.example.pointroulette.budget.dto.BudgetResponse
import com.example.pointroulette.budget.dto.BudgetSummaryResponse
import com.example.pointroulette.budget.dto.SetBudgetRequest
import com.example.pointroulette.budget.entity.Budget
import com.example.pointroulette.budget.entity.PeriodType
import com.example.pointroulette.budget.repository.BudgetRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate

@Service
@Transactional(readOnly = true)
class BudgetService(
  private val budgetRepository: BudgetRepository,
) {

  /** 오늘/이번달 예산 요약 조회 */
  fun getSummary(): BudgetSummaryResponse {
    val today = LocalDate.now()
    val monthStart = today.withDayOfMonth(1)

    val daily = budgetRepository
      .findByPeriodTypeAndPeriodDate(PeriodType.DAILY, today)
      ?.let(BudgetResponse::from)

    val monthly = budgetRepository
      .findByPeriodTypeAndPeriodDate(PeriodType.MONTHLY, monthStart)
      ?.let(BudgetResponse::from)

    return BudgetSummaryResponse(dailyBudget = daily, monthlyBudget = monthly)
  }

  /** 예산 설정 (없으면 생성, 있으면 한도 수정) */
  @Transactional
  fun setBudget(request: SetBudgetRequest): BudgetResponse {
    val existing = budgetRepository.findByPeriodTypeAndPeriodDate(
      request.periodType,
      request.periodDate,
    )

    val budget = if (existing != null) {
      existing.updateLimit(request.limitAmount)
      existing
    } else {
      budgetRepository.save(
        Budget(
          periodType = request.periodType,
          periodDate = request.periodDate,
          limitAmount = request.limitAmount,
        ),
      )
    }

    return BudgetResponse.from(budget)
  }
}
