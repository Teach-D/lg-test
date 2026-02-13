package com.example.pointroulette.order.dto

import com.example.pointroulette.order.entity.Order
import com.example.pointroulette.order.entity.OrderStatus
import com.example.pointroulette.order.entity.OrderType
import jakarta.validation.constraints.Min
import java.time.LocalDateTime

data class OrderResponse(
  val id: Long,
  val userId: Long,
  val productId: Long,
  val productName: String,
  val pointCost: Int,
  val status: OrderStatus,
  val orderType: OrderType,
  val createdAt: LocalDateTime,
) {
  companion object {
    fun from(order: Order) = OrderResponse(
      id = order.id,
      userId = order.userId,
      productId = order.productId,
      productName = order.productName,
      pointCost = order.pointCost,
      status = order.status,
      orderType = order.orderType,
      createdAt = order.createdAt,
    )
  }
}

data class CreateExchangeOrderRequest(
  @field:Min(value = 1, message = "상품 ID는 필수입니다")
  val productId: Long,
)

data class UpdateOrderStatusRequest(
  val status: OrderStatus,
)