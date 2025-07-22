package com.cozentus.pms.entites;


import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.DynamicInsert;
import org.hibernate.annotations.UpdateTimestamp;

import com.cozentus.pms.dto.ProjectDetailsDTO;
import com.cozentus.pms.dto.ProjectTypeDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "proj_details")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@DynamicInsert
public class ProjectDetails {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String projectCode;
    private String projectName;
    private String currency;
    private LocalDate startDate;
    private LocalDate endDate;
    private String projectDescription;
    private String contractType;
    private String billingFrequency;
    @CreationTimestamp
	@Column(name = "created_at", updatable = false)
	private LocalDateTime createdAt;
	@Column(name = "created_by", length = 50, updatable = false)
	private String createdBy;
	@UpdateTimestamp
	@Column(name = "updated_at")
	private LocalDateTime updatedAt;
	private String updatedBy;
    private Boolean enabled = true;
    private String status;
    private String timesheetSummaryDay;
    private String timesheetReminderDay;
    @Column(name = "timesheet_warning_day_1")
    private String timesheetWarningDay1;
    @Column(name = "timesheet_warning_day_2")
    private String timesheetWarningDay2;
    private LocalDate actualEndDate;
    
    @ManyToOne
    @JoinColumn(name = "projectTypeId", nullable = true)
    private ProjectType projectType;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "project_manager_id")
    private UserInfo projectManager;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "delivery_manager_id")
    private UserInfo deliveryManager;

    @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
    @JoinColumn(name = "customer_id")
    private Client customer;

    
    @OneToMany(mappedBy = "project")
    private List<ResourceAllocation> allocations;
    
    public ProjectDetails(ProjectDetailsDTO projectDetailsDTO, ProjectTypeDTO projectTypeDTO) {
		this.projectCode = projectDetailsDTO.code();
		this.projectName = projectDetailsDTO.name();
		this.projectDescription = projectDetailsDTO.description();
		this.startDate = projectDetailsDTO.startDate();
		this.endDate = projectDetailsDTO.endDate();
		this.currency = projectDetailsDTO.currency();
		this.contractType = projectDetailsDTO.contractType();
		this.billingFrequency = projectDetailsDTO.billingFrequency();
		this.status = "INITIALIZED";
    }

	public void updateProjectDetails(ProjectDetailsDTO projectDetailsDTO, ProjectTypeDTO projectTypeDTO) {
		this.projectCode = projectDetailsDTO.code();
		this.projectName = projectDetailsDTO.name();
		this.projectDescription = projectDetailsDTO.description();
		this.startDate = projectDetailsDTO.startDate();
		this.endDate = projectDetailsDTO.endDate();
		this.currency = projectDetailsDTO.currency();
		this.contractType = projectDetailsDTO.contractType();
		this.billingFrequency = projectDetailsDTO.billingFrequency();
		this.status = "INITIALIZED";
		
	}
}
