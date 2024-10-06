package com.virnect.account.adapter.inbound.dto.request.hubspot;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@ApiModel
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExternalServiceMappingSyncableRequestDto {

	@ApiModelProperty(value = "sync 가능 여부", example = "true")
	private Boolean isSyncable;

	private ExternalServiceMappingSyncableRequestDto(Boolean isSyncable) {
		this.isSyncable = isSyncable;
	}

	public static ExternalServiceMappingSyncableRequestDto from(Boolean isSyncable) {
		return new ExternalServiceMappingSyncableRequestDto(isSyncable);
	}
}
