package com.ecommerce.project.util;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;

@Component
public class AuthUtil {

	@Autowired
	UserRepository userRepository;

	public String loggedInEmail() {

		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.findByUserName(authentication.getName()).orElseThrow(
				() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
		return user.getEmail();

	}

	public User loggedInUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.findByUserName(authentication.getName()).orElseThrow(
				() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
		return user;

	}

	public Long loggedInUserId() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		User user = userRepository.findByUserName(authentication.getName()).orElseThrow(
				() -> new UsernameNotFoundException("User Not Found with username: " + authentication.getName()));
		return user.getUserId();
	}
}
