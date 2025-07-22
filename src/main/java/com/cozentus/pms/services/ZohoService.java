package com.cozentus.pms.services;

import java.util.Map;
import java.util.Set;

import org.apache.commons.lang3.tuple.Pair;

import com.cozentus.pms.dto.ZohoEmployeeDTO;

public interface ZohoService {
	Pair<Map<String, ZohoEmployeeDTO>, Set<String>> fetchDataAllEmployeeDataFromZoho();

}
