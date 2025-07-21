package com.cozentus.pms.dto;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
 
public interface ProjectDashboardDTO {
    String getCode();
    String getName();
    String getCustomer();
    String getManager();
    Double getBillability();
    Integer getTotalResources();
    Double getPlannedUtilization();
    Double getActualUtilization();
}
