package com.example.pointroulette.user.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.user.dto.UserResponse
import com.example.pointroulette.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "User", description = "사용자 API")
@RestController
@RequestMapping("/api/users")
class UserController(
  private val userService: UserService,
) {

  @Operation(
    summary = "내 정보 조회",
    description = "인증된 사용자의 정보를 조회합니다",
    security = [SecurityRequirement(name = "bearerAuth")]
  )
  @ApiResponses(
    io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
    io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
  )
  @GetMapping("/me")
  fun getMe(@AuthenticationPrincipal userId: Long): ApiResponse<UserResponse> {
    val user = userService.getUserById(userId)
    return ApiResponse.ok(user)
  }
}
