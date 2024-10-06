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

import com.virnect.account.adapter.inbound.dto.request.license.LicenseAdditionalAttributeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseAttributeRequestDto;
import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.DependencyType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Getter
@Audited
@Table(name = "license_attributes",
	indexes = {
		@Index(name = "idx_license_id", columnList = "license_id"),
		@Index(name = "idx_license_attribute_type", columnList = "license_attribute_type"),
		@Index(name = "idx_license_additional_attribute_type", columnList = "license_additional_attribute_type"),
		@Index(name = "idx_license_attribute_dependency_type", columnList = "license_attribute_dependency_type"),
		@Index(name = "idx_status", columnList = "status"),
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LicenseAttribute extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "license_id", nullable = false)
	private Long licenseId;

	@Enumerated(EnumType.STRING)
	@Column(name = "license_attribute_type", nullable = true, length = 30)
	private LicenseAttributeType attributeType;

	@Enumerated(EnumType.STRING)
	@Column(name = "license_additional_attribute_type", nullable = true, length = 30)
	private LicenseAdditionalAttributeType additionalAttributeType;

	@Enumerated(EnumType.STRING)
	@Column(name = "data_type", nullable = false, length = 10)
	private DataType dataType;

	@Column(name = "data_value", nullable = false, length = 10)
	@Size(min = 1, max = 10)
	private String dataValue;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private UseStatus status = UseStatus.USE;

	@Column(name = "license_attribute_dependency_type", nullable = false, length = 30)
	@Enumerated(EnumType.STRING)
	private DependencyType attributeDependencyType;

	@Builder
	private LicenseAttribute(
		Long licenseId, LicenseAttributeType attributeType, LicenseAdditionalAttributeType additionalAttributeType,
		DataType dataType, String dataValue, DependencyType attributeDependencyType
	) {
		this.licenseId = licenseId;
		this.attributeType = attributeType;
		this.additionalAttributeType = additionalAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
		this.attributeDependencyType = attributeDependencyType;
	}

	public static LicenseAttribute of(Long licenseId, LicenseAttributeRequestDto licenseAttributeRequestDto) {
		return LicenseAttribute.builder()
			.licenseId(licenseId)
			.attributeType(licenseAttributeRequestDto.getLicenseAttributeType())
			.additionalAttributeType(null)
			.dataType(licenseAttributeRequestDto.getDataType())
			.dataValue(licenseAttributeRequestDto.getDataValue())
			.attributeDependencyType(DependencyType.DEPENDENCE)
			.build();
	}

	public static LicenseAttribute of(
		Long licenseId, LicenseAdditionalAttributeRequestDto licenseAdditionalAttributeRequestDto
	) {
		return LicenseAttribute.builder()
			.licenseId(licenseId)
			.attributeType(null)
			.additionalAttributeType(licenseAdditionalAttributeRequestDto.getLicenseAdditionalAttributeType())
			.dataType(licenseAdditionalAttributeRequestDto.getDataType())
			.dataValue(licenseAdditionalAttributeRequestDto.getDataValue())
			.attributeDependencyType(DependencyType.INDEPENDENCE)
			.build();
	}

	public void setDataValue(String dataValue) {
		this.dataValue = dataValue;
	}

	public void setStatus(UseStatus status) {
		this.status = status;
	}

}
