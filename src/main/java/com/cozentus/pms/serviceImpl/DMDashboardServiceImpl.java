package com.cozentus.pms.serviceImpl;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Service;

import com.cozentus.pms.config.UserAuthDetails;
import com.cozentus.pms.dto.DMResourceStatsDTO;
import com.cozentus.pms.dto.DMResourceStatsPartialDTO;
import com.cozentus.pms.dto.ResourceProjectUtilizationSummaryDTO;
import com.cozentus.pms.dto.UtilizationBreakdownDTO;
import com.cozentus.pms.helpers.Roles;
import com.cozentus.pms.repositories.ResourceAllocationRepository;
import com.cozentus.pms.repositories.UserInfoRepository;
import com.cozentus.pms.services.AuthenticationService;
import com.cozentus.pms.services.DMDashboardService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class DMDashboardServiceImpl implements DMDashboardService {
	
	private final UserInfoRepository userInfoRepository;
	private final ResourceAllocationRepository resourceAllocationRepository;
	private final AuthenticationService authenticationService;
	
	public DMDashboardServiceImpl(UserInfoRepository userInfoRepository, ResourceAllocationRepository resourceAllocationRepository, AuthenticationService authenticationService) {
		this.userInfoRepository = userInfoRepository;
		this.resourceAllocationRepository = resourceAllocationRepository;
		this.authenticationService = authenticationService;
	}
	
	public DMResourceStatsDTO getResourceBillabilityStats() {
		Pair<Roles, UserAuthDetails> userAuthDetails = authenticationService.getCurrentUserDetails();
		String dmEmpId = userAuthDetails.getRight().empId();
		if (userAuthDetails.getLeft().equals(Roles.DELIVERY_MANAGER)) {
			return userInfoRepository.getResourceStatsCombined(Roles.RESOURCE, dmEmpId);
		}
		String empId = userAuthDetails.getRight().empId();
		return userInfoRepository.getResourceStatsCombinedForPM(Roles.RESOURCE,empId);
	}
	
	
	
	public UtilizationBreakdownDTO computeUtilizationBreakdown() {
		Pair<Roles, UserAuthDetails> userAuthDetails = authenticationService.getCurrentUserDetails();
	    List<ResourceProjectUtilizationSummaryDTO> rows;
	    if (userAuthDetails.getLeft().equals(Roles.DELIVERY_MANAGER)) {
	    	String dmEmpId = userAuthDetails.getRight().empId();
	        rows = resourceAllocationRepository.findResourceUtilizationSummaryForDM(Roles.RESOURCE, dmEmpId);
	    }
	    else {
	    	rows = resourceAllocationRepository.findResourceUtilizationSummaryForPm(Roles.RESOURCE, userAuthDetails.getRight().empId());
	    }
	    		
	    Map<Boolean, List<ResourceProjectUtilizationSummaryDTO>> grouped = rows.stream()
	        .filter(r -> r.projectCode() != null)
	        .collect(Collectors.groupingBy(ResourceProjectUtilizationSummaryDTO::isCustomerProject));

	    // true = customer, false = internal
	    return new UtilizationBreakdownDTO(
	        computeNormalizedPlannedUtil(grouped.get(true)),
	        computeNormalizedActualUtil(grouped.get(true)),
	        computeNormalizedPlannedUtil(grouped.get(false)),
	        computeNormalizedActualUtil(grouped.get(false))
	    );
	}
	
	private BigDecimal computeNormalizedPlannedUtil(List<ResourceProjectUtilizationSummaryDTO> resourceProjectUtilisationSummaryDTO) {
	    if (resourceProjectUtilisationSummaryDTO == null) return BigDecimal.ZERO;

	    return resourceProjectUtilisationSummaryDTO.stream()
	        .map(r -> {
	            BigDecimal planned = defaultZero(r.plannedHours());
	            BigDecimal daily = defaultZero(r.dailyWorkingHours());
	            if (daily.compareTo(BigDecimal.ZERO) <= 0) return BigDecimal.ZERO;
	            return planned.divide(daily, 4, RoundingMode.HALF_UP).multiply(BigDecimal.valueOf(100));
	        })
	        .reduce(BigDecimal.ZERO, BigDecimal::add)
	        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
	}

	private BigDecimal computeNormalizedActualUtil(List<ResourceProjectUtilizationSummaryDTO> resourceProjectUtilisationSummaryDTO) {
	    if (resourceProjectUtilisationSummaryDTO == null) return BigDecimal.ZERO;

	    return resourceProjectUtilisationSummaryDTO.stream()
	        .map(r -> {
	            BigDecimal daily = defaultZero(r.dailyWorkingHours());
	            Long days = Optional.ofNullable(r.approvedEntryDays()).orElse(0L);
	            if (daily.compareTo(BigDecimal.ZERO) <= 0 || days == 0) return BigDecimal.ZERO;

	            BigDecimal actual = BigDecimal.valueOf(days)
	                .divide(daily.multiply(BigDecimal.valueOf(days)), 4, RoundingMode.HALF_UP)
	                .multiply(BigDecimal.valueOf(100));

	            return actual;
	        })
	        .reduce(BigDecimal.ZERO, BigDecimal::add)
	        .divide(BigDecimal.valueOf(100), 4, RoundingMode.HALF_UP);
	}

	private BigDecimal defaultZero(BigDecimal input) {
	    return input != null ? input : BigDecimal.ZERO;
	}
	
	public DMResourceStatsDTO getResourceBillabilityStatsModified() {
	    Pair<Roles, UserAuthDetails> userAuthDetails = authenticationService.getCurrentUserDetails();
	    String empId = userAuthDetails.getRight().empId();
	    Long globalZeroPlanned = userInfoRepository.getGlobalZeroPlannedUtilizationCount(Roles.RESOURCE);
	    if (userAuthDetails.getLeft().equals(Roles.DELIVERY_MANAGER)) {
	        DMResourceStatsPartialDTO dmStats = userInfoRepository.getResourceStatsDMSpecific(Roles.RESOURCE, empId);
	        
	        log.info("Global zero planned utilization count: {}", globalZeroPlanned);
	        return new DMResourceStatsDTO(
	            dmStats.totalBillability(),
	            dmStats.totalResourceUsers(),
	            dmStats.zeroOrNoBillabilityUsers(),
	            globalZeroPlanned
	        );
	    }
	    DMResourceStatsPartialDTO dmStats = userInfoRepository.getResourceStatsPMSpecific(Roles.RESOURCE, empId);
        
        log.info("Global zero planned utilization count: {}", globalZeroPlanned);
        return new DMResourceStatsDTO(
            dmStats.totalBillability(),
            dmStats.totalResourceUsers(),
            dmStats.zeroOrNoBillabilityUsers(),
            globalZeroPlanned
        );
	}
	
	



}
