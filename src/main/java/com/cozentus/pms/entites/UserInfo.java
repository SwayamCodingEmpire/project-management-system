package com.cozentus.pms.entites;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cozentus.pms.dto.ResourceDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_info")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class UserInfo {

	// TODO Auto-generated constructor stub

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;

	private String name;
	private String emailId;
	private String phoneNo;
//	private String primarySkill;
//	private String secondarySkill;
	private String designation;
	private String role;
	@Column(name = "daily_working_duration_in_hrs")
	private BigDecimal dailyWorkingHours;
	private BigDecimal expInYears;
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
	private String employeeType;
	@Column(name = "emp_id", unique = true)
	private String empId;

	@OneToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "credential_id", unique = true)
	private Credential credential;

	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "reporting_manager_id")
	private UserInfo reportingManager;
	
	@ManyToOne(fetch = FetchType.LAZY, cascade = { CascadeType.PERSIST, CascadeType.MERGE })
	@JoinColumn(name = "delivery_manager_id")
	private UserInfo deliveryManager;

	@OneToMany(mappedBy = "reportingManager")
	private List<UserInfo> subordinates;

	@OneToMany(mappedBy = "projectManager")
	private List<ProjectDetails> managedProjects;

	@OneToMany(mappedBy = "deliveryManager")
	private List<ProjectDetails> deliveredProjects;

	@OneToMany(mappedBy = "resource")
	private List<ResourceAllocation> allocations;
	
	@OneToMany(mappedBy = "user", cascade = { CascadeType.PERSIST, CascadeType.MERGE }, orphanRemoval = true)
	private List<UserSkillDetail> userSkillDetails;

	public UserInfo(String empId, String name, String emailId) {
		this.empId = empId;
		this.name = name;
		this.emailId = emailId;
		this.enabled = true; // Default value for enabled
	}

	public UserInfo(ResourceDTO resourceDTO) {
		this.empId = resourceDTO.id();
		this.name = resourceDTO.name();
		this.emailId = resourceDTO.emailId();
		this.phoneNo = resourceDTO.phoneNumber();
//		this.primarySkill = resourceDTO.primarySkill();
//		this.secondarySkill = resourceDTO.secondarySkill();
		this.designation = resourceDTO.designation();
		this.expInYears = BigDecimal.valueOf(resourceDTO.experience());
		this.role = resourceDTO.role();
		this.enabled = true; // Default value for enabled

		// TODO Auto-generated constructor stub
	}

	public void updateFromResourceDTO(ResourceDTO resourceDTO) {
		this.name = resourceDTO.name();
		this.emailId = resourceDTO.emailId();
		this.phoneNo = resourceDTO.phoneNumber();
//		this.primarySkill = resourceDTO.primarySkill();
//		this.secondarySkill = resourceDTO.secondarySkill();
		this.designation = resourceDTO.designation();
		this.expInYears = BigDecimal.valueOf(resourceDTO.experience());
		this.role = resourceDTO.role();
		this.empId = resourceDTO.id();
		// TODO Auto-generated method stub

	}
}
