package com.example.pointroulette.product.service

import com.example.pointroulette.product.dto.CreateProductRequest
import com.example.pointroulette.product.dto.ProductResponse
import com.example.pointroulette.product.dto.UpdateProductRequest
import com.example.pointroulette.product.entity.Product
import com.example.pointroulette.product.exception.ProductNotFoundException
import com.example.pointroulette.product.repository.ProductRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional(readOnly = true)
class ProductService(
  private val productRepository: ProductRepository,
) {

  /** 상품 목록 페이지 조회 */
  fun getProducts(page: Int, size: Int): Page<ProductResponse> {
    val pageable = PageRequest.of(page, size)
    return productRepository.findAllByOrderByCreatedAtDesc(pageable)
      .map(ProductResponse::from)
  }

  /** 상품 단건 조회 */
  fun getProduct(id: Long): ProductResponse {
    val product = findById(id)
    return ProductResponse.from(product)
  }

  /** 상품 등록 */
  @Transactional
  fun createProduct(request: CreateProductRequest): ProductResponse {
    val product = Product(
      name = request.name,
      imageUrl = request.imageUrl,
      pointCost = request.pointCost,
      stock = request.stock,
      active = request.active,
    )
    return ProductResponse.from(productRepository.save(product))
  }

  /** 상품 수정 */
  @Transactional
  fun updateProduct(id: Long, request: UpdateProductRequest): ProductResponse {
    val product = findById(id)
    product.update(
      name = request.name,
      imageUrl = request.imageUrl,
      pointCost = request.pointCost,
      stock = request.stock,
      active = request.active,
    )
    return ProductResponse.from(product)
  }

  /** 상품 삭제 */
  @Transactional
  fun deleteProduct(id: Long) {
    val product = findById(id)
    productRepository.delete(product)
  }

  private fun findById(id: Long): Product =
    productRepository.findById(id)
      .orElseThrow { ProductNotFoundException(id) }
}
