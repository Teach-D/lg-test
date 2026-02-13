package com.example.pointroulette.user.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.user.dto.AdminUserResponse
import com.example.pointroulette.user.service.UserService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponses
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** 관리자 사용자 관리 API */
@Tag(name = "Admin - User", description = "관리자 사용자 관리 API")
@RestController
@RequestMapping("/api/admin/users")
class AdminUserController(
  private val userService: UserService,
) {

  /**
   * 전체 사용자 목록 페이징 조회.
   * search 파라미터가 있으면 이메일 또는 닉네임으로 부분 검색한다.
   */
  @Operation(
    summary = "사용자 목록 조회",
    description = "전체 사용자를 페이징으로 조회합니다. search 파라미터로 이메일/닉네임 검색이 가능합니다.",
    security = [SecurityRequirement(name = "bearerAuth")]
  )
  @ApiResponses(
    io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "200", description = "조회 성공"),
    io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "401", description = "인증 실패"),
    io.swagger.v3.oas.annotations.responses.ApiResponse(responseCode = "403", description = "권한 없음"),
  )
  @GetMapping
  fun getUsers(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int,
    @RequestParam(required = false) search: String?,
  ): ApiResponse<Page<AdminUserResponse>> =
    ApiResponse.ok(userService.getAdminUsers(page, size, search))
}
