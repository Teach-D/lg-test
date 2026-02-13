package com.example.pointroulette.budget.dto

import com.example.pointroulette.budget.entity.Budget
import com.example.pointroulette.budget.entity.PeriodType
import jakarta.validation.constraints.Min
import java.time.LocalDate

data class BudgetResponse(
  val id: Long,
  val periodType: PeriodType,
  val periodDate: LocalDate,
  val limitAmount: Long,
  val spentAmount: Long,
  val remainingAmount: Long,
) {
  companion object {
    fun from(budget: Budget) = BudgetResponse(
      id = budget.id,
      periodType = budget.periodType,
      periodDate = budget.periodDate,
      limitAmount = budget.limitAmount,
      spentAmount = budget.spentAmount,
      remainingAmount = budget.remainingAmount,
    )
  }
}

data class SetBudgetRequest(
  val periodType: PeriodType,
  val periodDate: LocalDate,

  @field:Min(value = 0, message = "예산 한도는 0 이상이어야 합니다")
  val limitAmount: Long,
)

data class BudgetSummaryResponse(
  val dailyBudget: BudgetResponse?,
  val monthlyBudget: BudgetResponse?,
)
