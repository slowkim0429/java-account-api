package com.virnect.account.adapter.inbound.dto.request.hubspot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.InternalDomain;

@Getter
@Setter
@ApiModel
public class ExternalServiceMappingSearchDto {
	@ApiModelProperty(value = "Internal Service Domain Name", example = "USER")
	private InternalDomain internalDomain;

	@ApiModelProperty(value = "Internal Service Domain Mapping (With Hubspot) Id", example = "1000000000")
	private Long internalMappingId;

	@ApiModelProperty(value = "Latest Service Data Mapping Result(true or false)", example = "ture")
	private Boolean isLatestMappingSucceeded;
}
