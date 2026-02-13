package com.example.pointroulette.budget.entity

import com.example.pointroulette.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table
import java.time.LocalDate

@Entity
@Table(name = "budget")
class Budget(
  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 10)
  var periodType: PeriodType,

  @Column(nullable = false)
  var periodDate: LocalDate,

  @Column(nullable = false)
  var limitAmount: Long,

  @Column(nullable = false)
  var spentAmount: Long = 0,
) : BaseEntity() {

  fun updateLimit(limitAmount: Long) {
    this.limitAmount = limitAmount
  }

  fun addSpent(amount: Long) {
    this.spentAmount += amount
  }

  val remainingAmount: Long
    get() = limitAmount - spentAmount
}

enum class PeriodType {
  DAILY, MONTHLY
}
