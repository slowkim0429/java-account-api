package com.virnect.account.adapter.inbound.dto.request.product;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.domain.enumclass.ApprovalStatus;

@Setter
@Getter
@NoArgsConstructor
public class ProductStatusChangeRequestDto {
	@ApiModelProperty(value = "Product id", example = "1000000000", required = true)
	@NotNull(message = "Product Id는 반드시 입력되어야 합니다.")
	@Min(1000000000)
	private Long productId;

	@ApiModelProperty(
		value = "REGISTER,REVIEWING,PENDING,REJECT,APPROVED", position = 1,
		example = "REGISTER", required = true
	)
	@NotNull(message = "변경할 Account 상태가 반드시 입력되어야 합니다.")
	private ApprovalStatus status;
}
