package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.domain.model.OrganizationLicense;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@RequiredArgsConstructor
public class OrganizationLicenseDetailAndItemResponseDto {
	@ApiModelProperty(value = "organization license id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "organization id", example = "1000000000")
	private Long organizationId;

	@ApiModelProperty(value = "organization license 상태", example = "PROCESSING")
	private OrganizationLicenseStatus status;

	@ApiModelProperty(value = "organization license 시작일자", example = "2022-04-26 11:29:33")
	private String startDate;

	@ApiModelProperty(value = "organization license 만료일자", example = "2022-04-26 11:29:33")
	private String endDate;

	@ApiModelProperty(value = "organization license의 license id", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "organization license의 구매한 item id", example = "1000000000")
	private Long itemId;

	@ApiModelProperty(value = "organization license의 구매한 item name", example = "Free Plus License Item")
	private String itemName;

	@ApiModelProperty(value = "organization license의 contract id", example = "1000000000")
	private Long contractId;

	@ApiModelProperty(value = "organization license의 생성 일자", example = "2022-04-26 11:29:33")
	private String createdDate;

	@ApiModelProperty(value = "organization license의 수정 일자", example = "2022-04-26 11:29:33")
	private String updatedDate;

	@QueryProjection
	public OrganizationLicenseDetailAndItemResponseDto(
		Long id, Long organizationId, OrganizationLicenseStatus status, ZonedDateTime startDate, ZonedDateTime endDate,
		Long licenseId, Long itemId, String itemName, Long contractId, ZonedDateTime createdDate,
		ZonedDateTime updatedDate
	) {
		this.id = id;
		this.organizationId = organizationId;
		this.status = status;
		this.startDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(startDate);
		this.endDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(endDate);
		this.licenseId = licenseId;
		this.itemId = itemId;
		this.itemName = itemName;
		this.contractId = contractId;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
	}

	public OrganizationLicenseDetailAndItemResponseDto(OrganizationLicense organizationLicense, String itemName) {
		this.id = organizationLicense.getId();
		this.organizationId = organizationLicense.getOrganizationId();
		this.status = organizationLicense.getStatus();
		this.startDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getStartedAt());
		this.endDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getExpiredAt());
		this.licenseId = organizationLicense.getLicenseId();
		this.itemId = organizationLicense.getItemId();
		this.itemName = itemName;
		this.contractId = organizationLicense.getContractId();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(organizationLicense.getUpdatedDate());
	}
}
