package com.example.pointroulette.roulette.entity

import com.example.pointroulette.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "roulette_segment")
class RouletteSegment(
  @Column(nullable = false, length = 50)
  val label: String,

  /** 당첨 시 지급 포인트 (0이면 꽝) */
  @Column(nullable = false)
  val rewardPoint: Int,

  /** 당첨 확률 가중치 (전체 합 대비 비율) */
  @Column(nullable = false)
  val weight: Int,

  @Column(nullable = false)
  val displayOrder: Int,
) : BaseEntity()