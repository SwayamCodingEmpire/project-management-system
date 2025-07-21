package com.cozentus.pms.entites;

import java.time.LocalDateTime;
import java.util.List;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.cozentus.pms.dto.ClientDTO;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "client")
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Client {

	@Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String name;
    private String legalEntity;
    private String businessUnit;
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

    @OneToMany(mappedBy = "customer", cascade = CascadeType.ALL)
    private List<ProjectDetails> projects;
    
    public Client(ClientDTO customerInfo) {
		this.name = customerInfo.name();
		this.legalEntity = customerInfo.legalEntity();
		this.businessUnit = customerInfo.businessUnit();
	}
    
    public Client(String name, String legalEntity, String businessUnit) {
		this.name = name;
		this.legalEntity = legalEntity;
		this.businessUnit = businessUnit;
	}
    
}
