package com.example.pointroulette.point.service

import com.example.pointroulette.common.exception.BusinessException
import com.example.pointroulette.point.entity.PointHistory
import com.example.pointroulette.point.entity.PointType
import com.example.pointroulette.point.exception.InsufficientPointException
import com.example.pointroulette.point.repository.PointHistoryRepository
import com.example.pointroulette.user.entity.Role
import com.example.pointroulette.user.entity.User
import com.example.pointroulette.user.repository.UserRepository
import io.kotest.assertions.throwables.shouldThrow
import io.kotest.core.spec.style.BehaviorSpec
import io.kotest.matchers.shouldBe
import io.mockk.every
import io.mockk.mockk
import io.mockk.slot
import io.mockk.verify
import org.springframework.data.domain.PageImpl
import org.springframework.data.domain.Pageable
import java.time.LocalDateTime
import java.util.*

class PointServiceTest : BehaviorSpec({

  val pointHistoryRepository = mockk<PointHistoryRepository>()
  val userRepository = mockk<UserRepository>()
  val pointService = PointService(pointHistoryRepository, userRepository)

  Given("사용자가 존재하고") {
    val userId = 1L
    val user = User(
      email = "test@example.com",
      password = "password",
      nickname = "테스트유저",
      point = 1500,
      role = Role.USER
    )

    When("포인트 요약을 조회하면") {
      val history1 = PointHistory(
        userId = userId,
        amount = 1000,
        type = PointType.SIGNUP_BONUS,
        description = "회원가입 보너스"
      )
      val history2 = PointHistory(
        userId = userId,
        amount = 500,
        type = PointType.ROULETTE_WIN,
        description = "룰렛 당첨"
      )

      every { userRepository.findById(userId) } returns Optional.of(user)
      every {
        pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, any())
      } returns PageImpl(listOf(history1, history2))
      every {
        pointHistoryRepository.sumExpiringPoints(userId, any(), any())
      } returns 500

      Then("현재 포인트와 이력 목록을 반환한다") {
        val summary = pointService.getMyPointSummary(userId)

        summary.currentPoint shouldBe 1500
        summary.expiringPointIn7Days shouldBe 500
        summary.histories.size shouldBe 2
        summary.histories[0].amount shouldBe 1000
        summary.histories[0].type shouldBe PointType.SIGNUP_BONUS
        summary.histories[1].amount shouldBe 500

        verify { userRepository.findById(userId) }
        verify { pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(userId, any()) }
        verify { pointHistoryRepository.sumExpiringPoints(userId, any(), any()) }
      }
    }

    When("존재하지 않는 사용자의 포인트를 조회하면") {
      every { userRepository.findById(999L) } returns Optional.empty()

      Then("BusinessException을 던진다") {
        shouldThrow<BusinessException> {
          pointService.getMyPointSummary(999L)
        }
      }
    }
  }

  Given("포인트 적립 요청이 주어지고") {
    val userId = 1L
    val user = User(
      email = "test@example.com",
      password = "password",
      nickname = "테스트유저",
      point = 1000,
      role = Role.USER
    )

    When("양수 금액을 추가하면") {
      every { userRepository.findById(userId) } returns Optional.of(user)
      every { pointHistoryRepository.save(any()) } answers { firstArg() }

      Then("포인트가 증가하고 이력이 저장된다") {
        pointService.addPointHistory(
          userId = userId,
          amount = 500,
          type = PointType.ROULETTE_WIN,
          description = "룰렛 당첨"
        )

        user.point shouldBe 1500

        verify { userRepository.findById(userId) }
        verify { pointHistoryRepository.save(any()) }
      }
    }
  }

  Given("포인트 차감 요청이 주어지고") {
    val userId = 1L

    When("잔액이 충분하면") {
      val user = User(
        email = "test@example.com",
        password = "password",
        nickname = "테스트유저",
        point = 1000,
        role = Role.USER
      )

      every { userRepository.findById(userId) } returns Optional.of(user)
      every { pointHistoryRepository.save(any()) } answers { firstArg() }

      Then("포인트가 차감되고 이력이 저장된다") {
        pointService.addPointHistory(
          userId = userId,
          amount = -300,
          type = PointType.PRODUCT_EXCHANGE,
          description = "상품 교환"
        )

        user.point shouldBe 700

        verify { userRepository.findById(userId) }
        verify { pointHistoryRepository.save(any()) }
      }
    }

    When("잔액이 부족하면") {
      val user = User(
        email = "test@example.com",
        password = "password",
        nickname = "테스트유저",
        point = 100,
        role = Role.USER
      )

      every { userRepository.findById(userId) } returns Optional.of(user)

      Then("InsufficientPointException을 던진다") {
        shouldThrow<InsufficientPointException> {
          pointService.addPointHistory(
            userId = userId,
            amount = -300,
            type = PointType.PRODUCT_EXCHANGE,
            description = "상품 교환"
          )
        }

        user.point shouldBe 100 // 포인트 변경되지 않음
      }
    }
  }

  Given("포인트 이력 저장 시") {
    val userId = 1L
    val user = User(
      email = "test@example.com",
      password = "password",
      nickname = "테스트유저",
      point = 1000,
      role = Role.USER
    )

    When("관리자가 포인트를 조정하면") {
      every { userRepository.findById(userId) } returns Optional.of(user)
      val historySlot = slot<PointHistory>()
      every { pointHistoryRepository.save(capture(historySlot)) } answers { firstArg() }

      Then("이력에 정확한 정보가 기록된다") {
        pointService.addPointHistory(
          userId = userId,
          amount = 200,
          type = PointType.ADMIN_ADJUST,
          description = "관리자 지급"
        )

        val savedHistory = historySlot.captured
        savedHistory.userId shouldBe userId
        savedHistory.amount shouldBe 200
        savedHistory.type shouldBe PointType.ADMIN_ADJUST
        savedHistory.description shouldBe "관리자 지급"

        verify { pointHistoryRepository.save(any()) }
      }
    }
  }
})
