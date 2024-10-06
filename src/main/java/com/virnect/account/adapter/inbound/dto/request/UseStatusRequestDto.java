package com.virnect.account.adapter.inbound.dto.request;

import javax.validation.constraints.NotBlank;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.validate.UseStatusSubset;
import com.virnect.account.domain.enumclass.UseStatus;

@Getter
@ApiModel
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UseStatusRequestDto {
	@NotBlank
	@ApiModelProperty(value = "사용 상태", example = "USE")
	@UseStatusSubset(anyOf = {UseStatus.USE, UseStatus.UNUSE})
	private String useStatus;

	private UseStatusRequestDto(String useStatus) {
		this.useStatus = useStatus;
	}

	public static UseStatusRequestDto from(String useStatus) {
		return new UseStatusRequestDto(useStatus);
	}

	public UseStatus useStatusValueOf() {
		return UseStatus.valueOf(useStatus);
	}
}
