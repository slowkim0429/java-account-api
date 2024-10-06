package com.virnect.account.adapter.outbound.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.OrganizationLicenseAttribute;

@Getter
@NoArgsConstructor
public class OrganizationLicenseAttributeSendDto {
	private Long organizationLicenseAttributeId;
	private Long licenseAttributeId;
	private LicenseAttributeType licenseAttributeType;
	private DataType dataType;
	private String dataValue;
	private UseStatus status;

	public OrganizationLicenseAttributeSendDto(
		Long organizationLicenseAttributeId, Long licenseAttributeId, LicenseAttributeType licenseAttributeType,
		DataType dataType, String dataValue, UseStatus status
	) {
		this.organizationLicenseAttributeId = organizationLicenseAttributeId;
		this.licenseAttributeId = licenseAttributeId;
		this.licenseAttributeType = licenseAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
		this.status = status;
	}

	public static OrganizationLicenseAttributeSendDto from(OrganizationLicenseAttribute organizationLicenseAttribute) {
		return new OrganizationLicenseAttributeSendDto(organizationLicenseAttribute.getId(),
			organizationLicenseAttribute.getLicenseAttributeId(),
			organizationLicenseAttribute.getLicenseAttributeType(), organizationLicenseAttribute.getDataType(),
			organizationLicenseAttribute.getDataValue(), organizationLicenseAttribute.getStatus()
		);
	}
}
