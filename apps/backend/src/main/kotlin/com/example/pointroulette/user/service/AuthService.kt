package com.example.pointroulette.user.service

import com.example.pointroulette.common.security.JwtProvider
import com.example.pointroulette.user.dto.LoginRequest
import com.example.pointroulette.user.dto.LoginResponse
import com.example.pointroulette.user.dto.RegisterRequest
import com.example.pointroulette.user.dto.UserResponse
import com.example.pointroulette.user.entity.User
import com.example.pointroulette.user.exception.DuplicateEmailException
import com.example.pointroulette.user.exception.InvalidCredentialsException
import com.example.pointroulette.user.repository.UserRepository
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

private val logger = KotlinLogging.logger {}

@Service
class AuthService(
  private val userRepository: UserRepository,
  private val passwordEncoder: PasswordEncoder,
  private val jwtProvider: JwtProvider,
) {

  /**
   * 회원가입
   */
  @Transactional
  fun register(request: RegisterRequest): LoginResponse {
    if (userRepository.existsByEmail(request.email)) {
      throw DuplicateEmailException(request.email)
    }

    val user = User(
      email = request.email,
      password = passwordEncoder.encode(request.password),
      nickname = request.nickname,
      point = 1000,
    )

    val savedUser = userRepository.save(user)
    logger.info { "User registered: email=${savedUser.email}, id=${savedUser.id}" }

    val token = jwtProvider.generateToken(
      userId = savedUser.id,
      email = savedUser.email,
      role = savedUser.role,
    )

    return LoginResponse(
      accessToken = token,
      user = UserResponse.from(savedUser),
    )
  }

  /**
   * 로그인
   */
  @Transactional(readOnly = true)
  fun login(request: LoginRequest): LoginResponse {
    val user = userRepository.findByEmail(request.email)
      .orElseThrow { InvalidCredentialsException() }

    if (!passwordEncoder.matches(request.password, user.password)) {
      throw InvalidCredentialsException()
    }

    logger.info { "User logged in: email=${user.email}, id=${user.id}" }

    val token = jwtProvider.generateToken(
      userId = user.id,
      email = user.email,
      role = user.role,
    )

    return LoginResponse(
      accessToken = token,
      user = UserResponse.from(user),
    )
  }
}
