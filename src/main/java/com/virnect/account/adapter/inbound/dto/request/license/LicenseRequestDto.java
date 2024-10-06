package com.virnect.account.adapter.inbound.dto.request.license;

import java.util.List;

import javax.validation.Valid;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Setter
@Getter
@NoArgsConstructor
public class LicenseRequestDto {
	@ApiModelProperty(value = "Product Id", example = "1000000000", required = true)
	@NotNull
	@Min(1000000000)
	private Long productId;

	@ApiModelProperty(value = "License Grade Id", example = "1000000000", required = true)
	@NotNull
	@Min(1000000000)
	private Long licenseGradeId;

	@ApiModelProperty(value = "License 이름", example = "Free License", required = true)
	@NotBlank(message = "License 이름 정보는 반드시 입력되어야 합니다.")
	@Size(max = 50)
	private String name;

	@ApiModelProperty(value = "License 설명", example = "Free License 입니다.", required = true)
	@NotBlank(message = "License 설명 정보는 반드시 입력되어야 합니다.")
	@Size(max = 108)
	private String description;

	@ApiModelProperty(value = "sales target", example = "For Freelancers")
	@NotBlank(message = "sales target 정보는 반드시 입력되어야 합니다.")
	@Size(max = 33)
	private String salesTarget;

	@ApiModelProperty(value = "License 속성 목록")
	private List<@Valid LicenseAttributeRequestDto> licenseAttributes;

	@ApiModelProperty(value = "License 부가 속성 목록")
	private List<LicenseAdditionalAttributeRequestDto> licenseAdditionalAttributes;
}
