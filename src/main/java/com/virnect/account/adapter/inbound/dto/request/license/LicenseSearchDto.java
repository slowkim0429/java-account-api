package com.virnect.account.adapter.inbound.dto.request.license;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.ApprovalStatusSubset;
import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.LicenseGradeType;

@Getter
@Setter
@ApiModel
public class LicenseSearchDto {
	@ApiModelProperty(value = "라이선스 승인 상태", example = "APPROVED")
	@ApprovalStatusSubset(anyOf = {ApprovalStatus.REGISTER, ApprovalStatus.APPROVED, ApprovalStatus.REJECT})
	private String status;

	@ApiModelProperty(value = "product id", example = "1000000000")
	private Long productId;

	@ApiModelProperty(value = "license grade type", example = "ENTERPRISE")
	@CommonEnum(enumClass = LicenseGradeType.class)
	private String licenseGradeType;

	@ApiModelProperty(value = "license grade id", example = "1000000000")
	private Long licenseGradeId;

	@ApiModelProperty(value = "license id", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "license name", example = "SQUARS Free Plus License")
	private String licenseName;
}
