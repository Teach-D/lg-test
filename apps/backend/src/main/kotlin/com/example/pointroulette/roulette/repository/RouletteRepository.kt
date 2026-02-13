package com.example.pointroulette.roulette.repository

import com.example.pointroulette.roulette.entity.RouletteSegment
import com.example.pointroulette.roulette.entity.SpinHistory
import org.springframework.data.jpa.repository.JpaRepository

interface RouletteSegmentRepository : JpaRepository<RouletteSegment, Long> {
  fun findAllByOrderByDisplayOrderAsc(): List<RouletteSegment>
}

interface SpinHistoryRepository : JpaRepository<SpinHistory, Long>