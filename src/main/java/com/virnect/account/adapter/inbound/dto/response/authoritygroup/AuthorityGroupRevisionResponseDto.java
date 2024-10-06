package com.virnect.account.adapter.inbound.dto.response.authoritygroup;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.AuthorityGroup;
import com.virnect.account.util.ZonedDateTimeUtil;

@ApiModel
@Getter
@Setter
public class AuthorityGroupRevisionResponseDto {

	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "id", example = "10000000000")
	private Long id;

	@ApiModelProperty(value = "name", example = "Admin master")
	private String name;

	@ApiModelProperty(value = "description", example = "For admin master")
	private String description;

	@ApiModelProperty(value = "status", example = "USE")
	private UseStatus status;

	@ApiModelProperty(value = "생성자", example = "10000000000")
	private Long createdBy;

	@ApiModelProperty(value = "생성일", example = "2022-11-14 01:44:55")
	private String createdDate;

	@ApiModelProperty(value = "수정자", example = "10000000000")
	private Long updatedBy;

	@ApiModelProperty(value = "수정일", example = "2022-11-14 01:44:55")
	private String updatedDate;

	private AuthorityGroupRevisionResponseDto(
		RevisionType revisionType, AuthorityGroup authorityGroup
	) {
		this.revisionType = revisionType;
		this.id = authorityGroup.getId();
		this.name = authorityGroup.getName();
		this.description = authorityGroup.getDescription();
		this.status = authorityGroup.getStatus();
		this.createdBy = authorityGroup.getCreatedBy();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(authorityGroup.getCreatedDate());
		this.updatedBy = authorityGroup.getLastModifiedBy();
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(authorityGroup.getUpdatedDate());
	}

	public static AuthorityGroupRevisionResponseDto of(Byte representation, AuthorityGroup authorityGroup) {
		return new AuthorityGroupRevisionResponseDto(RevisionType.valueOf(representation), authorityGroup);
	}
}
