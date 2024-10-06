package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.OrganizationLicenseStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@RequiredArgsConstructor
public class OrganizationLicenseAndItemResponseDto {
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

	@ApiModelProperty(value = "organization license의 구매한 item id", example = "1000000000")
	private Long itemId;

	@ApiModelProperty(value = "organization license의 구매한 item name", example = "Free Plus License Item")
	private String itemName;

	@QueryProjection
	public OrganizationLicenseAndItemResponseDto(
		Long id, Long organizationId, OrganizationLicenseStatus status, ZonedDateTime startDate, ZonedDateTime endDate,
		Long itemId, String itemName
	) {
		this.id = id;
		this.organizationId = organizationId;
		this.status = status;
		this.startDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(startDate);
		this.endDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(endDate);
		this.itemId = itemId;
		this.itemName = itemName;
	}
}
