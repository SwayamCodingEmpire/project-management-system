package com.cozentus.pms.dto;

import java.util.List;
import java.util.Map;

public record ZohoResponseDTO(
	    List<Map<String, List<ZohoEmployeeDTO>>> result,
	    String message,
	     String uri,
	    int status
		) {

}
