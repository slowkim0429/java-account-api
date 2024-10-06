package com.virnect.account.domain.enumclass;

import lombok.Getter;

public enum LicenseAdditionalAttributeType {
	MAXIMUM_VIEW(ProductType.SQUARS, DataType.NUMBER);

	private final ProductType productType;

	@Getter
	private final DataType dataType;

	LicenseAdditionalAttributeType(ProductType productType, DataType dataType) {
		this.productType = productType;
		this.dataType = dataType;
	}

	public static long squarsAdditionalAttributesSize() {
		long count = 0;

		for (LicenseAdditionalAttributeType licenseAdditionalAttributeType : values()) {
			if (ProductType.SQUARS.equals(licenseAdditionalAttributeType.productType)) {
				count++;
			}
		}

		return count;
	}

	public boolean isSquarsAdditionalAttribute() {
		return ProductType.SQUARS.equals(productType);
	}
}
