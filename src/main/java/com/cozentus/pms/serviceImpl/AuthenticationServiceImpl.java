package com.cozentus.pms.serviceImpl;
import org.apache.commons.lang3.tuple.Pair;
import org.slf4j.Logger;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import com.cozentus.pms.config.CustomAuthDetails;
import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.LoginDTO;
import com.cozentus.pms.dto.LoginResponseDTO;
import com.cozentus.pms.dto.NameAndEmpId;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.JwtService;
@Service
public class AuthenticationServiceImpl implements AuthenticationService {

	private final Logger logger = org.slf4j.LoggerFactory.getLogger(AuthenticationServiceImpl.class);
	private final UserDetailsService userDetailsService;
	private final AuthenticationManager authenticationManager;
	private final UserInfoRepository userInfoRepository;
	private final JwtService jwtService;
	
	public AuthenticationServiceImpl(UserDetailsService userDetailsService,AuthenticationManager authenticationManager, JwtService jwtService, UserInfoRepository userInfoRepository) {
		this.userDetailsService = userDetailsService;
		this.authenticationManager = authenticationManager;
		this.jwtService = jwtService;
		this.userInfoRepository = userInfoRepository;
	}
	@Override
	public LoginResponseDTO authenticate(LoginDTO loginDTO) {
		logger.info("Authenticating user with email: {}", loginDTO.username());
		if (loginDTO.username() == null || loginDTO.password() == null) {
			throw new IllegalArgumentException("Email and password must not be null");
		}
		UsernamePasswordAuthenticationToken authToken = new UsernamePasswordAuthenticationToken(loginDTO.username(),loginDTO.password());
		authenticationManager.authenticate(authToken);
		UserDetails userDetails =  userDetailsService.loadUserByUsername(loginDTO.username());
        if (userDetails != null) {
            String jwtToken = jwtService.generateToken(userDetails);

            // Extract role from authorities
            String roleStr = userDetails.getAuthorities().stream()
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("No role assigned"))
                .getAuthority();

            // Convert string to enum
            NameAndEmpId nameAndEmpId = userInfoRepository.findNameByUsername(loginDTO.username())
				.orElseThrow(() -> new IllegalStateException("User not found"));
            Roles role = Roles.valueOf(roleStr.substring(5));

            LoginResponseDTO loginResponse = new LoginResponseDTO(jwtToken, role, nameAndEmpId.name(), nameAndEmpId.empId());
            return loginResponse;
        } else {
            throw new IllegalStateException("User is Invalid");
        }
	}
	@Override
	public Pair<Roles,UserAuthDetails> getCurrentUserDetails() {
		// TODO Auto-generated method stub
		
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		UserDetails userDetails = (UserDetails)authentication.getPrincipal();
		String roleStr = userDetails.getAuthorities().stream()
				.map(GrantedAuthority::getAuthority)
				.findFirst()
				.orElseThrow(() -> new IllegalStateException("No role assigned"));
		Roles role = Roles.valueOf(roleStr.substring(5));
    	CustomAuthDetails auth = (CustomAuthDetails)authentication.getDetails();
    	return Pair.of(role,auth.userAuthDetails());
    	
	}

}
