package com.virnect.account.adapter.inbound.dto.request.license;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.LicenseGradeType;

@Setter
@Getter
public class LicenseGradeUpdateRequestDto {

	@ApiModelProperty(value = "License Grade 이름", example = "Enterprise", required = true)
	@NotBlank(message = "License Grade 이름 정보는 반드시 입력 되어야 합니다.")
	@Size(max = 100)
	private String name;

	@ApiModelProperty(value = "License Grade type", example = "ENTERPRISE", required = true)
	@NotBlank(message = "License Grade type 정보는 반드시 입력되어야 합니다.")
	@Size(max = 100)
	@CommonEnum(enumClass = LicenseGradeType.class)
	private String gradeType;

	@ApiModelProperty(value = "License Grade description", example = "License Grade description", position = 1, required = true)
	@NotBlank(message = "License Grade description 정보는 반드시 입력되어야 합니다.")
	@Size(max = 255)
	private String description;
}
