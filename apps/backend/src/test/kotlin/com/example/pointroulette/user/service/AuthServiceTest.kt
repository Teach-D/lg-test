package com.example.pointroulette.user.service

import com.example.pointroulette.common.security.JwtProvider
import com.example.pointroulette.user.dto.LoginRequest
import com.example.pointroulette.user.dto.RegisterRequest
import com.example.pointroulette.user.entity.Role
import com.example.pointroulette.user.entity.User
import com.example.pointroulette.user.exception.DuplicateEmailException
import com.example.pointroulette.user.exception.InvalidCredentialsException
import com.example.pointroulette.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.springframework.security.crypto.password.PasswordEncoder
import java.util.*

class AuthServiceTest : BehaviorSpec({

  val userRepository = mockk<UserRepository>()
  val passwordEncoder = mockk<PasswordEncoder>()
  val jwtProvider = mockk<JwtProvider>()
  val authService = AuthService(userRepository, passwordEncoder, jwtProvider)

  Given("회원가입 요청이 주어지고") {
    val request = RegisterRequest(
      email = "test@example.com",
      password = "password123",
      nickname = "테스트유저"
    )

    When("이메일이 중복되지 않으면") {
      every { userRepository.existsByEmail(request.email) } returns false
      every { passwordEncoder.encode(request.password) } returns "encodedPassword"
      every { userRepository.save(any()) } answers {
        val user = firstArg<User>()
        User(
          email = user.email,
          password = user.password,
          nickname = user.nickname,
          point = user.point,
          role = user.role
        ).apply {
          // Simulate saved entity with ID
        }
      }
      every { jwtProvider.generateToken(any(), any(), any()) } returns "jwt.token.here"

      Then("회원가입에 성공하고 1000 포인트를 지급한다") {
        val response = authService.register(request)

        response.user.email shouldBe request.email
        response.user.nickname shouldBe request.nickname
        response.user.point shouldBe 1000
        response.user.role shouldBe Role.USER
        response.accessToken shouldNotBe null

        verify { userRepository.save(any()) }
        verify { passwordEncoder.encode(request.password) }
      }
    }

    When("이메일이 중복되면") {
      every { userRepository.existsByEmail(request.email) } returns true

      Then("DuplicateEmailException을 던진다") {
        shouldThrow<DuplicateEmailException> {
          authService.register(request)
        }
      }
    }
  }

  Given("로그인 요청이 주어지고") {
    val request = LoginRequest(
      email = "test@example.com",
      password = "password123"
    )

    When("이메일과 비밀번호가 올바르면") {
      val user = User(
        email = request.email,
        password = "encodedPassword",
        nickname = "테스트유저",
        point = 1500,
        role = Role.USER
      )

      every { userRepository.findByEmail(request.email) } returns Optional.of(user)
      every { passwordEncoder.matches(request.password, user.password) } returns true
      every { jwtProvider.generateToken(any(), any(), any()) } returns "jwt.token.here"

      Then("로그인에 성공하고 JWT 토큰을 발급한다") {
        val response = authService.login(request)

        response.user.email shouldBe request.email
        response.user.point shouldBe 1500
        response.accessToken shouldNotBe null
      }
    }

    When("이메일이 존재하지 않으면") {
      every { userRepository.findByEmail(request.email) } returns Optional.empty()

      Then("InvalidCredentialsException을 던진다") {
        shouldThrow<InvalidCredentialsException> {
          authService.login(request)
        }
      }
    }

    When("비밀번호가 일치하지 않으면") {
      val user = User(
        email = request.email,
        password = "encodedPassword",
        nickname = "테스트유저",
        point = 1500
      )

      every { userRepository.findByEmail(request.email) } returns Optional.of(user)
      every { passwordEncoder.matches(request.password, user.password) } returns false

      Then("InvalidCredentialsException을 던진다") {
        shouldThrow<InvalidCredentialsException> {
          authService.login(request)
        }
      }
    }
  }
})
