package com.example.pointroulette.roulette.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.roulette.dto.RouletteConfigResponse
import com.example.pointroulette.roulette.dto.SpinResultResponse
import com.example.pointroulette.roulette.service.RouletteService
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** 룰렛 API */
@RestController
@RequestMapping("/api/roulette")
class RouletteController(
  private val rouletteService: RouletteService,
) {

  /** 룰렛 설정 조회 (세그먼트 + 스핀 비용) */
  @GetMapping("/config")
  fun getConfig(): ApiResponse<RouletteConfigResponse> =
    ApiResponse.ok(rouletteService.getConfig())

  /** 룰렛 스핀 실행 */
  @PostMapping("/spin")
  fun spin(
    @AuthenticationPrincipal userId: Long,
  ): ApiResponse<SpinResultResponse> =
    ApiResponse.ok(rouletteService.spin(userId))
}