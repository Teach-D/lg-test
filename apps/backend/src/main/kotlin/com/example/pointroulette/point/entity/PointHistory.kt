package com.example.pointroulette.point.entity

import com.example.pointroulette.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDateTime

@Entity
@Table(name = "point_history")
class PointHistory(
  @Column(nullable = false, name = "user_id")
  val userId: Long,

  @Column(nullable = false)
  val amount: Int,

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  val type: PointType,

  @Column(nullable = false, length = 100)
  val description: String,

  @Column
  var expiresAt: LocalDateTime? = null,
) : BaseEntity()

enum class PointType {
  SIGNUP_BONUS,
  ROULETTE_WIN,
  PRODUCT_EXCHANGE,
  PRODUCT_REFUND,
  ADMIN_ADJUST,
  EXPIRED,
}
