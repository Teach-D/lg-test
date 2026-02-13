package com.example.pointroulette.order.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.order.dto.CreateExchangeOrderRequest
import com.example.pointroulette.order.dto.OrderResponse
import com.example.pointroulette.order.entity.OrderStatus
import com.example.pointroulette.order.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Order", description = "주문 API")
@RestController
@RequestMapping("/api/orders")
class OrderController(
  private val orderService: OrderService,
) {

  @Operation(summary = "상품 교환 주문", description = "포인트를 차감하여 상품 교환 주문을 생성합니다")
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  fun createExchangeOrder(
    @AuthenticationPrincipal userId: Long,
    @Valid @RequestBody request: CreateExchangeOrderRequest,
  ): ApiResponse<OrderResponse> =
    ApiResponse.ok(orderService.createExchangeOrder(userId, request.productId))

  @Operation(summary = "내 주문 목록 조회", description = "로그인한 사용자의 주문 내역을 조회합니다")
  @GetMapping("/me")
  fun getMyOrders(
    @AuthenticationPrincipal userId: Long,
    @RequestParam(required = false) status: OrderStatus?,
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int,
  ): ApiResponse<Page<OrderResponse>> =
    ApiResponse.ok(orderService.getMyOrders(userId, status, page, size))
}