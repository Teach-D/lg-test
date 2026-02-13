package com.example.pointroulette.roulette.entity

import com.example.pointroulette.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "spin_history")
class SpinHistory(
  @Column(nullable = false)
  val userId: Long,

  @Column(nullable = false)
  val segmentId: Long,

  @Column(nullable = false, length = 50)
  val segmentLabel: String,

  @Column(nullable = false)
  val rewardPoint: Int,

  @Column(nullable = false)
  val costPoint: Int,
) : BaseEntity()