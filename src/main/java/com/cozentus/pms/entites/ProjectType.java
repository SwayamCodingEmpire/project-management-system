package com.cozentus.pms.entites;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonIgnore;

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

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "projectType")
@ToString
public class ProjectType {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @Column(length = 400, nullable = false)
    private String projectType;

    @Column(name = "is_customer_project", nullable = false)
    private Boolean isCustomerProject;

    @JsonIgnore
    @ToString.Exclude
    @OneToMany(mappedBy = "projectType", cascade = CascadeType.ALL)
    private List<ProjectDetails> projectDetails;
}
