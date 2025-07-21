package com.cozentus.pms.config;

import org.springframework.security.web.authentication.WebAuthenticationDetails;

public record CustomAuthDetails(
		WebAuthenticationDetails webDetails, 
		UserAuthDetails userAuthDetails
		) {

}
