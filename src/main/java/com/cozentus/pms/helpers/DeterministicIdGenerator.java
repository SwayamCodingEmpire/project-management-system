package com.cozentus.pms.helpers;

import java.util.UUID;

import org.springframework.ai.document.id.IdGenerator;

public class DeterministicIdGenerator implements IdGenerator {

	@Override
	public String generateId(Object... contents) {
		// Ensure empId is passed as the first argument
		if (contents.length == 0 || contents[0] == null) {
			throw new IllegalArgumentException("empId is required to generate ID");
		}
		String empId = contents[0].toString();
		return UUID.nameUUIDFromBytes(empId.getBytes()).toString();
	}
}

