package com.example.pointroulette.order.exception

import com.example.pointroulette.common.exception.BusinessException

class ProductOutOfStockException(productName: String) :
  BusinessException("ORDER_STOCK", "'$productName' 상품의 재고가 부족합니다.")

class InvalidStatusTransitionException(from: String, to: String) :
  BusinessException("ORDER_STATUS", "$from 에서 $to 로 변경할 수 없습니다.")