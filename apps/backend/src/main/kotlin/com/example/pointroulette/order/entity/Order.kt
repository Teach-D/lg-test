package com.example.pointroulette.order.entity

import com.example.pointroulette.common.entity.BaseEntity
import com.example.pointroulette.order.exception.InvalidStatusTransitionException
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "orders")
class Order(
  @Column(nullable = false)
  val userId: Long,

  @Column(nullable = false)
  val productId: Long,

  @Column(nullable = false, length = 100)
  val productName: String,

  @Column(nullable = false)
  val pointCost: Int,

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  var status: OrderStatus = OrderStatus.PENDING,

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  val orderType: OrderType = OrderType.EXCHANGE,
) : BaseEntity() {

  fun updateStatus(newStatus: OrderStatus) {
    validateTransition(newStatus)
    this.status = newStatus
  }

  private fun validateTransition(newStatus: OrderStatus) {
    val allowed = when (status) {
      OrderStatus.PENDING -> setOf(OrderStatus.CONFIRMED, OrderStatus.CANCELLED)
      OrderStatus.CONFIRMED -> setOf(OrderStatus.SHIPPED, OrderStatus.CANCELLED)
      OrderStatus.SHIPPED -> setOf(OrderStatus.COMPLETED)
      OrderStatus.COMPLETED -> emptySet()
      OrderStatus.CANCELLED -> emptySet()
    }
    if (newStatus !in allowed) {
      throw InvalidStatusTransitionException(status.name, newStatus.name)
    }
  }
}

enum class OrderStatus {
  PENDING, CONFIRMED, SHIPPED, COMPLETED, CANCELLED
}

enum class OrderType {
  EXCHANGE, ROULETTE_WIN
}