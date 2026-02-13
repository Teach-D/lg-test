package com.example.pointroulette.product.repository

import com.example.pointroulette.product.entity.Product
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository

interface ProductRepository : JpaRepository<Product, Long> {
  fun findAllByOrderByCreatedAtDesc(pageable: Pageable): Page<Product>
}
