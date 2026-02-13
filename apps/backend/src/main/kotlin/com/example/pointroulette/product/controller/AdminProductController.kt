package com.example.pointroulette.product.controller

import com.example.pointroulette.common.dto.ApiResponse
import com.example.pointroulette.product.dto.CreateProductRequest
import com.example.pointroulette.product.dto.ProductResponse
import com.example.pointroulette.product.dto.UpdateProductRequest
import com.example.pointroulette.product.service.ProductService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.web.bind.annotation.DeleteMapping
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.PutMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.ResponseStatus
import org.springframework.web.bind.annotation.RestController

/** 어드민용 상품 관리 API */
@Tag(name = "Admin - Product", description = "관리자 상품 관리 API")
@RestController
@RequestMapping("/api/admin/products")
class AdminProductController(
  private val productService: ProductService,
) {

  /** 전체 상품 목록 조회 (active 무관) */
  @Operation(
    summary = "전체 상품 목록 조회",
    description = "active 여부와 관계없이 모든 상품을 조회합니다",
    security = [SecurityRequirement(name = "bearerAuth")],
  )
  @GetMapping
  fun getAllProducts(
    @RequestParam(defaultValue = "0") page: Int,
    @RequestParam(defaultValue = "10") size: Int,
  ): ApiResponse<Page<ProductResponse>> =
    ApiResponse.ok(productService.getProducts(page, size))

  /** 상품 단건 조회 */
  @Operation(summary = "상품 단건 조회", security = [SecurityRequirement(name = "bearerAuth")])
  @GetMapping("/{id}")
  fun getProduct(@PathVariable id: Long): ApiResponse<ProductResponse> =
    ApiResponse.ok(productService.getProduct(id))

  /** 상품 등록 */
  @Operation(summary = "상품 등록", security = [SecurityRequirement(name = "bearerAuth")])
  @PostMapping
  @ResponseStatus(HttpStatus.CREATED)
  fun createProduct(
    @Valid @RequestBody request: CreateProductRequest,
  ): ApiResponse<ProductResponse> =
    ApiResponse.ok(productService.createProduct(request))

  /** 상품 수정 */
  @Operation(summary = "상품 수정", security = [SecurityRequirement(name = "bearerAuth")])
  @PutMapping("/{id}")
  fun updateProduct(
    @PathVariable id: Long,
    @Valid @RequestBody request: UpdateProductRequest,
  ): ApiResponse<ProductResponse> =
    ApiResponse.ok(productService.updateProduct(id, request))

  /** 상품 삭제 */
  @Operation(summary = "상품 삭제", security = [SecurityRequirement(name = "bearerAuth")])
  @DeleteMapping("/{id}")
  @ResponseStatus(HttpStatus.NO_CONTENT)
  fun deleteProduct(@PathVariable id: Long) {
    productService.deleteProduct(id)
  }
}
