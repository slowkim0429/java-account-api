package com.virnect.account.adapter.inbound.dto.response.grade;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.model.LicenseGrade;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class LicenseGradeRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "License Grade id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "License Grade 이름", example = "Free")
	private String name;

	@ApiModelProperty(value = "License Grade Type", example = "FREE_PLUS")
	private LicenseGradeType gradeType;

	@ApiModelProperty(value = "License Grade 설명", example = "Free License Grade")
	private String description;

	@ApiModelProperty(value = "License Grade 상태", example = "REGISTER")
	private ApprovalStatus status;

	@ApiModelProperty(value = "등록 일자", example = "2022-01-03T10:15:30")
	private String createdDate;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03T11:15:30")
	private String updatedDate;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	private LicenseGradeRevisionResponseDto(
		RevisionType revisionType, LicenseGrade licenseGrade
	) {
		this.revisionType = revisionType;
		this.id = licenseGrade.getId();
		this.name = licenseGrade.getName();
		this.gradeType = licenseGrade.getGradeType();
		this.description = licenseGrade.getDescription();
		this.status = licenseGrade.getStatus();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(licenseGrade.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(licenseGrade.getUpdatedDate());
		this.createdBy = licenseGrade.getCreatedBy();
		this.updatedBy = licenseGrade.getLastModifiedBy();
	}

	public static LicenseGradeRevisionResponseDto of(Byte representation, LicenseGrade licenseGrade) {
		return new LicenseGradeRevisionResponseDto(RevisionType.valueOf(representation), licenseGrade);
	}
}
