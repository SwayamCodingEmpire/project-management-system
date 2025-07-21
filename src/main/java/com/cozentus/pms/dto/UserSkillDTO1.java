package com.cozentus.pms.dto;

import java.util.List;

public class UserSkillDTO1 {
    private String empId;
    private List<String> skills;

    public UserSkillDTO1(String empId, List<String> skills) {
        this.empId = empId;
        this.skills = skills;
    }

    // Getters and setters
    public String getEmpId() {
        return empId;
    }

    public List<String> getSkills() {
        return skills;
    }
}
