package com.cozentus.pms.dto;

public record ZohoAuthResponseDTO(
	    String access_token,
	    String scope,
	    String api_domain,
	    String token_type,
	    int expires_in
	) {}
