package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.OrganizationLicenseAdditionalAttribute;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@RequiredArgsConstructor
public class OrganizationLicenseAdditionalAttributeRevisionResponseDto {
	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "Organization License Additional Attribute Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Organization License Id", example = "1000000000")
	private Long subscriptionOrganizationLicenseId;

	@ApiModelProperty(value = "Organization Contract Id", example = "1000000000")
	private Long organizationContractId;

	@ApiModelProperty(value = "Contract Id", example = "1000000000")
	private Long contractId;

	@ApiModelProperty(value = "License Attribute Id", example = "1000000000")
	private Long licenseAttributeId;

	@ApiModelProperty(value = "License Additional Attribute Type", example = "MAXIMUM_VIEW")
	private LicenseAdditionalAttributeType licenseAdditionalAttributeType;

	@ApiModelProperty(value = "Data Type", example = "NUMBER")
	private DataType dataType;

	@ApiModelProperty(value = "Data Value", example = "1000")
	private String dataValue;

	@ApiModelProperty(value = "Status", example = "USE")
	private UseStatus status;

	@ApiModelProperty(value = "Organization License Additional Attribute 의 생성 일자", example = "2022-04-26 11:29:33")
	private String createdDate;

	@ApiModelProperty(value = "Organization License Additional Attribute 의 수정 일자", example = "2022-04-26 11:29:33")
	private String updatedDate;

	@ApiModelProperty(value = "Organization License Additional Attribute 의 생성 User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "OOrganization License Additional Attribute 의 수정 User Id", example = "1000000000")
	private Long updatedBy;

	public OrganizationLicenseAdditionalAttributeRevisionResponseDto(
		RevisionType revisionType, OrganizationLicenseAdditionalAttribute organizationLicenseAdditionalAttribute
	) {
		this.revisionType = revisionType;
		this.id = organizationLicenseAdditionalAttribute.getId();
		this.subscriptionOrganizationLicenseId = organizationLicenseAdditionalAttribute.getSubscriptionOrganizationLicenseId();
		this.organizationContractId = organizationLicenseAdditionalAttribute.getOrganizationContractId();
		this.contractId = organizationLicenseAdditionalAttribute.getContractId();
		this.licenseAttributeId = organizationLicenseAdditionalAttribute.getLicenseAttributeId();
		this.licenseAdditionalAttributeType = organizationLicenseAdditionalAttribute.getLicenseAdditionalAttributeType();
		this.dataType = organizationLicenseAdditionalAttribute.getDataType();
		this.dataValue = organizationLicenseAdditionalAttribute.getDataValue();
		this.status = organizationLicenseAdditionalAttribute.getStatus();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(
			organizationLicenseAdditionalAttribute.getCreatedDate());
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(
			organizationLicenseAdditionalAttribute.getUpdatedDate());
		this.createdBy = organizationLicenseAdditionalAttribute.getCreatedBy();
		this.updatedBy = organizationLicenseAdditionalAttribute.getLastModifiedBy();
	}

	public static OrganizationLicenseAdditionalAttributeRevisionResponseDto of(
		Byte representation, OrganizationLicenseAdditionalAttribute organizationLicenseAdditionalAttribute
	) {
		return new OrganizationLicenseAdditionalAttributeRevisionResponseDto(
			RevisionType.valueOf(representation), organizationLicenseAdditionalAttribute);
	}
}
