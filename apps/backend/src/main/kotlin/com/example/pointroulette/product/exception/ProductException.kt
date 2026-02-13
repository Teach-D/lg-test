package com.example.pointroulette.product.exception

import com.example.pointroulette.common.exception.BusinessException

class ProductNotFoundException(id: Long) :
  BusinessException("PRODUCT_NOT_FOUND", "상품을 찾을 수 없습니다. (id=$id)")
