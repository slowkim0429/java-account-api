package com.virnect.account.adapter.outbound.request;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.response.OrganizationLicenseAdditionalAttributeResponseDto;
import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.UseStatus;

@Getter
@NoArgsConstructor
public class LicenseAdditionalAttributeSendDto {
	private Long organizationLicenseAdditionalAttributeId;
	private Long licenseAttributeId;
	private Long contractId;
	private LicenseAdditionalAttributeType licenseAdditionalAttributeType;
	private DataType dataType;
	private String dataValue;
	private UseStatus status;

	public LicenseAdditionalAttributeSendDto(
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

	public static LicenseAdditionalAttributeSendDto from(
		OrganizationLicenseAdditionalAttributeResponseDto organizationLicenseAdditionalAttributeResponseDto
	) {
		return new LicenseAdditionalAttributeSendDto(
			organizationLicenseAdditionalAttributeResponseDto.getId(),
			organizationLicenseAdditionalAttributeResponseDto.getLicenseAttributeId(),
			organizationLicenseAdditionalAttributeResponseDto.getContractId(),
			organizationLicenseAdditionalAttributeResponseDto.getLicenseAdditionalAttributeType(),
			organizationLicenseAdditionalAttributeResponseDto.getDataType(),
			organizationLicenseAdditionalAttributeResponseDto.getDataValue(),
			organizationLicenseAdditionalAttributeResponseDto.getStatus()
		);
	}

	@Override
	public String toString() {
		return "LicenseAdditionalAttributeSendDto{" +
			"licenseAttributeId=" + licenseAttributeId +
			", contractId=" + contractId +
			", licenseAdditionalAttributeType=" + licenseAdditionalAttributeType +
			", dataType=" + dataType +
			", dataValue='" + dataValue + '\'' +
			", status=" + status +
			'}';
	}
}
