package com.example.pointroulette.point.scheduler

import com.example.pointroulette.point.entity.PointHistory
import com.example.pointroulette.point.entity.PointType
import com.example.pointroulette.point.repository.PointHistoryRepository
import com.example.pointroulette.user.repository.UserRepository
import org.slf4j.LoggerFactory
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Component
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDateTime

@Component
class PointExpiryScheduler(
  private val pointHistoryRepository: PointHistoryRepository,
  private val userRepository: UserRepository,
) {

  private val log = LoggerFactory.getLogger(javaClass)

  /** 매일 자정에 만료 포인트를 처리한다 */
  @Scheduled(cron = "0 0 0 * * *")
  @Transactional
  fun processExpiredPoints() {
    val now = LocalDateTime.now()
    val expired = pointHistoryRepository.findByExpiresAtBeforeAndAmountGreaterThanAndTypeNot(
      expiresAt = now,
      amount = 0,
      type = PointType.EXPIRED,
    )

    if (expired.isEmpty()) return

    log.info("만료 포인트 처리 시작: ${expired.size}건")

    expired.groupBy { it.userId }.forEach { (userId, histories) ->
      val user = userRepository.findById(userId).orElse(null) ?: return@forEach

      val totalExpired = histories.sumOf { it.amount }
      val deductAmount = minOf(totalExpired, user.point)

      if (deductAmount > 0) {
        user.deductPoint(deductAmount)
        pointHistoryRepository.save(
          PointHistory(
            userId = userId,
            amount = -deductAmount,
            type = PointType.EXPIRED,
            description = "포인트 유효기간 만료",
          ),
        )
      }

      // 만료 처리된 내역의 expiresAt을 null로 변경하여 재처리 방지
      histories.forEach { it.expiresAt = null }

      log.info("사용자 $userId: 만료 ${totalExpired}P, 차감 ${deductAmount}P")
    }
  }
}
