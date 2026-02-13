package com.example.pointroulette.point.service

import com.example.pointroulette.common.exception.BusinessException
import com.example.pointroulette.point.dto.PointSummaryResponse
import com.example.pointroulette.point.entity.PointHistory
import com.example.pointroulette.point.entity.PointType
import com.example.pointroulette.point.exception.InsufficientPointException
import com.example.pointroulette.point.repository.PointHistoryRepository
import com.example.pointroulette.user.repository.UserRepository
import org.springframework.data.domain.PageRequest
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
class PointService(
  private val pointHistoryRepository: PointHistoryRepository,
  private val userRepository: UserRepository,
) {

  /**
   * 사용자의 현재 포인트와 최근 이력 10건을 조회한다
   */
  @Transactional(readOnly = true)
  fun getMyPointSummary(userId: Long): PointSummaryResponse {
    val user = userRepository.findById(userId)
      .orElseThrow { BusinessException("USER_001", "사용자를 찾을 수 없습니다") }

    val histories = pointHistoryRepository.findByUserIdOrderByCreatedAtDesc(
      userId = userId,
      pageable = PageRequest.of(0, 10)
    ).content

    return PointSummaryResponse.of(
      currentPoint = user.point,
      histories = histories,
    )
  }

  /**
   * 포인트 이력을 추가하고 사용자 포인트를 업데이트한다
   */
  @Transactional
  fun addPointHistory(userId: Long, amount: Int, type: PointType, description: String) {
    val user = userRepository.findById(userId)
      .orElseThrow { BusinessException("USER_001", "사용자를 찾을 수 없습니다") }

    // 포인트 차감인 경우 잔액 확인
    if (amount < 0 && user.point < -amount) {
      throw InsufficientPointException()
    }

    // 포인트 업데이트
    if (amount > 0) {
      user.addPoint(amount)
    } else {
      user.deductPoint(-amount)
    }

    // 이력 저장
    val history = PointHistory(
      userId = userId,
      amount = amount,
      type = type,
      description = description,
    )
    pointHistoryRepository.save(history)
  }
}
