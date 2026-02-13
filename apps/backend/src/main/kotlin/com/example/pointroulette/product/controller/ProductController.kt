package com.example.pointroulette.product.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.product.dto.ProductResponse
import com.example.pointroulette.product.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.data.domain.Page
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

/** 사용자용 상품 조회 API */
@Tag(name = "Product", description = "상품 조회 API")
@RestController
@RequestMapping("/api/products")
class ProductController(
  private val productService: ProductService,
) {

  /** 활성 상품 목록 조회 */
  @Operation(summary = "상품 목록 조회", description = "활성 상태인 상품만 조회합니다")
  @GetMapping
  fun getProducts(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int,
  ): ApiResponse<Page<ProductResponse>> =
    ApiResponse.ok(productService.getActiveProducts(page, size))

  /** 활성 상품 단건 조회 */
  @Operation(summary = "상품 상세 조회")
  @GetMapping("/{id}")
  fun getProduct(@PathVariable id: Long): ApiResponse<ProductResponse> =
    ApiResponse.ok(productService.getActiveProduct(id))
}
