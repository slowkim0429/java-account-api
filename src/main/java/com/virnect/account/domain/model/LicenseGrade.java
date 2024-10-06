package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseGradeRequestDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.LicenseGradeType;

@Entity
@Getter
@Audited
@Table(name = "license_grades",
	indexes = {
		@Index(name = "idx_status", columnList = "status"),
		@Index(name = "idx_name", columnList = "name")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LicenseGrade extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "grade_type", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private LicenseGradeType gradeType;

	@Column(name = "description", nullable = false, length = 255)
	private String description;

	@Column(name = "status", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private ApprovalStatus status = ApprovalStatus.REGISTER;

	public void update(LicenseGradeRequestDto requestDto) {
		this.name = requestDto.getName();
		this.gradeType = LicenseGradeType.valueOf(requestDto.getGradeType().toUpperCase());
		this.description = requestDto.getDescription();
	}

	private LicenseGrade(String name, LicenseGradeType gradeType, String description) {
		this.name = name;
		this.gradeType = gradeType;
		this.description = description;
	}

	public static LicenseGrade from(LicenseGradeRequestDto licenseGradeRequestDto) {
		return new LicenseGrade(
			licenseGradeRequestDto.getName(),
			LicenseGradeType.valueOf(licenseGradeRequestDto.getGradeType().toUpperCase()),
			licenseGradeRequestDto.getDescription()
		);
	}

	public void setStatus(ApprovalStatus status) {
		this.status = status;
	}

	public boolean isInvalidTypeForCoupon() {
		return gradeType == LicenseGradeType.FREE_PLUS || gradeType == LicenseGradeType.ENTERPRISE;
	}
}

