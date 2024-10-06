package com.virnect.account.adapter.inbound.dto.response;

import java.time.ZonedDateTime;

import com.querydsl.core.annotations.QueryProjection;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@ApiModel
public class ItemPaymentLinkResponseDto {
	@ApiModelProperty(value = "Item Payment Link Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Item Id", example = "1000000000")
	private Long itemId;

	@ApiModelProperty(value = "User Id", example = "1000000000")
	private Long userId;

	@ApiModelProperty(value = "Item Payment Link Receiver Email", example = "ljk@virnect.com")
	private String email;

	@ApiModelProperty(value = "Item Payment Link Receiver Email Domain", example = "virnect.com")
	private String emailDomain;

	@ApiModelProperty(value = "Link Expired Date", example = "2022-01-03T11:15:30")
	private String expiredDate;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03T11:15:30")
	private String updatedDate;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "등록일시", example = "2022-01-03T11:15:30")
	private String createdDate;

	@QueryProjection
	public ItemPaymentLinkResponseDto(
		Long id, Long itemId, Long userId, String email, String emailDomain, ZonedDateTime expiredDate, Long updatedBy,
		ZonedDateTime updatedDate,
		Long createdBy, ZonedDateTime createdDate
	) {
		this.id = id;
		this.itemId = itemId;
		this.userId = userId;
		this.email = email;
		this.emailDomain = emailDomain;
		this.expiredDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(expiredDate);
		this.updatedBy = updatedBy;
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(updatedDate);
		this.createdBy = createdBy;
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(createdDate);
	}
}
