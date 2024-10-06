package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementRequestDto;
import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Getter
@Audited
@Table(name = "email_customizing_managements",
	indexes = {
		@Index(name = "idx_email_type", columnList = "email_type"),
		@Index(name = "idx_use_status", columnList = "use_status"),

	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class EmailCustomizingManagement extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "email_type", nullable = false, length = 100)
	@Enumerated(EnumType.STRING)
	private Mail emailType;

	@Column(name = "contents_inline_image_url", nullable = false, length = 255)
	private String contentsInlineImageUrl;

	@Column(name = "description", nullable = true, length = 255)
	private String description;

	@Column(name = "use_status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private UseStatus useStatus = UseStatus.UNUSE;

	private EmailCustomizingManagement(
		Mail emailType, String contentsInlineImageUrl, String description
	) {
		this.emailType = emailType;
		this.contentsInlineImageUrl = contentsInlineImageUrl;
		this.description = description;
	}

	public static EmailCustomizingManagement of(
		EmailManagementRequestDto emailManagementRequestDto, String contentsInlineImageUrl
	) {
		return new EmailCustomizingManagement(
			emailManagementRequestDto.getEmailType(), contentsInlineImageUrl,
			emailManagementRequestDto.getDescription()
		);
	}

	public void setUseStatus(UseStatus useStatus) {
		this.useStatus = useStatus;
	}

	public void unuse() {
		this.useStatus = UseStatus.UNUSE;
	}

	public void setEmailType(Mail emailType) {
		this.emailType = emailType;
	}

	public void setContentsInlineImageUrl(String contentsInlineImageUrl) {
		this.contentsInlineImageUrl = contentsInlineImageUrl;
	}

	public void setDescription(String description) {
		this.description = description;
	}
}
