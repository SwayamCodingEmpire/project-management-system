package com.cozentus.pms.dto;

public record ProjectResourceSummaryCountDTO(
		  String code,
		    String name,
		    String customer,
		    Long resourceCount,
		    String empId
		) {

}
