package com.example.pointroulette.order.service

import com.example.pointroulette.order.dto.OrderResponse
import com.example.pointroulette.common.exception.BusinessException
import com.example.pointroulette.order.entity.Order
import com.example.pointroulette.order.entity.OrderStatus
import com.example.pointroulette.order.entity.OrderType
import com.example.pointroulette.order.exception.ProductOutOfStockException
import com.example.pointroulette.order.repository.OrderRepository
import com.example.pointroulette.point.entity.PointType
import com.example.pointroulette.point.service.PointService
import com.example.pointroulette.product.exception.ProductNotFoundException
import com.example.pointroulette.product.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class OrderService(
  private val orderRepository: OrderRepository,
  private val productRepository: ProductRepository,
  private val pointService: PointService,
) {

  /** 상품 교환 주문 생성 (포인트 차감 + 재고 차감) */
  @Transactional
  fun createExchangeOrder(userId: Long, productId: Long): OrderResponse {
    val product = productRepository.findById(productId)
      .orElseThrow { ProductNotFoundException(productId) }

    if (product.stock <= 0) {
      throw ProductOutOfStockException(product.name)
    }

    // 포인트 차감
    pointService.addPointHistory(
      userId = userId,
      amount = -product.pointCost,
      type = PointType.PRODUCT_EXCHANGE,
      description = "'${product.name}' 상품 교환",
    )

    // 재고 차감
    product.stock -= 1

    val order = orderRepository.save(
      Order(
        userId = userId,
        productId = product.id,
        productName = product.name,
        pointCost = product.pointCost,
        orderType = OrderType.EXCHANGE,
      ),
    )

    return OrderResponse.from(order)
  }

  /** 내 주문 목록 조회 */
  fun getMyOrders(userId: Long, status: OrderStatus?, page: Int, size: Int): Page<OrderResponse> {
    val pageable = PageRequest.of(page, size)

    val orders = if (status != null) {
      orderRepository.findByUserIdAndStatusOrderByCreatedAtDesc(userId, status, pageable)
    } else {
      orderRepository.findByUserIdOrderByCreatedAtDesc(userId, pageable)
    }

    return orders.map(OrderResponse::from)
  }

  /** [관리자] 전체 주문 목록 조회 */
  fun getAllOrders(status: OrderStatus?, page: Int, size: Int): Page<OrderResponse> {
    val pageable = PageRequest.of(page, size)

    val orders = if (status != null) {
      orderRepository.findByStatusOrderByCreatedAtDesc(status, pageable)
    } else {
      orderRepository.findAllByOrderByCreatedAtDesc(pageable)
    }

    return orders.map(OrderResponse::from)
  }

  /** [관리자] 주문 상태 변경 */
  @Transactional
  fun updateOrderStatus(orderId: Long, status: OrderStatus): OrderResponse {
    val order = orderRepository.findById(orderId)
      .orElseThrow { BusinessException("ORDER_NOT_FOUND", "주문을 찾을 수 없습니다. (id=$orderId)") }
    order.updateStatus(status)
    return OrderResponse.from(order)
  }
}