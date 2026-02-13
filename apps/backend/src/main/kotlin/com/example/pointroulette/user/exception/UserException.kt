package com.example.pointroulette.user.exception

import com.example.pointroulette.common.exception.BusinessException

class DuplicateEmailException(email: String) : BusinessException(
  code = "USER_001",
  message = "이미 사용 중인 이메일입니다: $email"
)

class InvalidCredentialsException : BusinessException(
  code = "USER_002",
  message = "이메일 또는 비밀번호가 올바르지 않습니다"
)

class UserNotFoundException(userId: Long) : BusinessException(
  code = "USER_003",
  message = "사용자를 찾을 수 없습니다: $userId"
)
