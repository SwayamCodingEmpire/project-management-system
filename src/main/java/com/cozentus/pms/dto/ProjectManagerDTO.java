package com.cozentus.pms.dto;

import java.util.List;

import com.cozentus.pms.entites.ProjectDetails;


import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Setter
@Getter
@NoArgsConstructor
@ToString
public class ProjectManagerDTO {
    private String id;
    private String name;
    private String email;
    private List<String> projects;
    
    public ProjectManagerDTO(String id, String name, String email, List<ProjectDetails> projects) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.projects = projects.size()==0? List.of() : projects.stream().map(p -> p.getProjectName()).toList();
	}
    
    public void setValues(String id, String name, String email, List<String> projects) {
		this.id = id;
		this.name = name;
		this.email = email;
		this.projects = projects;
	}


}