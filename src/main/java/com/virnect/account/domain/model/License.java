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
import javax.validation.constraints.Size;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseRequestDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Getter
@Audited
@Table(name = "licenses",
	indexes = {
		@Index(name = "idx_product_id", columnList = "product_id"),
		@Index(name = "idx_license_grade_id", columnList = "license_grade_id"),
		@Index(name = "idx_name", columnList = "name"),
		@Index(name = "idx_status", columnList = "status"),
		@Index(name = "idx_created_by", columnList = "created_by"),
		@Index(name = "idx_updated_by", columnList = "updated_by"),
		@Index(name = "idx_use_status", columnList = "use_status")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class License extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "product_id", nullable = false)
	private Long productId;

	@Column(name = "license_grade_id", nullable = false)
	private Long licenseGradeId;

	@Column(name = "name", length = 50, nullable = false)
	@Size(max = 50)
	private String name;

	@Column(name = "description", nullable = false)
	private String description;

	@Column(name = "sales_target", length = 50, nullable = false)
	private String salesTarget;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private ApprovalStatus status = ApprovalStatus.REGISTER;

	@Column(name = "use_status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private UseStatus useStatus = UseStatus.USE;

	@Builder
	private License(
		Long productId, Long licenseGradeId, String name, String description, String salesTarget
	) {
		this.productId = productId;
		this.licenseGradeId = licenseGradeId;
		this.name = name;
		this.description = description;
		this.salesTarget = salesTarget;
	}

	public static License from(LicenseRequestDto licenseRequestDto, Long productId) {
		return License.builder()
			.productId(productId)
			.licenseGradeId(licenseRequestDto.getLicenseGradeId())
			.name(licenseRequestDto.getName())
			.description(licenseRequestDto.getDescription())
			.salesTarget(licenseRequestDto.getSalesTarget())
			.build();
	}

	public void update(LicenseRequestDto licenseRequestDto, Long productId) {
		this.productId = productId;
		this.licenseGradeId = licenseRequestDto.getLicenseGradeId();
		this.name = licenseRequestDto.getName();
		this.description = licenseRequestDto.getDescription();
		this.salesTarget = licenseRequestDto.getSalesTarget();
	}

	public void updateApprovalStatus(ApprovalStatus status) {
		this.status = status;
	}
}
