package com.cozentus.pms.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public record ZohoEmployeeDTO(
    @JsonProperty("EmployeeID")
    String employeeId,

    @JsonProperty("FirstName")
    String firstName,

    @JsonProperty("LastName")
    String lastName,
    
    @JsonProperty("EmailID")
    String emailId,

    @JsonProperty("Mobile")
    String phoneNo,

    @JsonProperty("Designation")
    String designation,

    @JsonProperty("Role")
    String role,

    @JsonProperty("total_experience")
    String experience,

    @JsonProperty("Employee_type")
    String employeeType,

    @JsonProperty("Reporting_To.ID")
    String reportingManagerId,

    @JsonProperty("Reporting_To")
    String reportingManagerName,

    @JsonProperty("Primary_Skills1")
    String primarySkills,

    @JsonProperty("Secondary_Skills")
    String secondarySkills,
    
    @JsonProperty("Level1")
    String primarySkillLevel,
    @JsonProperty("Level2")
    String secondarySkillLevel,
    @JsonProperty("Organization_Role")
    String organizationRole,
    @JsonProperty("Reporting_To.MailID")
    String reportingManagerEmailId
) {}
