package com.example.pointroulette.budget.repository

import com.example.pointroulette.budget.entity.Budget
import com.example.pointroulette.budget.entity.PeriodType
import org.springframework.data.jpa.repository.JpaRepository
import java.time.LocalDate

interface BudgetRepository : JpaRepository<Budget, Long> {
  fun findByPeriodTypeAndPeriodDate(periodType: PeriodType, periodDate: LocalDate): Budget?
}
