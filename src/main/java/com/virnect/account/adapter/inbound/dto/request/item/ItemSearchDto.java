package com.virnect.account.adapter.inbound.dto.request.item;

import javax.validation.constraints.Min;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.ApprovalStatusSubset;
import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.ProductType;

@Getter
@Setter
@ApiModel
public class ItemSearchDto {

	@CommonEnum(enumClass = ItemType.class)
	@ApiModelProperty(value = "아이템 타입", example = "LICENSE")
	private String itemType;

	@ApprovalStatusSubset(anyOf = {ApprovalStatus.REGISTER, ApprovalStatus.APPROVED, ApprovalStatus.REJECT})
	@ApiModelProperty(value = "item 승인 상태", example = "REGISTER")
	private String status;

	@CommonEnum(enumClass = LicenseGradeType.class)
	@ApiModelProperty(value = "라이센스 등급 타입", example = "FREE_PLUS")
	private String licenseGradeType;

	@CommonEnum(enumClass = ProductType.class)
	@ApiModelProperty(value = "프로덕트 타입", example = "SQUARS")
	private String productType;

	@ApiModelProperty(value = "노출여부", example = "true")
	private Boolean isExposed;

	@Min(value = 1000000000)
	@ApiModelProperty(value = "item id", example = "1000000000")
	private Long itemId;

	@ApiModelProperty(value = "item name", example = "free")
	private String itemName;

	public ProductType valueOfProductType() {
		return StringUtils.isBlank(productType) ? null : ProductType.valueOf(productType);
	}
}
