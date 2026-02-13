package com.example.pointroulette.dashboard.dto

data class DashboardSummaryResponse(
  val todayRevenue: Long,
  val todayOrderCount: Long,
  val totalUsers: Long,
  val totalProducts: Long,
  val orderStatusCounts: Map<String, Long>,
)