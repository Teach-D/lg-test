package com.example.pointroulette.budget.service

import com.example.pointroulette.budget.dto.BudgetResponse
import com.example.pointroulette.budget.dto.BudgetSummaryResponse
import com.example.pointroulette.budget.dto.SetBudgetRequest
import com.example.pointroulette.budget.entity.Budget
import com.example.pointroulette.budget.entity.PeriodType
import com.example.pointroulette.budget.repository.BudgetRepository
import com.example.pointroulette.roulette.exception.BudgetExceededException
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

  /** 일일 예산 확인 후 소비 (원자적 UPDATE). 예산 미설정 시 제한 없음 */
  @Transactional
  fun checkAndSpendDailyBudget(amount: Int) {
    val today = LocalDate.now()
    val budget = budgetRepository.findByPeriodTypeAndPeriodDate(PeriodType.DAILY, today)
      ?: return // 예산 미설정 시 제한 없음

    val updated = budgetRepository.spendBudget(PeriodType.DAILY, today, amount.toLong())
    if (updated == 0) {
      throw BudgetExceededException()
    }
  }

  /** 오늘 남은 일일 예산 조회. 미설정 시 -1 반환 */
  fun getDailyRemaining(): Long {
    val today = LocalDate.now()
    val budget = budgetRepository.findByPeriodTypeAndPeriodDate(PeriodType.DAILY, today)
      ?: return -1L
    return budget.remainingAmount
  }

  /** 예산 소비 복원 (취소 시 사용) */
  @Transactional
  fun restoreDailyBudget(amount: Int, date: LocalDate) {
    budgetRepository.restoreBudget(PeriodType.DAILY, date, amount.toLong())
  }
}
