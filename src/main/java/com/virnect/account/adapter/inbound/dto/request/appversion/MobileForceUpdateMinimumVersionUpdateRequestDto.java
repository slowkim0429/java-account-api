package com.virnect.account.adapter.inbound.dto.request.appversion;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.ForceUpdateType;

@ApiModel
@Getter
@Setter
public class MobileForceUpdateMinimumVersionUpdateRequestDto {

	@ApiModelProperty(value = "Bundle ID", required = true)
	@Size(max = 255)
	@Pattern(regexp = "^[a-zA-Z0-9_-]{1,63}(\\.[a-zA-Z0-9_-]{1,63})+$")
	@NotBlank
	private String bundleId;

	@ApiModelProperty(value = "Version", required = true)
	@Size(max = 50)
	@Pattern(regexp = "^([0-9]{1,10})\\.([0-9]{1,10})\\.([0-9]{1,10})$")
	@NotBlank
	private String version;

	@ApiModelProperty(value = "Force update type", required = true)
	@CommonEnum(enumClass = ForceUpdateType.class)
	@NotBlank
	private String forceUpdateType;

	@ApiModelProperty(hidden = true)
	public ForceUpdateType valueOfForceUpdateType() {
		return ForceUpdateType.valueOf(forceUpdateType);
	}
}
