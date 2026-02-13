package com.example.pointroulette.roulette.dto

import com.example.pointroulette.roulette.entity.RouletteSegment

data class SegmentResponse(
  val id: Long,
  val label: String,
  val rewardPoint: Int,
  val displayOrder: Int,
) {
  companion object {
    fun from(seg: RouletteSegment) = SegmentResponse(
      id = seg.id,
      label = seg.label,
      rewardPoint = seg.rewardPoint,
      displayOrder = seg.displayOrder,
    )
  }
}

data class RouletteConfigResponse(
  val spinCost: Int,
  val segments: List<SegmentResponse>,
)

data class SpinResultResponse(
  val rewardPoint: Int,
  val remainingPoint: Int,
)

data class RouletteStatusResponse(
  val hasSpunToday: Boolean,
  val dailyBudgetRemaining: Long,
  val spinCost: Int,
)

data class AdminSpinResponse(
  val id: Long,
  val userId: Long,
  val segmentLabel: String,
  val rewardPoint: Int,
  val costPoint: Int,
  val cancelled: Boolean,
  val createdAt: java.time.LocalDateTime,
) {
  companion object {
    fun from(spin: com.example.pointroulette.roulette.entity.SpinHistory) = AdminSpinResponse(
      id = spin.id,
      userId = spin.userId,
      segmentLabel = spin.segmentLabel,
      rewardPoint = spin.rewardPoint,
      costPoint = spin.costPoint,
      cancelled = spin.cancelled,
      createdAt = spin.createdAt,
    )
  }
}