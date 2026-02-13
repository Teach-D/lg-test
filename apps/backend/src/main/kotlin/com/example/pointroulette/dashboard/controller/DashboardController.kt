package com.example.pointroulette.dashboard.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.dashboard.dto.DashboardSummaryResponse
import com.example.pointroulette.dashboard.service.DashboardService
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** 대시보드 API */
@RestController
@RequestMapping("/api/admin/dashboard")
class DashboardController(
  private val dashboardService: DashboardService,
) {

  /** 대시보드 요약 데이터 조회 */
  @GetMapping("/summary")
  fun getSummary(): ApiResponse<DashboardSummaryResponse> =
    ApiResponse.ok(dashboardService.getSummary())
}