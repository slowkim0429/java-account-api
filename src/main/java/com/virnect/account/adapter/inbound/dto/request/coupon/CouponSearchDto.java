package com.virnect.account.adapter.inbound.dto.request.coupon;

import org.apache.commons.lang3.StringUtils;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.ApprovalStatusSubset;
import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.adapter.inbound.dto.request.validate.UseStatusSubset;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CouponType;
import com.virnect.account.domain.enumclass.UseStatus;

@Getter
@Setter
@ApiModel
public class CouponSearchDto {
	@CommonEnum(enumClass = CouponType.class)
	@ApiModelProperty(value = "쿠폰 타입", example = "UPGRADE_LICENSE_ATTRIBUTE")
	private String couponType;

	@ApprovalStatusSubset(anyOf = {ApprovalStatus.REGISTER, ApprovalStatus.APPROVED, ApprovalStatus.REJECT})
	@ApiModelProperty(value = "쿠폰 승인 상태", example = "REGISTER")
	private String status;

	@UseStatusSubset(anyOf = {UseStatus.USE, UseStatus.UNUSE})
	@ApiModelProperty(value = "사용 상태", example = "USE")
	private String useStatus;

	@ApiModelProperty(value = "coupon name", example = "Happy Christmas Coupon")
	private String name;

	@ApiModelProperty(hidden = true)
	public CouponType couponTypeValueOf() {
		return StringUtils.isBlank(couponType) ? null : CouponType.valueOf(couponType);
	}

	@ApiModelProperty(hidden = true)
	public ApprovalStatus statusValueOf() {
		return StringUtils.isBlank(status) ? null : ApprovalStatus.valueOf(status);
	}

	@ApiModelProperty(hidden = true)
	public UseStatus useStatusValueOf() {
		return StringUtils.isBlank(useStatus) ? null : UseStatus.valueOf(useStatus);
	}
}
