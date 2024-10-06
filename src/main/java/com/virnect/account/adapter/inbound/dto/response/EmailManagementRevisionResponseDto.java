package com.virnect.account.adapter.inbound.dto.response;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.enumclass.RevisionType;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.EmailCustomizingManagement;
import com.virnect.account.util.ZonedDateTimeUtil;

@ApiModel
@Getter
public class EmailManagementRevisionResponseDto {

	@ApiModelProperty(value = "revision type", example = "CREATE")
	private RevisionType revisionType;

	@ApiModelProperty(value = "Email Management Id", example = "1000000000")
	private Long id;

	@ApiModelProperty(value = "Email Type", example = "WELCOME")
	private Mail emailType;

	@ApiModelProperty(value = "Email Contents Inline Image Url", example = "https://devfile.squars.io/virnect-platform/.....png")
	private String contentsInlineImageUrl;

	@ApiModelProperty(value = "Description", example = "sign up email")
	private String description;

	@ApiModelProperty(value = "Use Status", example = "UNUSE")
	private UseStatus useStatus;

	@ApiModelProperty(value = "Created Date", example = "2022-01-04T11:44:55")
	private String createdDate;

	@ApiModelProperty(value = "Updated Date", example = "2022-01-04T11:44:55")
	private String updatedDate;

	@ApiModelProperty(value = "Created By User Id", example = "1000000000")
	private Long createdBy;

	@ApiModelProperty(value = "Updated By User Id", example = "1000000000")
	private Long updatedBy;

	public EmailManagementRevisionResponseDto(RevisionType revisionType, EmailCustomizingManagement emailManagement) {
		this.revisionType = revisionType;
		this.id = emailManagement.getId();
		this.emailType = emailManagement.getEmailType();
		this.contentsInlineImageUrl = emailManagement.getContentsInlineImageUrl();
		this.description = emailManagement.getDescription();
		this.useStatus = emailManagement.getUseStatus();
		this.updatedBy = emailManagement.getLastModifiedBy();
		this.updatedDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(emailManagement.getUpdatedDate());
		this.createdBy = emailManagement.getCreatedBy();
		this.createdDate = ZonedDateTimeUtil.convertToStringWithCurrentZoneId(emailManagement.getCreatedDate());
	}

	public static EmailManagementRevisionResponseDto of(
		Byte representation, EmailCustomizingManagement emailManagement
	) {
		return new EmailManagementRevisionResponseDto(RevisionType.valueOf(representation), emailManagement);
	}
}
