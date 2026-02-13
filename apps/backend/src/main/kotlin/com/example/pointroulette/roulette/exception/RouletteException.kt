package com.example.pointroulette.roulette.exception

import com.example.pointroulette.common.exception.BusinessException

class DailyLimitExceededException : BusinessException(
  code = "ROULETTE_001",
  message = "오늘 이미 룰렛에 참여하셨습니다.",
)

class BudgetExceededException : BusinessException(
  code = "ROULETTE_002",
  message = "오늘 예산이 소진되었습니다.",
)

class SpinNotFoundException(id: Long) : BusinessException(
  code = "ROULETTE_003",
  message = "스핀 이력을 찾을 수 없습니다. (id=$id)",
)

class SpinAlreadyCancelledException(id: Long) : BusinessException(
  code = "ROULETTE_004",
  message = "이미 취소된 스핀입니다. (id=$id)",
)
