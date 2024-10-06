package com.virnect.account.adapter.outbound.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.OrganizationLicenseAdditionalAttribute;

@Getter
@NoArgsConstructor
public class OrganizationLicenseAdditionalAttributeSendDto {
	private Long organizationLicenseAdditionalAttributeId;
	private Long licenseAttributeId;
	private Long contractId;
	private LicenseAdditionalAttributeType licenseAdditionalAttributeType;
	private DataType dataType;
	private String dataValue;
	private UseStatus status;

	private OrganizationLicenseAdditionalAttributeSendDto(
		Long organizationLicenseAdditionalAttributeId, Long licenseAttributeId, Long contractId,
		LicenseAdditionalAttributeType licenseAdditionalAttributeType,
		DataType dataType,
		String dataValue, UseStatus status
	) {
		this.organizationLicenseAdditionalAttributeId = organizationLicenseAdditionalAttributeId;
		this.licenseAttributeId = licenseAttributeId;
		this.contractId = contractId;
		this.licenseAdditionalAttributeType = licenseAdditionalAttributeType;
		this.dataType = dataType;
		this.dataValue = dataValue;
		this.status = status;
	}

	public static OrganizationLicenseAdditionalAttributeSendDto from(
		OrganizationLicenseAdditionalAttribute organizationLicenseAdditionalAttribute
	) {
		return new OrganizationLicenseAdditionalAttributeSendDto(
			organizationLicenseAdditionalAttribute.getId(),
			organizationLicenseAdditionalAttribute.getLicenseAttributeId(),
			organizationLicenseAdditionalAttribute.getContractId(),
			organizationLicenseAdditionalAttribute.getLicenseAdditionalAttributeType(),
			organizationLicenseAdditionalAttribute.getDataType(),
			organizationLicenseAdditionalAttribute.getDataValue(),
			organizationLicenseAdditionalAttribute.getStatus()
		);
	}
}
