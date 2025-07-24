package com.cozentus.pms.dto;

public class ProjectDashboardRoundedDTO implements ProjectDashboardDTO {
	  private final String code;
	    private final String name;
	    private final String customer;
	    private final String manager;
	    private final Double billability;
	    private final Integer totalResources;
	    private final Double plannedUtilization;
	    private final Double actualUtilization;

	    public ProjectDashboardRoundedDTO(ProjectDashboardDTO original) {
	        this.code = original.getCode();
	        this.name = original.getName();
	        this.customer = original.getCustomer();
	        this.manager = original.getManager();
	        this.billability = round(original.getBillability());
	        this.totalResources = original.getTotalResources();
	        this.plannedUtilization = round(original.getPlannedUtilization());
	        this.actualUtilization = round(original.getActualUtilization());
	    }

	    private Double round(Double value) {
	        return value == null ? null : Math.round(value * 100.0) / 100.0;
	    }

	    // Getters
	    public String getCode() { return code; }
	    public String getName() { return name; }
	    public String getCustomer() { return customer; }
	    public String getManager() { return manager; }
	    public Double getBillability() { return billability; }
	    public Integer getTotalResources() { return totalResources; }
	    public Double getPlannedUtilization() { return plannedUtilization; }
	    public Double getActualUtilization() { return actualUtilization; }

}
