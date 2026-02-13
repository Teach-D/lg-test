package com.example.pointroulette.user.service

import com.example.pointroulette.user.entity.Role
import com.example.pointroulette.user.entity.User
import com.example.pointroulette.user.exception.UserNotFoundException
import com.example.pointroulette.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.PageRequest
import java.util.*

class UserServiceTest : BehaviorSpec({

  val userRepository = mockk<UserRepository>()
  val userService = UserService(userRepository)

  Given("사용자 ID가 주어지고") {
    val userId = 1L
    val user = User(
      email = "user@example.com",
      password = "encoded",
      nickname = "테스터",
      point = 1000,
      role = Role.USER,
    )

    When("해당 ID의 사용자가 존재하면") {
      every { userRepository.findById(userId) } returns Optional.of(user)

      Then("UserResponse를 반환한다") {
        val response = userService.getUserById(userId)

        response.email shouldBe user.email
        response.nickname shouldBe user.nickname
        response.point shouldBe user.point
        response.role shouldBe Role.USER
      }
    }

    When("해당 ID의 사용자가 존재하지 않으면") {
      every { userRepository.findById(userId) } returns Optional.empty()

      Then("UserNotFoundException을 던진다") {
        shouldThrow<UserNotFoundException> {
          userService.getUserById(userId)
        }
      }
    }
  }

  Given("관리자가 사용자 목록을 조회할 때") {
    val users = listOf(
      User(email = "a@example.com", password = "enc", nickname = "알파", point = 500, role = Role.USER),
      User(email = "b@example.com", password = "enc", nickname = "베타", point = 1500, role = Role.ADMIN),
    )

    When("search 조건이 없으면") {
      val pageable = PageRequest.of(0, 10)
      every { userRepository.findAll(any<PageRequest>()) } returns PageImpl(users, pageable, users.size.toLong())

      Then("전체 사용자 목록을 반환한다") {
        val result = userService.getAdminUsers(0, 10, null)

        result.totalElements shouldBe 2
        result.content[0].email shouldBe "a@example.com"
        result.content[1].email shouldBe "b@example.com"
      }
    }

    When("search 조건이 빈 문자열이면") {
      val pageable = PageRequest.of(0, 10)
      every { userRepository.findAll(any<PageRequest>()) } returns PageImpl(users, pageable, users.size.toLong())

      Then("전체 사용자 목록을 반환한다") {
        val result = userService.getAdminUsers(0, 10, "")

        result.totalElements shouldBe 2
      }
    }

    When("search 조건이 주어지면") {
      val filtered = listOf(users[0])
      val pageable = PageRequest.of(0, 10)
      every {
        userRepository.findByEmailContainingOrNicknameContaining("알파", "알파", any())
      } returns PageImpl(filtered, pageable, filtered.size.toLong())

      Then("이메일 또는 닉네임으로 검색된 사용자 목록을 반환한다") {
        val result = userService.getAdminUsers(0, 10, "알파")

        result.totalElements shouldBe 1
        result.content[0].nickname shouldBe "알파"
      }
    }
  }
})
