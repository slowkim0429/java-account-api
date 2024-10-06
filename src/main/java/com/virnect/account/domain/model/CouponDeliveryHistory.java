package com.virnect.account.domain.model;

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

import com.virnect.account.domain.converter.ValueConverter;

@Entity
@Audited
@Getter
@Table(name = "coupon_delivery_history",
	indexes = {
		@Index(name = "idx_coupon_id", columnList = "coupon_id"),
		@Index(name = "idx_receiver_user_id", columnList = "receiver_user_id"),
		@Index(name = "idx_receiver_email", columnList = "receiver_email"),
		@Index(name = "idx_receiver_email_domain", columnList = "receiver_email_domain")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CouponDeliveryHistory extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "coupon_id", nullable = false)
	private Long couponId;

	@Column(name = "event_popup_id", nullable = false)
	private Long eventPopupId;

	@Column(name = "receiver_user_id")
	private Long receiverUserId;

	@Column(name = "receiver_email", length = 100, nullable = false)
	private String receiverEmail;

	@Column(name = "receiver_email_domain", nullable = false)
	private String receiverEmailDomain;

	private CouponDeliveryHistory(Long couponId, Long eventPopupId, Long receiverUserId, String receiverEmail) {
		this.couponId = couponId;
		this.eventPopupId = eventPopupId;
		this.receiverUserId = receiverUserId;
		this.receiverEmail = receiverEmail;
		this.receiverEmailDomain = ValueConverter.getEmailDomain(receiverEmail);
	}

	public static CouponDeliveryHistory create(
		Long couponId, Long eventPopupId, Long receiverUserId, String receiverEmail
	) {
		return new CouponDeliveryHistory(couponId, eventPopupId, receiverUserId, receiverEmail);
	}
}
