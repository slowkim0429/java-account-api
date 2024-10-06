package com.virnect.account.adapter.inbound.dto.request.product;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.domain.enumclass.ProductType;

@Setter
@Getter
@NoArgsConstructor
public class ProductCreateRequestDto {
	@ApiModelProperty(value = "Product Type", example = "WORKSPACE", required = true)
	@NotNull(message = "Product Type은 반드시 입력되어야 합니다.")
	private ProductType productType;

	@ApiModelProperty(value = "Product 명", example = "워크스페이스", position = 1, required = true)
	@NotBlank(message = "Product명은 반드시 입력되어야 합니다.")
	@Size(max = 100)
	private String name;
}
