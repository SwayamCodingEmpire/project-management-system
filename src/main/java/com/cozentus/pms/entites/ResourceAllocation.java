package com.cozentus.pms.entites;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cozentus.pms.dto.ResourceAllocationsSubmitDTO;

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
@Table(name = "resource_allocation")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class ResourceAllocation {
	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private LocalDate allocationStartDate;
    private LocalDate allocationEndDate;
    private String role;
    private BigDecimal billabilityPercent;
    private BigDecimal plannedHours;
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
    private LocalDate actualAllocationEndDate;
    private boolean allocationCompleted = false;

    @ManyToOne
    @JoinColumn(name = "resource_id")
    private UserInfo resource;

    @ManyToOne
    @JoinColumn(name = "project_id")
    private ProjectDetails project;

    @OneToMany(mappedBy = "resourceAllocation")
    private List<TimeSheet> timeSheets;
    
    public ResourceAllocation(ResourceAllocationsSubmitDTO allocation) {
		this.allocationStartDate = allocation.start();
		this.allocationEndDate = allocation.end();
		this.role = allocation.role();
		this.billabilityPercent = allocation.billability();
		this.plannedHours = allocation.plannedHours();
		this.enabled = true;
		this.actualAllocationEndDate = null;
	}
}
