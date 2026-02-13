package com.example.pointroulette.product.entity

import com.example.pointroulette.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table

@Entity
@Table(name = "product")
class Product(
  @Column(nullable = false, length = 100)
  var name: String,

  @Column(length = 500)
  var imageUrl: String? = null,

  @Column(nullable = false)
  var pointCost: Int,

  @Column(nullable = false)
  var stock: Int,

  @Column(nullable = false)
  var active: Boolean = true,
) : BaseEntity() {

  fun update(name: String, imageUrl: String?, pointCost: Int, stock: Int, active: Boolean) {
    this.name = name
    this.imageUrl = imageUrl
    this.pointCost = pointCost
    this.stock = stock
    this.active = active
  }
}
