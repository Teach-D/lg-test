package com.example.pointroulette.user.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.user.dto.LoginRequest
import com.example.pointroulette.user.dto.LoginResponse
import com.example.pointroulette.user.dto.RegisterRequest
import com.example.pointroulette.user.service.AuthService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Auth", description = "인증 API")
@RestController
@RequestMapping("/api/auth")
class AuthController(
  private val authService: AuthService,
) {

  @Operation(summary = "회원가입", description = "신규 사용자를 등록하고 가입 보너스 1000포인트를 지급합니다")
  @ApiResponses(
    io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "회원가입 성공"),
    io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "이메일 중복 또는 입력값 오류"),
  )
  @PostMapping("/register")
  fun register(@Valid @RequestBody request: RegisterRequest): ApiResponse<LoginResponse> {
    val response = authService.register(request)
    return ApiResponse.ok(response)
  }

  @Operation(summary = "로그인", description = "이메일과 비밀번호로 로그인하여 JWT 토큰을 발급받습니다")
  @ApiResponses(
    io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "로그인 성공"),
    io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "400", description = "인증 실패"),
  )
  @PostMapping("/login")
  fun login(@Valid @RequestBody request: LoginRequest): ApiResponse<LoginResponse> {
    val response = authService.login(request)
    return ApiResponse.ok(response)
  }
}
