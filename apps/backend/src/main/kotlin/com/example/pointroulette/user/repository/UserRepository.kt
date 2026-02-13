package com.example.pointroulette.user.repository

import com.example.pointroulette.user.entity.User
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.util.*

@Repository
interface UserRepository : JpaRepository<User, Long> {

  fun findByEmail(email: String): Optional<User>

  fun existsByEmail(email: String): Boolean

  fun findByNickname(nickname: String): Optional<User>

  fun findByEmailContainingOrNicknameContaining(
    email: String,
    nickname: String,
    pageable: Pageable,
  ): Page<User>
}
