package com.example.pointroulette.roulette.service

import com.example.pointroulette.budget.service.BudgetService
import com.example.pointroulette.common.exception.BusinessException
import com.example.pointroulette.point.entity.PointType
import com.example.pointroulette.point.service.PointService
import com.example.pointroulette.roulette.dto.AdminSpinResponse
import com.example.pointroulette.roulette.dto.RouletteConfigResponse
import com.example.pointroulette.roulette.dto.RouletteStatusResponse
import com.example.pointroulette.roulette.dto.SegmentResponse
import com.example.pointroulette.roulette.dto.SpinResultResponse
import com.example.pointroulette.roulette.entity.SpinHistory
import com.example.pointroulette.roulette.exception.DailyLimitExceededException
import com.example.pointroulette.roulette.exception.SpinAlreadyCancelledException
import com.example.pointroulette.roulette.exception.SpinNotFoundException
import com.example.pointroulette.roulette.repository.RouletteSegmentRepository
import com.example.pointroulette.roulette.repository.SpinHistoryRepository
import com.example.pointroulette.user.repository.UserRepository
import org.springframework.dao.DataIntegrityViolationException
import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.LocalTime
import kotlin.random.Random

@Service
@Transactional(readOnly = true)
class RouletteService(
  private val segmentRepository: RouletteSegmentRepository,
  private val spinHistoryRepository: SpinHistoryRepository,
  private val pointService: PointService,
  private val userRepository: UserRepository,
  private val budgetService: BudgetService,
) {

  /** 룰렛 설정 조회 (세그먼트 목록 + 스핀 비용) */
  fun getConfig(): RouletteConfigResponse {
    val segments = segmentRepository.findAllByOrderByDisplayOrderAsc()
      .map(SegmentResponse::from)

    return RouletteConfigResponse(
      spinCost = 0,
      segments = segments,
    )
  }

  /** 오늘 스핀 여부 + 잔여 예산 조회 */
  fun getStatus(userId: Long): RouletteStatusResponse {
    val today = LocalDate.now()
    val hasSpun = spinHistoryRepository.existsByUserIdAndCancelledFalseAndCreatedAtBetween(
      userId,
      today.atStartOfDay(),
      today.atTime(LocalTime.MAX),
    )

    return RouletteStatusResponse(
      hasSpunToday = hasSpun,
      dailyBudgetRemaining = budgetService.getDailyRemaining(),
      spinCost = 0,
    )
  }

  /** 룰렛 스핀: 1일 1회 체크 → 스핀 이력 저장(유니크 보장) → 예산 소비 → 포인트 지급 */
  @Transactional
  fun spin(userId: Long): SpinResultResponse {
    // 1일 1회 빠른 체크 (대부분의 중복 요청을 여기서 걸러냄)
    val today = LocalDate.now()
    val alreadySpun = spinHistoryRepository.existsByUserIdAndCancelledFalseAndCreatedAtBetween(
      userId,
      today.atStartOfDay(),
      today.atTime(LocalTime.MAX),
    )
    if (alreadySpun) {
      throw DailyLimitExceededException()
    }

    // 100~1000p 랜덤 보상
    val rewardPoint = Random.nextInt(100, 1001)

    // 스핀 이력 먼저 저장 — DB 유니크 인덱스가 동시 중복 참여를 차단
    try {
      spinHistoryRepository.saveAndFlush(
        SpinHistory(
          userId = userId,
          segmentId = 0,
          segmentLabel = "랜덤 보상",
          rewardPoint = rewardPoint,
          costPoint = 0,
        ),
      )
    } catch (e: DataIntegrityViolationException) {
      throw DailyLimitExceededException()
    }

    // 예산 체크 + 소비 (원자적 UPDATE)
    budgetService.checkAndSpendDailyBudget(rewardPoint)

    // 포인트 지급 (비관적 락으로 Lost Update 방지)
    pointService.addPointHistory(
      userId = userId,
      amount = rewardPoint,
      type = PointType.ROULETTE_WIN,
      description = "룰렛 당첨: ${rewardPoint}p",
    )

    val user = userRepository.findById(userId)
      .orElseThrow { BusinessException("USER_001", "사용자를 찾을 수 없습니다") }

    return SpinResultResponse(
      rewardPoint = rewardPoint,
      remainingPoint = user.point,
    )
  }

  /** [관리자] 스핀 이력 목록 조회 */
  fun getSpinHistory(page: Int, size: Int): Page<AdminSpinResponse> {
    val pageable = PageRequest.of(page, size)
    return spinHistoryRepository.findAllByOrderByCreatedAtDesc(pageable)
      .map(AdminSpinResponse::from)
  }

  /** [관리자] 스핀 취소 — 포인트 회수 + 예산 복원 */
  @Transactional
  fun cancelSpin(spinId: Long) {
    val spin = spinHistoryRepository.findById(spinId)
      .orElseThrow { SpinNotFoundException(spinId) }

    if (spin.cancelled) {
      throw SpinAlreadyCancelledException(spinId)
    }

    spin.cancel()

    // 당첨 포인트 회수
    if (spin.rewardPoint > 0) {
      pointService.addPointHistory(
        userId = spin.userId,
        amount = -spin.rewardPoint,
        type = PointType.ADMIN_ADJUST,
        description = "룰렛 참여 취소 (관리자): 포인트 회수",
      )

      // 예산 복원
      budgetService.restoreDailyBudget(spin.rewardPoint, spin.createdAt.toLocalDate())
    }

    // 스핀 비용 환불 (비용이 있는 경우만)
    if (spin.costPoint > 0) {
      pointService.addPointHistory(
        userId = spin.userId,
        amount = spin.costPoint,
        type = PointType.ADMIN_ADJUST,
        description = "룰렛 참여 취소 (관리자): 비용 환불",
      )
    }
  }
}