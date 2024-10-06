package com.virnect.account.domain.enumclass;

import lombok.Getter;

public enum LicenseAttributeType {
	MAXIMUM_WORKSPACE(null, DataType.NUMBER),
	MAXIMUM_GROUP(null, DataType.NUMBER),
	MAXIMUM_GROUP_USER(null, DataType.NUMBER),
	STORAGE_SIZE_PER_MB(null, DataType.NUMBER),
	MAXIMUM_PROJECT(ProductType.SQUARS, DataType.NUMBER),
	MAXIMUM_PUBLISHING_PROJECT(ProductType.SQUARS, DataType.NUMBER),
	EXCLUSION_WATERMARK(ProductType.SQUARS, DataType.BOOL),
	MAXIMUM_VIEW_PER_MONTH(ProductType.SQUARS, DataType.NUMBER),
	CUSTOMIZING_SPLASH_SCREEN(ProductType.SQUARS, DataType.BOOL);

	private final ProductType productType;

	@Getter
	private final DataType dataType;

	LicenseAttributeType(ProductType productType, DataType dataType) {
		this.productType = productType;
		this.dataType = dataType;
	}

	public static long squarsAttributesSize() {
		long count = 0;

		for (LicenseAttributeType licenseAttributeType : values()) {
			if (ProductType.SQUARS.equals(licenseAttributeType.productType)) {
				count++;
			}
		}

		return count;
	}

	public boolean isSquarsAttribute() {
		return ProductType.SQUARS.equals(productType);
	}
}
