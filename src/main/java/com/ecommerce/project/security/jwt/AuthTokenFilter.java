package com.ecommerce.project.security.jwt;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import com.ecommerce.project.security.services.UserDetailsServiceImpl;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class AuthTokenFilter extends OncePerRequestFilter {

	private static final Logger logger = LoggerFactory.getLogger(AuthTokenFilter.class);

	@Autowired
	private JwtUtils jwtUtils;
	@Autowired
	private UserDetailsServiceImpl userDetailsService;

	public AuthTokenFilter(JwtUtils jwtUtils, UserDetailsServiceImpl userDetailsService) {
		super();
		this.jwtUtils = jwtUtils;
		this.userDetailsService = userDetailsService;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
			throws ServletException, IOException {
//		  logger.debug("---- FILTER START ------------------------------------------------");

//		    logger.debug("---- FILTER CONTINUE ------------------------------------------------");

		logger.debug("AuthTokenFilter called for URI: {}", request.getRequestURI());

		try {
			String jwt = parseJwt(request);

			if (jwt != null && jwtUtils.validatejwtToken(jwt)) {
				String username = jwtUtils.getUserNamefromJWTToken(jwt);
				UserDetails userDetails = userDetailsService.loadUserByUsername(username);
				UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
						userDetails, null, userDetails.getAuthorities());
				logger.debug("Roles from JWT: {}", userDetails.getAuthorities());
				authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
				SecurityContextHolder.getContext().setAuthentication(authentication);
				logger.debug("Roles from jwt: {}", userDetails.getAuthorities());
			}
		} catch (Exception e) {
			logger.error("cannot set user authentication: {}", e);
		}

		filterChain.doFilter(request, response);
	}

//	private String parseJwt(HttpServletRequest request) {
////		String jwt = jwtUtils.getJwtFromHeader(request);
//		String jwt = jwtUtils.getJwtTokenFromCookies(request);
//		logger.debug("AuthTokenFilter.java: {}", jwt);
//		return jwt;
//	}

	private String parseJwt(HttpServletRequest request) {

		String jwtfromCookie = jwtUtils.getJwtTokenFromCookies(request);
		if (jwtfromCookie != null) {
			logger.debug("AuthTokenFilter.java: {}", jwtfromCookie);
			return jwtfromCookie;

		}
		String jwtFromHeader = jwtUtils.getJwtFromHeader(request);
		if (jwtFromHeader != null) {
			logger.debug("AuthTokenFilter.java: {}", jwtFromHeader);
			return jwtFromHeader;
		}

		return null;
	}

}
