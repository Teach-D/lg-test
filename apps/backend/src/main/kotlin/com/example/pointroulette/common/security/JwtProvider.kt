package com.example.pointroulette.common.security

import com.example.pointroulette.user.entity.Role
import io.jsonwebtoken.Claims
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Component
import java.util.*

@Component
class JwtProvider(
  @Value("\${jwt.secret}") private val secret: String,
  @Value("\${jwt.expiration}") private val expiration: Long,
) {

  private val key by lazy { Keys.hmacShaKeyFor(secret.toByteArray()) }

  /**
   * JWT 토큰 생성
   */
  fun generateToken(userId: Long, email: String, role: Role): String {
    val now = Date()
    val expiryDate = Date(now.time + expiration)

    return Jwts.builder()
      .subject(userId.toString())
      .claim("email", email)
      .claim("role", role.name)
      .issuedAt(now)
      .expiration(expiryDate)
      .signWith(key)
      .compact()
  }

  /**
   * 토큰 검증
   */
  fun validateToken(token: String): Boolean {
    return try {
      Jwts.parser()
        .verifyWith(key)
        .build()
        .parseSignedClaims(token)
      true
    } catch (e: Exception) {
      false
    }
  }

  /**
   * 토큰에서 사용자 ID 추출
   */
  fun getUserIdFromToken(token: String): Long {
    val claims = getClaims(token)
    return claims.subject.toLong()
  }

  /**
   * 토큰에서 이메일 추출
   */
  fun getEmailFromToken(token: String): String {
    val claims = getClaims(token)
    return claims["email"] as String
  }

  /**
   * 토큰에서 권한 추출
   */
  fun getRoleFromToken(token: String): Role {
    val claims = getClaims(token)
    val roleString = claims["role"] as String
    return Role.valueOf(roleString)
  }

  private fun getClaims(token: String): Claims {
    return Jwts.parser()
      .verifyWith(key)
      .build()
      .parseSignedClaims(token)
      .payload
  }
}
