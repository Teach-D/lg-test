package com.example.pointroulette.point.dto

import com.example.pointroulette.point.entity.PointHistory
import com.example.pointroulette.point.entity.PointType
import java.time.LocalDateTime

data class PointHistoryResponse(
  val id: Long,
  val amount: Int,
  val type: PointType,
  val description: String,
  val createdAt: LocalDateTime,
) {
  companion object {
    fun from(history: PointHistory) = PointHistoryResponse(
      id = history.id,
      amount = history.amount,
      type = history.type,
      description = history.description,
      createdAt = history.createdAt,
    )
  }
}

data class PointSummaryResponse(
  val currentPoint: Int,
  val histories: List<PointHistoryResponse>,
) {
  companion object {
    fun of(currentPoint: Int, histories: List<PointHistory>) = PointSummaryResponse(
      currentPoint = currentPoint,
      histories = histories.map { PointHistoryResponse.from(it) },
    )
  }
}
