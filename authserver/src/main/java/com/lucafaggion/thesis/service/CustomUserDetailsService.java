package com.lucafaggion.thesis.service;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

// import com.lucafaggion.thesis.common.model.CustomUserDetails;
import com.lucafaggion.thesis.common.model.User;
import com.lucafaggion.thesis.model.CustomUserDetails;
import com.lucafaggion.thesis.repository.UserRepository;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

	private final UserRepository userRepository;
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByUsername(username).orElseThrow(() -> new UsernameNotFoundException("User not found"));
	  return new CustomUserDetails(user);
	}

}