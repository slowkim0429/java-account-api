package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.DependencyType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.LicenseAttribute;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
@NoArgsConstructor
public class LicenseAttributeRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "license attribute id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "license id", example = "1000000000")
	private Long licenseId;

	@ApiModelProperty(value = "license attribute dependency type", example = "DEPENDENCE")
	private DependencyType attributeDependencyType;

	@ApiModelProperty(value = "license attribute type", example = "MAXIMUM_GROUP")
	private LicenseAttributeType attributeType;

	@ApiModelProperty(value = "license additional attribute type", example = "MAXIMUM_VIEW")
	private LicenseAdditionalAttributeType additionalAttributeType;

	@ApiModelProperty(value = "License Attribute data type", example = "NUMBER")
	private DataType dataType;

	@ApiModelProperty(value = "license attribute data value", example = "100")
	private String dataValue;

	@ApiModelProperty(value = "license attribute status", example = "USE")
	private UseStatus status;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03T11:15:30")
	private String updatedDate;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "등록일시", example = "2022-01-03T11:15:30")
	private String createdDate;

	private LicenseAttributeRevisionResponseDto(
		RevisionType revisionType, LicenseAttribute licenseAttribute
	) {
		this.revisionType = revisionType;
		this.id = licenseAttribute.getId();
		this.licenseId = licenseAttribute.getLicenseId();
		this.attributeDependencyType = licenseAttribute.getAttributeDependencyType();
		this.attributeType = licenseAttribute.getAttributeType();
		this.additionalAttributeType = licenseAttribute.getAdditionalAttributeType();
		this.dataType = licenseAttribute.getDataType();
		this.dataValue = licenseAttribute.getDataValue();
		this.status = licenseAttribute.getStatus();
		this.updatedBy = licenseAttribute.getLastModifiedBy();
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(licenseAttribute.getUpdatedDate());
		this.createdBy = licenseAttribute.getCreatedBy();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(licenseAttribute.getCreatedDate());
	}

	public static LicenseAttributeRevisionResponseDto of(Byte representation, LicenseAttribute licenseAttribute) {
		return new LicenseAttributeRevisionResponseDto(RevisionType.valueOf(representation), licenseAttribute);
	}
}
