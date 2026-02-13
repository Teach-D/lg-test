package com.example.pointroulette.user.service

import com.example.pointroulette.user.dto.UserResponse
import com.example.pointroulette.user.exception.UserNotFoundException
import com.example.pointroulette.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class UserService(
  private val userRepository: UserRepository,
) {

  /**
   * 사용자 정보 조회
   */
  @Transactional(readOnly = true)
  fun getUserById(userId: Long): UserResponse {
    val user = userRepository.findById(userId)
      .orElseThrow { UserNotFoundException(userId) }

    return UserResponse.from(user)
  }
}
