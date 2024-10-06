package com.virnect.account.adapter.inbound.dto.request.authoritygroup;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@ApiModel
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorityGroupModifyRequestDto {
	@ApiModelProperty(value = "name", example = "Admin Master", required = true)
	@NotBlank(message = "name은 필수 값입니다.")
	@Size(max = 50)
	private String name;

	@ApiModelProperty(value = "description", example = "For admin Master")
	@Size(max = 250)
	private String description;

	private AuthorityGroupModifyRequestDto(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public static AuthorityGroupModifyRequestDto of(String name, String description) {
		return new AuthorityGroupModifyRequestDto(name, description);
	}
}
