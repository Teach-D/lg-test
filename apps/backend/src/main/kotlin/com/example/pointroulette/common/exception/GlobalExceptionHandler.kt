package com.example.pointroulette.common.exception

import com.example.pointroulette.common.dto.ApiResponse
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.MethodArgumentNotValidException
import org.springframework.web.bind.annotation.ExceptionHandler
import org.springframework.web.bind.annotation.RestControllerAdvice

private val logger = KotlinLogging.logger {}

@RestControllerAdvice
class GlobalExceptionHandler {

  @ExceptionHandler(BusinessException::class)
  fun handleBusiness(e: BusinessException): ResponseEntity<ApiResponse<Nothing>> {
    logger.warn { "BusinessException: [${e.code}] ${e.message}" }
    return ResponseEntity
      .badRequest()
      .body(ApiResponse.error(e.code, e.message))
  }

  @ExceptionHandler(MethodArgumentNotValidException::class)
  fun handleValidation(e: MethodArgumentNotValidException): ResponseEntity<ApiResponse<Nothing>> {
    val message = e.bindingResult.fieldErrors
      .joinToString(", ") { "${it.field}: ${it.defaultMessage}" }
    return ResponseEntity
      .badRequest()
      .body(ApiResponse.error("VALIDATION_ERROR", message))
  }

  @ExceptionHandler(Exception::class)
  fun handleUnexpected(e: Exception): ResponseEntity<ApiResponse<Nothing>> {
    logger.error(e) { "Unexpected error" }
    return ResponseEntity
      .status(HttpStatus.INTERNAL_SERVER_ERROR)
      .body(ApiResponse.error("INTERNAL_ERROR", "서버 오류가 발생했습니다."))
  }
}
