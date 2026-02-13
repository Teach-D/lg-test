package com.example.pointroulette.product.dto

import com.example.pointroulette.product.entity.Product
import jakarta.validation.constraints.Min
import jakarta.validation.constraints.NotBlank
import jakarta.validation.constraints.Size
import java.time.LocalDateTime

data class ProductResponse(
  val id: Long,
  val name: String,
  val imageUrl: String?,
  val pointCost: Int,
  val stock: Int,
  val active: Boolean,
  val createdAt: LocalDateTime,
  val updatedAt: LocalDateTime,
) {
  companion object {
    fun from(product: Product) = ProductResponse(
      id = product.id,
      name = product.name,
      imageUrl = product.imageUrl,
      pointCost = product.pointCost,
      stock = product.stock,
      active = product.active,
      createdAt = product.createdAt,
      updatedAt = product.updatedAt,
    )
  }
}

data class CreateProductRequest(
  @field:NotBlank(message = "상품명은 필수입니다")
  @field:Size(max = 100, message = "상품명은 100자 이하입니다")
  val name: String,

  val imageUrl: String? = null,

  @field:Min(value = 0, message = "포인트 비용은 0 이상이어야 합니다")
  val pointCost: Int,

  @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
  val stock: Int,

  val active: Boolean = true,
)

data class UpdateProductRequest(
  @field:NotBlank(message = "상품명은 필수입니다")
  @field:Size(max = 100, message = "상품명은 100자 이하입니다")
  val name: String,

  val imageUrl: String? = null,

  @field:Min(value = 0, message = "포인트 비용은 0 이상이어야 합니다")
  val pointCost: Int,

  @field:Min(value = 0, message = "재고는 0 이상이어야 합니다")
  val stock: Int,

  val active: Boolean = true,
)
