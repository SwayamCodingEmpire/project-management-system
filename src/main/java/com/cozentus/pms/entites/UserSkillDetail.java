package com.cozentus.pms.entites;

import java.math.BigDecimal;

import com.cozentus.pms.helpers.SkillPriority;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;

@Entity
@Table(name = "user_skill_details")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ToString
public class UserSkillDetail {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "skillId", nullable = false, foreignKey = @ForeignKey(name = "fk_user_skill_skill"))
    private Skill skill;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "userId", nullable = false, foreignKey = @ForeignKey(name = "fk_user_skill_user"))
    private UserInfo user;

    @Column(length = 20)
    private String level;

    @Column(name = "experience_in_years", precision = 4, scale = 2)
    private BigDecimal experienceInYears;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private SkillPriority priority;
}