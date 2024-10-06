package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@NoArgsConstructor
public class OrganizationLicenseAdditionalAttributeResponseDto {
	private Long id;
	private Long contractId;
	private Long licenseAttributeId;
	private LicenseAdditionalAttributeType licenseAdditionalAttributeType;
	private DataType dataType;
	private String dataValue;
	private UseStatus status;
	private String createdDate;
	private String updatedDate;
	private Long createdBy;
	private Long updatedBy;

	@QueryProjection
	public OrganizationLicenseAdditionalAttributeResponseDto(
		Long id, Long contractId, Long licenseAttributeId,
		LicenseAdditionalAttributeType licenseAdditionalAttributeType,
		DataType dataType, String dataValue, UseStatus status,
		ZonedDateTime createdDate, ZonedDateTime updatedDate, Long createdBy, Long updatedBy
	) {
		this.id = id;
		this.contractId = contractId;
		this.licenseAttributeId = licenseAttributeId;
		this.licenseAdditionalAttributeType = licenseAdditionalAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
		this.status = status;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
	}

}
