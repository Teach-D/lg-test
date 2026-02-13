package com.example.pointroulette.point.exception

import com.example.pointroulette.common.exception.BusinessException

class InsufficientPointException : BusinessException(
  code = "POINT_001",
  message = "포인트가 부족합니다",
)
