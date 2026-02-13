package com.example.pointroulette.common.security

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource

@Configuration
@EnableWebSecurity
class SecurityConfig(
  private val jwtAuthenticationFilter: JwtAuthenticationFilter,
) {

  @Bean
  fun securityFilterChain(http: HttpSecurity): SecurityFilterChain {
    http
      .cors { it.configurationSource(corsConfigurationSource()) }
      .csrf { it.disable() }
      .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
      .authorizeHttpRequests { auth ->
        auth
          .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
          .requestMatchers("/api/auth/**").permitAll()
          .requestMatchers("/api/admin/**").hasRole("ADMIN")
          .requestMatchers("/api/**").authenticated()
          .anyRequest().authenticated()
      }
      .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter::class.java)
    return http.build()
  }

  @Bean
  fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

  @Bean
  fun corsConfigurationSource(): CorsConfigurationSource {
    val config = CorsConfiguration().apply {
      allowedOriginPatterns = listOf(
        "http://localhost:*",
        "https://point-roulette-*.vercel.app",
      )
      allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "OPTIONS")
      allowedHeaders = listOf("*")
      allowCredentials = true
    }
    return UrlBasedCorsConfigurationSource().apply {
      registerCorsConfiguration("/**", config)
    }
  }
}
