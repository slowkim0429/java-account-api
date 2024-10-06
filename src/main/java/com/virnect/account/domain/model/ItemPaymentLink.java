package com.virnect.account.domain.model;

import java.time.ZonedDateTime;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.item.ItemPaymentLinkRequestDto;
import com.virnect.account.domain.converter.ValueConverter;

@Entity
@Getter
@Audited
@Table(name = "item_payment_links",
	indexes = {
		@Index(name = "idx_item_id", columnList = "item_id")
		, @Index(name = "idx_user_id", columnList = "user_id")
		, @Index(name = "idx_email", columnList = "email")
		, @Index(name = "idx_email_domain", columnList = "email_domain")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ItemPaymentLink extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "item_id", nullable = false)
	private Long itemId;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "email", nullable = false)
	private String email;

	@Column(name = "email_domain", nullable = false)
	private String emailDomain;

	@Column(name = "expired_at", nullable = false)
	private ZonedDateTime expiredDate;

	public static ItemPaymentLink of(
		Long itemId, Long userId, ItemPaymentLinkRequestDto itemPaymentLinkRequestDto,
		ZonedDateTime expiredDate
	) {
		ItemPaymentLink itemPaymentLink = new ItemPaymentLink();
		itemPaymentLink.itemId = itemId;
		itemPaymentLink.userId = userId;
		itemPaymentLink.email = itemPaymentLinkRequestDto.getEmail();
		itemPaymentLink.emailDomain = ValueConverter.getEmailDomain(itemPaymentLink.getEmail());
		itemPaymentLink.expiredDate = expiredDate;
		return itemPaymentLink;
	}
}
