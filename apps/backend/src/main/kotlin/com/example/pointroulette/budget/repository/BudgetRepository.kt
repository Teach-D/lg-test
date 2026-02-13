package com.example.pointroulette.budget.repository

import com.example.pointroulette.budget.entity.Budget
import com.example.pointroulette.budget.entity.PeriodType
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Modifying
import org.springframework.data.jpa.repository.Query
import java.time.LocalDate

interface BudgetRepository : JpaRepository<Budget, Long> {
  fun findByPeriodTypeAndPeriodDate(periodType: PeriodType, periodDate: LocalDate): Budget?

  /** 예산 잔여량이 충분할 때만 원자적으로 차감. 반환값: 변경된 행 수 (0이면 잔액 부족) */
  @Modifying
  @Query(
    "UPDATE Budget b SET b.spentAmount = b.spentAmount + :amount " +
      "WHERE b.periodType = :type AND b.periodDate = :date " +
      "AND (b.limitAmount - b.spentAmount) >= :amount",
  )
  fun spendBudget(type: PeriodType, date: LocalDate, amount: Long): Int

  /** 예산 복원 (취소 시 사용) */
  @Modifying
  @Query(
    "UPDATE Budget b SET b.spentAmount = b.spentAmount - :amount " +
      "WHERE b.periodType = :type AND b.periodDate = :date",
  )
  fun restoreBudget(type: PeriodType, date: LocalDate, amount: Long): Int
}
