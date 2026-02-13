package com.example.pointroulette.user.repository

import com.example.pointroulette.user.entity.User
import jakarta.persistence.LockModeType
import org.springframework.data.domain.Page
import org.springframework.data.domain.Pageable
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Lock
import org.springframework.data.jpa.repository.Query
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

  /** 포인트 변경 시 사용 — 비관적 락으로 Lost Update 방지 */
  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT u FROM User u WHERE u.id = :id")
  fun findByIdForUpdate(id: Long): Optional<User>
}
