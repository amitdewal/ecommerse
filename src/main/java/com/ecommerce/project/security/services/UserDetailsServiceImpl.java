package com.ecommerce.project.security.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;
import com.ecommerce.project.model.User;
import com.ecommerce.project.repository.UserRepository;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import jakarta.transaction.Transactional;

@Service
public class UserDetailsServiceImpl  implements UserDetailsService{
	
	@Autowired
	UserRepository userRepository;

	@Override
	@Transactional
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUserName(username)
				.orElseThrow(()-> new  UsernameNotFoundException("User Not Found With username: "+username));
		
		
		
		return UserDetailsImpl.build(user);
	}

}
