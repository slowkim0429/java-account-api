package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ExternalDomain;
import com.virnect.account.domain.enumclass.ExternalService;
import com.virnect.account.domain.enumclass.InternalDomain;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.model.ExternalServiceMapping;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class ExternalServiceMappingRevisionResponseDto {

	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "external service mapping id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "External Service", example = "HUBSPOT")
	private ExternalService externalService;

	@ApiModelProperty(value = "External Service Domain", example = "CONTACT")
	private ExternalDomain externalDomain;

	@ApiModelProperty(value = "Internal Service Domain", example = "USER")
	private InternalDomain internalDomain;

	@ApiModelProperty(value = "Internal Service Mapping Id", example = "1000000000")
	private Long internalMappingId;

	@ApiModelProperty(value = "External Service Mapping Id", example = "1000000000")
	private String externalMappingId;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03T11:15:30")
	private String updatedDate;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "등록일시", example = "2022-01-03T11:15:30")
	private String createdDate;

	@ApiModelProperty(value = "Is Latest Mapping Succeeded", example = "true")
	private boolean isLatestMappingSucceeded;

	private ExternalServiceMappingRevisionResponseDto(
		RevisionType revisionType, ExternalServiceMapping externalServiceMapping
	) {
		this.revisionType = revisionType;
		this.id = externalServiceMapping.getId();
		this.externalService = externalServiceMapping.getExternalService();
		this.externalDomain = externalServiceMapping.getExternalDomain();
		this.internalDomain = externalServiceMapping.getInternalDomain();
		this.internalMappingId = externalServiceMapping.getInternalMappingId();
		this.externalMappingId = externalServiceMapping.getExternalMappingId();
		this.updatedBy = externalServiceMapping.getLastModifiedBy();
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(externalServiceMapping.getUpdatedDate());
		this.createdBy = externalServiceMapping.getCreatedBy();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(externalServiceMapping.getCreatedDate());
		this.isLatestMappingSucceeded = externalServiceMapping.getIsLatestMappingSucceeded();
	}

	public static ExternalServiceMappingRevisionResponseDto of(
		Byte representation, ExternalServiceMapping externalServiceMapping
	) {
		return new ExternalServiceMappingRevisionResponseDto(
			RevisionType.valueOf(representation), externalServiceMapping);
	}
}

