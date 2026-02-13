package com.example.pointroulette.order.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.order.dto.OrderResponse
import com.example.pointroulette.order.dto.UpdateOrderStatusRequest
import com.example.pointroulette.order.entity.OrderStatus
import com.example.pointroulette.order.service.OrderService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PatchMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@Tag(name = "Admin - Order", description = "관리자 주문 관리 API")
@RestController
@RequestMapping("/api/admin/orders")
class AdminOrderController(
  private val orderService: OrderService,
) {

  @Operation(summary = "전체 주문 목록 조회", description = "모든 주문을 조회합니다. 상태로 필터링할 수 있습니다")
  @GetMapping
  fun getAllOrders(
    @RequestParam(required = false) status: OrderStatus?,
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int,
  ): ApiResponse<Page<OrderResponse>> =
    ApiResponse.ok(orderService.getAllOrders(status, page, size))

  @Operation(summary = "주문 상태 변경", description = "주문 상태를 변경합니다. 취소 시 포인트가 환불됩니다")
  @PatchMapping("/{id}/status")
  fun updateStatus(
    @PathVariable id: Long,
    @Valid @RequestBody request: UpdateOrderStatusRequest,
  ): ApiResponse<OrderResponse> =
    ApiResponse.ok(orderService.updateOrderStatus(id, request.status))
}