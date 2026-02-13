package com.example.pointroulette.user.service

import com.example.pointroulette.user.dto.AdminUserResponse
import com.example.pointroulette.user.dto.UserResponse
import com.example.pointroulette.user.exception.UserNotFoundException
import com.example.pointroulette.user.repository.UserRepository
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
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

  /**
   * 관리자용 사용자 목록 페이징 조회.
   * search가 주어지면 이메일 또는 닉네임으로 부분 검색한다.
   */
  @Transactional(readOnly = true)
  fun getAdminUsers(page: Int, size: Int, search: String?): Page<AdminUserResponse> {
    val pageable = PageRequest.of(page, size, Sort.by(Sort.Direction.DESC, "createdAt"))

    return if (search.isNullOrBlank()) {
      userRepository.findAll(pageable)
    } else {
      userRepository.findByEmailContainingOrNicknameContaining(search, search, pageable)
    }.map { AdminUserResponse.from(it) }
  }
}
