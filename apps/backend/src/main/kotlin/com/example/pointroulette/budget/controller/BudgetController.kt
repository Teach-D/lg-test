package com.example.pointroulette.budget.controller

import com.example.pointroulette.budget.dto.BudgetResponse
import com.example.pointroulette.budget.dto.BudgetSummaryResponse
import com.example.pointroulette.budget.dto.SetBudgetRequest
import com.example.pointroulette.budget.service.BudgetService
import com.example.pointroulette.common.dto.ApiResponse
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** 예산 관리 API */
@RestController
@RequestMapping("/api/budgets")
class BudgetController(
  private val budgetService: BudgetService,
) {

  /** 오늘/이번달 예산 요약 조회 */
  @GetMapping("/summary")
  fun getSummary(): ApiResponse<BudgetSummaryResponse> =
    ApiResponse.ok(budgetService.getSummary())

  /** 예산 한도 설정 */
  @PutMapping
  fun setBudget(
    @Valid @RequestBody request: SetBudgetRequest,
  ): ApiResponse<BudgetResponse> =
    ApiResponse.ok(budgetService.setBudget(request))
}
