package com.cozentus.pms.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceBasics {
    private String empId;
    private String name;
    private String skillName;
    private String level;
}