package com.virnect.account.adapter.inbound.dto.request.license;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.LicenseGradeType;

@Setter
@Getter
@NoArgsConstructor
@ApiModel
public class LicenseGradeRequestDto {

	@ApiModelProperty(value = "license grade 이름", example = "Enterprise", required = true)
	@NotBlank(message = "license grade 이름 정보는 반드시 입력 되어야 합니다.")
	@Size(max = 100)
	private String name;

	@ApiModelProperty(value = "license grade type", example = "ENTERPRISE", required = true)
	@NotBlank(message = "license grade 타입 정보는 반드시 입력되어야 합니다.")
	@Size(max = 50)
	@CommonEnum(enumClass = LicenseGradeType.class)
	private String gradeType;

	@ApiModelProperty(value = "license grade 설명", example = "license grade description", required = true)
	@NotBlank(message = "license grade 설명 정보는 반드시 입력되어야 합니다.")
	@Size(max = 255)
	private String description;

	private LicenseGradeRequestDto(String name, String gradeType, String description) {
		this.name = name;
		this.gradeType = gradeType;
		this.description = description;
	}

	public static LicenseGradeRequestDto of(String name, String gradeType, String description) {
		return new LicenseGradeRequestDto(name, gradeType, description);
	}
}
