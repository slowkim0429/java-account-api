package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.License;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class LicenseRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "license id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "product id", example = "1000000000")
	private Long productId;

	@ApiModelProperty(value = "license type id", example = "1000000000")
	private Long licenseGradeId;

	@ApiModelProperty(value = "license name", example = "squars license")
	private String name;

	@ApiModelProperty(value = "license description", example = "squars license")
	private String description;

	@ApiModelProperty(value = "license status", example = "APPROVED")
	private ApprovalStatus status;

	@ApiModelProperty(value = "license use status", example = "USE")
	private UseStatus useStatus;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03T11:15:30")
	private String updatedDate;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "등록일시", example = "2022-01-03T11:15:30")
	private String createdDate;

	private LicenseRevisionResponseDto(
		RevisionType revisionType, License license
	) {
		this.revisionType = revisionType;
		this.id = license.getId();
		this.productId = license.getProductId();
		this.licenseGradeId = license.getLicenseGradeId();
		this.name = license.getName();
		this.description = license.getDescription();
		this.status = license.getStatus();
		this.useStatus = license.getUseStatus();
		this.updatedBy = license.getLastModifiedBy();
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(license.getUpdatedDate());
		this.createdBy = license.getCreatedBy();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(license.getCreatedDate());
	}

	public static LicenseRevisionResponseDto of(Byte representation, License license) {
		return new LicenseRevisionResponseDto(RevisionType.valueOf(representation), license);
	}
}
