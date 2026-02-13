package com.example.pointroulette.roulette.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.roulette.dto.AdminSpinResponse
import com.example.pointroulette.roulette.service.RouletteService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/** 관리자 룰렛 관리 API */
@Tag(name = "Admin - Roulette", description = "관리자 룰렛 스핀 이력 관리 API")
@RestController
@RequestMapping("/api/admin/roulette")
class AdminRouletteController(
  private val rouletteService: RouletteService,
) {

  /** 스핀 이력 목록 조회 */
  @Operation(
    summary = "스핀 이력 목록 조회",
    security = [SecurityRequirement(name = "bearerAuth")],
  )
  @GetMapping("/spins")
  fun getSpins(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int,
  ): ApiResponse<Page<AdminSpinResponse>> =
    ApiResponse.ok(rouletteService.getSpinHistory(page, size))

  /** 스핀 취소 (포인트 회수 + 예산 복원) */
  @Operation(
    summary = "스핀 취소",
    description = "스핀을 취소하고 지급된 포인트를 회수합니다",
    security = [SecurityRequirement(name = "bearerAuth")],
  )
  @DeleteMapping("/spins/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun cancelSpin(@PathVariable id: Long) {
    rouletteService.cancelSpin(id)
  }
}
