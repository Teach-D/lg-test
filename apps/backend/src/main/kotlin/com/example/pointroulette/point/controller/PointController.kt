package com.example.pointroulette.point.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.point.dto.PointSummaryResponse
import com.example.pointroulette.point.service.PointService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Point", description = "포인트 API")
@RestController
@RequestMapping("/api/points")
class PointController(
  private val pointService: PointService,
) {

  /**
   * 내 포인트 정보 조회
   */
  @Operation(summary = "내 포인트 조회", description = "현재 포인트와 최근 이력 10건을 조회한다")
  @ApiResponses(
    io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "200",
      description = "성공"
    ),
    io.swagger.v3.oas.annotations.responses.ApiResponse(
      responseCode = "401",
      description = "인증 실패"
    ),
  )
  @GetMapping("/me")
  fun getMyPoints(
    @AuthenticationPrincipal userId: Long,
  ): ApiResponse<PointSummaryResponse> {
    val summary = pointService.getMyPointSummary(userId)
    return ApiResponse.ok(summary)
  }
}
