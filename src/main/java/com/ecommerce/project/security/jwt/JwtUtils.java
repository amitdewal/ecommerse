package com.ecommerce.project.security.jwt;

import java.security.Key;
import java.util.Date;

import javax.crypto.SecretKey;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import com.ecommerce.project.security.services.UserDetailsImpl;

import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;

@Component
public class JwtUtils {
	private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);
	
	@Value("${spring.app.jwtSecret}")
	private String jwtSecret;


	@Value("${spring.app.jwtExpirationMs}")
	private int jwtExpirationMs;
	@Value("${spring.ecom.app.jwtCookieName}")
	private String jwtCookie;
	
	// getting JWT from Header
	  public String getJwtFromHeader(HttpServletRequest request) {
	        String bearerToken = request.getHeader("authorization");
	        System.out.println(bearerToken+"00000000000000000000000000000");
	        logger.debug("Authorization Header: {}", bearerToken);
	        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
	            return bearerToken.substring(7); // Remove Bearer prefix
	        }
	        return null;
	    }
	
	public String getJwtTokenFromCookies(HttpServletRequest request) {
		Cookie cookie = WebUtils.getCookie(request, jwtCookie);
		if (cookie != null) {
			logger.debug("COOKIE: {}", cookie.getValue());
			return cookie.getValue();

		} else {
			return null;
		}
	}
	
	


	public ResponseCookie generateJwtCookie(UserDetailsImpl userPrinicial) {
		String jwt = genrateTokenfromUsername(userPrinicial.getUsername());
		ResponseCookie cookie = ResponseCookie.from(jwtCookie, jwt).path("/api").maxAge(24 * 60 * 60).httpOnly(false)
				.build();
		return cookie;

	}
	
	
	public ResponseCookie getCleanJwtCookie() {
		ResponseCookie cookie = ResponseCookie.from(jwtCookie, null).path("/api").build();
		return cookie;

	}
	
	
	// generating token from username

	public String genrateTokenfromUsername(String username) {
//		String username = userDetails.getUsername();

		 return Jwts.builder()
	                .subject(username)
	                .issuedAt(new Date())
	                .expiration(new Date((new Date()).getTime() + jwtExpirationMs))
	                .signWith(key())
	                .compact();

	}

	// getting username from jwt token
	public String getUserNamefromJWTToken(String token) {
		return Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(token).getPayload().getSubject();

	}

	// generate signing key

	 private Key key() {
	        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtSecret));
	    }

	// validate jwt token

	public Boolean validatejwtToken(String authToken) {
		try {
			System.out.println("Validate");
			Jwts.parser().verifyWith((SecretKey) key()).build().parseSignedClaims(authToken);
			return true;
		} catch (MalformedJwtException exception) {
			logger.error("Invalid JWT token: {}", exception.getMessage());

		} catch (ExpiredJwtException exception) {
			logger.error("JWT token expired: {}", exception.getMessage());
		} catch (UnsupportedJwtException exception) {
			logger.error("JWT token unsupported: {}", exception.getMessage());
		} catch (IllegalArgumentException exception) {
			logger.error("JWT claims string is empty : {}", exception.getMessage());
		}
		return false;

	}

}
