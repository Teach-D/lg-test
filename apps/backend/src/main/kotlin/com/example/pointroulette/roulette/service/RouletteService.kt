package com.example.pointroulette.roulette.service

import com.example.pointroulette.common.exception.BusinessException
import com.example.pointroulette.point.entity.PointType
import com.example.pointroulette.point.service.PointService
import com.example.pointroulette.roulette.dto.RouletteConfigResponse
import com.example.pointroulette.roulette.dto.SegmentResponse
import com.example.pointroulette.roulette.dto.SpinResultResponse
import com.example.pointroulette.roulette.entity.SpinHistory
import com.example.pointroulette.roulette.repository.RouletteSegmentRepository
import com.example.pointroulette.roulette.repository.SpinHistoryRepository
import com.example.pointroulette.user.repository.UserRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.random.Random

private const val SPIN_COST = 100

@Service
@Transactional(readOnly = true)
class RouletteService(
  private val segmentRepository: RouletteSegmentRepository,
  private val spinHistoryRepository: SpinHistoryRepository,
  private val pointService: PointService,
  private val userRepository: UserRepository,
) {

  /** 룰렛 설정 조회 (세그먼트 목록 + 스핀 비용) */
  fun getConfig(): RouletteConfigResponse {
    val segments = segmentRepository.findAllByOrderByDisplayOrderAsc()
      .map(SegmentResponse::from)

    return RouletteConfigResponse(
      spinCost = SPIN_COST,
      segments = segments,
    )
  }

  /** 룰렛 스핀: 비용 차감 → 가중치 랜덤 당첨 → 보상 지급 */
  @Transactional
  fun spin(userId: Long): SpinResultResponse {
    val segments = segmentRepository.findAllByOrderByDisplayOrderAsc()

    if (segments.isEmpty()) {
      throw BusinessException("ROULETTE_NO_CONFIG", "룰렛이 설정되지 않았습니다.")
    }

    // 스핀 비용 차감
    pointService.addPointHistory(
      userId = userId,
      amount = -SPIN_COST,
      type = PointType.ROULETTE_WIN,
      description = "룰렛 스핀 비용",
    )

    // 가중치 기반 랜덤 선택
    val totalWeight = segments.sumOf { it.weight }
    var roll = Random.nextInt(totalWeight)
    var winnerIndex = 0

    for ((index, seg) in segments.withIndex()) {
      roll -= seg.weight
      if (roll < 0) {
        winnerIndex = index
        break
      }
    }

    val winner = segments[winnerIndex]

    // 당첨 포인트 지급 (0이 아닌 경우)
    if (winner.rewardPoint > 0) {
      pointService.addPointHistory(
        userId = userId,
        amount = winner.rewardPoint,
        type = PointType.ROULETTE_WIN,
        description = "룰렛 당첨: ${winner.label}",
      )
    }

    // 스핀 이력 저장
    spinHistoryRepository.save(
      SpinHistory(
        userId = userId,
        segmentId = winner.id,
        segmentLabel = winner.label,
        rewardPoint = winner.rewardPoint,
        costPoint = SPIN_COST,
      ),
    )

    val user = userRepository.findById(userId)
      .orElseThrow { BusinessException("USER_001", "사용자를 찾을 수 없습니다") }

    return SpinResultResponse(
      segmentId = winner.id,
      segmentLabel = winner.label,
      rewardPoint = winner.rewardPoint,
      segmentIndex = winnerIndex,
      remainingPoint = user.point,
    )
  }
}