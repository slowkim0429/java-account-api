package com.virnect.account.adapter.inbound.dto.request.product;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.ProductType;

@Setter
@Getter
@NoArgsConstructor
public class ProductSearchDto {
	@ApiModelProperty(value = "product 타입(service)", example = "SQUARS")
	private ProductType productType;

	@ApiModelProperty(value = "product 상태", example = "APPROVED")
	private ApprovalStatus status;

	@ApiModelProperty(hidden = true)
	public boolean isValid() {
		return productType != null || status != null;
	}

	@ApiModelProperty(hidden = true)
	public String isValidMessage() {
		return "product Type 또는 status 값을 선택헤 주세요";
	}
}
