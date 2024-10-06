package com.virnect.account.adapter.inbound.dto.response;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ExternalDomain;
import com.virnect.account.domain.enumclass.InternalDomain;

@Getter
@ApiModel
@NoArgsConstructor
public class HubspotMappingResponseDto {
	@ApiModelProperty(value = "External Service Mapping Id", example = "1000000000")
	private Long id;
	@ApiModelProperty(value = "External Service Domain (Hubspot)", example = "CONTACT")
	private ExternalDomain externalDomain;
	@ApiModelProperty(value = "Internal Service Domain", example = "USER")
	private InternalDomain internalDomain;
	@ApiModelProperty(value = "External Service Mapping Id", example = "1000000000")
	private String externalMappingId;
	@ApiModelProperty(value = "Internal Service Mapping Id", example = "1000000000")
	private Long internalMappingId;
	@ApiModelProperty(value = "Is Latest Mapping Succeeded", example = "true")
	private Boolean isLatestMappingSucceeded;
	@ApiModelProperty(value = "Is Syncable", example = "true")
	private Boolean isSyncable;
	@ApiModelProperty(value = "Create User Id", example = "1000000000")
	private Long createdBy;
	@ApiModelProperty(value = "Update User Id", example = "1000000000")
	private Long updatedBy;
	@ApiModelProperty(value = "Created Date", example = "2022-01-04T11:44:55")
	private String createdDate;
	@ApiModelProperty(value = "Latest Updated Date", example = "2022-01-04T11:44:55")
	private String updatedDate;

	@QueryProjection
	public HubspotMappingResponseDto(
		Long id, ExternalDomain externalDomain, InternalDomain internalDomain, String externalMappingId,
		Long internalMappingId, Boolean isLatestMappingSucceeded, Boolean isSyncable, Long createdBy, Long updatedBy,
		String createdDate, String updatedDate
	) {
		this.id = id;
		this.externalDomain = externalDomain;
		this.internalDomain = internalDomain;
		this.externalMappingId = externalMappingId;
		this.internalMappingId = internalMappingId;
		this.isLatestMappingSucceeded = isLatestMappingSucceeded;
		this.isSyncable = isSyncable;
		this.createdBy = createdBy;
		this.updatedBy = updatedBy;
		this.createdDate = createdDate;
		this.updatedDate = updatedDate;
	}
}
