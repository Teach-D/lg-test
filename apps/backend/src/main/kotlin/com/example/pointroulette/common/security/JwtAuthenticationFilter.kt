package com.example.pointroulette.common.security

import io.github.oshai.kotlinlogging.KotlinLogging
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

private val logger = KotlinLogging.logger {}

@Component
class JwtAuthenticationFilter(
  private val jwtProvider: JwtProvider,
) : OncePerRequestFilter() {

  override fun doFilterInternal(
    request: HttpServletRequest,
    response: HttpServletResponse,
    filterChain: FilterChain
  ) {
    try {
      val token = extractToken(request)
      if (token != null && jwtProvider.validateToken(token)) {
        val userId = jwtProvider.getUserIdFromToken(token)
        val role = jwtProvider.getRoleFromToken(token)

        val authorities = listOf(SimpleGrantedAuthority("ROLE_${role.name}"))
        val authentication = UsernamePasswordAuthenticationToken(userId, null, authorities)
        authentication.details = WebAuthenticationDetailsSource().buildDetails(request)

        SecurityContextHolder.getContext().authentication = authentication
        logger.debug { "JWT authentication successful: userId=$userId, role=$role" }
      }
    } catch (e: Exception) {
      logger.warn { "JWT authentication failed: ${e.message}" }
    }

    filterChain.doFilter(request, response)
  }

  private fun extractToken(request: HttpServletRequest): String? {
    val bearerToken = request.getHeader("Authorization")
    return if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
      bearerToken.substring(7)
    } else {
      null
    }
  }
}
