package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.ForceUpdateType;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.model.MobileForceUpdateMinimumVersion;
import com.virnect.account.util.ZonedDateTimeUtil;

@ApiModel
@Getter
public class MobileForceUpdateMinimumVersionRevisionResponseDto {

	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "Mobile force update minimum version Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(name = "bundle ID", example = "com.virnect.appname")
	private String bundleId;

	@ApiModelProperty(name = "version", example = "1.0.0")
	private String version;

	@ApiModelProperty(name = "강제 업데이트 타입", example = "UNUSED")
	private ForceUpdateType forceUpdateType;

	@ApiModelProperty(value = "등록자", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "등록일시", example = "2022-01-03 11:15:30")
	private String createdDate;

	@ApiModelProperty(value = "수정자(Id)", example = "1000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정 일자", example = "2022-01-03 11:15:30")
	private String updatedDate;

	private MobileForceUpdateMinimumVersionRevisionResponseDto(
		RevisionType revisionType, MobileForceUpdateMinimumVersion mobileForceUpdateMinimumVersion
	) {
		this.revisionType = revisionType;
		this.id = mobileForceUpdateMinimumVersion.getId();
		this.bundleId = mobileForceUpdateMinimumVersion.getBundleId();
		this.version = mobileForceUpdateMinimumVersion.getVersion();
		this.forceUpdateType = mobileForceUpdateMinimumVersion.getForceUpdateType();
		this.createdBy = mobileForceUpdateMinimumVersion.getCreatedBy();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(
			mobileForceUpdateMinimumVersion.getCreatedDate());
		this.updatedBy = mobileForceUpdateMinimumVersion.getLastModifiedBy();
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(
			mobileForceUpdateMinimumVersion.getUpdatedDate());
	}

	public static MobileForceUpdateMinimumVersionRevisionResponseDto of(
		Byte representation, MobileForceUpdateMinimumVersion mobileForceUpdateMinimumVersion
	) {
		return new MobileForceUpdateMinimumVersionRevisionResponseDto(
			RevisionType.valueOf(representation), mobileForceUpdateMinimumVersion);
	}
}
