package com.example.pointroulette.user.entity

import com.example.pointroulette.common.entity.BaseEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.EnumType
import jakarta.persistence.Enumerated
import jakarta.persistence.Table

@Entity
@Table(name = "users")
class User(
  @Column(nullable = false, unique = true, length = 100)
  val email: String,

  @Column(nullable = false)
  var password: String,

  @Column(nullable = false, length = 50)
  var nickname: String,

  @Column(nullable = false)
  var point: Int = 1000,

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 20)
  val role: Role = Role.USER,
) : BaseEntity() {

  fun updateNickname(newNickname: String) {
    this.nickname = newNickname
  }

  fun addPoint(amount: Int) {
    this.point += amount
  }

  fun deductPoint(amount: Int) {
    require(this.point >= amount) { "포인트가 부족합니다." }
    this.point -= amount
  }
}

enum class Role {
  USER,
  ADMIN
}
