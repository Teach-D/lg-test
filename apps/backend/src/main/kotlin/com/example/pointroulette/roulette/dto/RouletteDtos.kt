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
  val segmentId: Long,
  val segmentLabel: String,
  val rewardPoint: Int,
  val segmentIndex: Int,
  val remainingPoint: Int,
)