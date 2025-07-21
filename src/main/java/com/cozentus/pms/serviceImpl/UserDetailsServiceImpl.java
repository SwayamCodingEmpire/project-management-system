package com.cozentus.pms.serviceImpl;

import java.util.List;

import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.cozentus.pms.entites.Credential;
import com.cozentus.pms.repositories.CredentialRepository;
@Service
public class UserDetailsServiceImpl implements UserDetailsService {
	private final CredentialRepository credentialRepository;
	
	public UserDetailsServiceImpl(CredentialRepository credentialRepository) {
		this.credentialRepository = credentialRepository;
	}
	
	@Override
//	@Cacheable(value = "authCache", key = "#username")
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Credential credentials = credentialRepository.findByUsernameAndEnabledTrue(username)
				.orElseThrow(()-> new UsernameNotFoundException("User not found with email: " + username));
		
		return new User(
				credentials.getUsername(), 
				credentials.getPassword(), 
				credentials.getEnabled(),
				credentials.getEnabled(),
				credentials.getEnabled(),
				credentials.getEnabled(),
				List.of(new SimpleGrantedAuthority("ROLE_" + credentials.getRole().name()))
				);
	}


}
