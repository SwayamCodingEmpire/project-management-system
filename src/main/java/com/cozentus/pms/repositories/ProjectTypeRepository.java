package com.cozentus.pms.repositories;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.cozentus.pms.dto.ProjectTypeDropdownDTO;
import com.cozentus.pms.dto.ProjectTypeOptionsDTO;
import com.cozentus.pms.entites.ProjectType;

public interface ProjectTypeRepository extends JpaRepository<ProjectType, Integer> {

//	@Query("SELECT new com.cozentus.pms.dto.ProjectTypeDropdownDTO(pt.id, pt.projectType, pt.projectCategory) " +
//		   "FROM ProjectType pt")
//	List<ProjectTypeDropdownDTO> findAllProjectTypes();
//
//	@Query("SELECT new com.cozentus.pms.dto.ProjectTypeOptionsDTO(pt.id, pt.projectType, pt.projectCategory) " +
//		   "FROM ProjectType pt")
//	List<ProjectTypeOptionsDTO> findAllProjectTypeOptions();
//
//	Optional<ProjectType> findById(Integer id);

}
