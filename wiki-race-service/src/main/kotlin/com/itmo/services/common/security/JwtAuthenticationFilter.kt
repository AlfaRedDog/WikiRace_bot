package com.itmo.services.common.security

import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter
import javax.servlet.FilterChain
import javax.servlet.http.HttpServletRequest
import javax.servlet.http.HttpServletResponse

@Component
class JwtAuthenticationFilter(): OncePerRequestFilter() {

    override fun doFilterInternal(request: HttpServletRequest,
                                  response: HttpServletResponse,
                                  filterChain: FilterChain) {
//        val token = retrieveToken(request)
//        if (token == null) {
//            filterChain.doFilter(request, response)
//            return
//        }
//        kotlin.runCatching { tokenManager.readAccessToken(token) }
//                .onSuccess { user -> SecurityContextHolder.getContext().authentication =
//                        UsernamePasswordAuthenticationToken(user, token, user.authorities) }
//        filterChain.doFilter(request, response)
    }
}