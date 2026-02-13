package com.example.pointroulette.roulette.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.roulette.dto.RouletteConfigResponse
import com.example.pointroulette.roulette.dto.RouletteStatusResponse
import com.example.pointroulette.roulette.dto.SpinResultResponse
import com.example.pointroulette.roulette.service.RouletteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

/** 룰렛 API */
@Tag(name = "Roulette", description = "룰렛 API")
@RestController
@RequestMapping("/api/roulette")
class RouletteController(
  private val rouletteService: RouletteService,
) {

  /** 룰렛 설정 조회 (세그먼트 + 스핀 비용) */
  @Operation(summary = "룰렛 설정 조회")
  @GetMapping("/config")
  fun getConfig(): ApiResponse<RouletteConfigResponse> =
    ApiResponse.ok(rouletteService.getConfig())

  /** 오늘 참여 여부 + 잔여 예산 확인 */
  @Operation(summary = "룰렛 상태 조회", description = "오늘 스핀 여부, 잔여 예산, 스핀 비용을 조회합니다")
  @GetMapping("/status")
  fun getStatus(
    @AuthenticationPrincipal userId: Long,
  ): ApiResponse<RouletteStatusResponse> =
    ApiResponse.ok(rouletteService.getStatus(userId))

  /** 룰렛 스핀 실행 (1일 1회) */
  @Operation(summary = "룰렛 스핀", description = "1일 1회 룰렛을 실행합니다")
  @PostMapping("/spin")
  fun spin(
    @AuthenticationPrincipal userId: Long,
  ): ApiResponse<SpinResultResponse> =
    ApiResponse.ok(rouletteService.spin(userId))
}