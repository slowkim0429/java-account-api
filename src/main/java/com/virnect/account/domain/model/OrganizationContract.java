package com.virnect.account.domain.model;

import java.time.ZonedDateTime;

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

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationContractRequestDto;
import com.virnect.account.domain.enumclass.ContractStatus;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;

@Entity
@Getter
@Audited
@Table(name = "organization_contracts",
	indexes = {
		@Index(name = "idx_organization_id", columnList = "organization_id"),
		@Index(name = "idx_contract_id", columnList = "contract_id"),
		@Index(name = "idx_item_id", columnList = "item_id"),
		@Index(name = "idx_status", columnList = "status")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OrganizationContract extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "organization_id", nullable = false)
	private Long organizationId;

	@Column(name = "contract_id", nullable = false)
	private Long contractId;

	@Column(name = "item_id", nullable = false)
	private Long itemId;

	@Column(name = "item_name", nullable = false, length = 100)
	private String itemName;

	@Column(name = "item_type", nullable = false, length = 20)
	@Enumerated(EnumType.STRING)
	private ItemType itemType;

	@Column(name = "payment_type", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private PaymentType paymentType;

	@Column(name = "recurring_interval", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private RecurringIntervalType recurringInterval;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private ContractStatus status;

	@Column(name = "start_date", nullable = false)
	private ZonedDateTime startDate;

	@Column(name = "end_date", nullable = false)
	private ZonedDateTime endDate;

	@Column(name = "coupon_id")
	private Long couponId;

	private OrganizationContract(
		OrganizationContractRequestDto organizationContractRequestDto
	) {
		this.organizationId = organizationContractRequestDto.getOrganizationId();
		this.contractId = organizationContractRequestDto.getContractId();
		this.itemId = organizationContractRequestDto.getItemId();
		this.itemName = organizationContractRequestDto.getItemName();
		this.itemType = organizationContractRequestDto.getItemType();
		this.paymentType = organizationContractRequestDto.getPaymentType();
		this.recurringInterval = organizationContractRequestDto.getRecurringInterval();
		this.status = organizationContractRequestDto.getStatus();
		this.startDate = organizationContractRequestDto.getStartDate();
		this.endDate = organizationContractRequestDto.getEndDate();
		this.couponId = organizationContractRequestDto.getCouponId();
	}

	public static OrganizationContract from(OrganizationContractRequestDto organizationContractRequestDto) {
		return new OrganizationContract(organizationContractRequestDto);
	}

	public void setStatus(ContractStatus status) {
		this.status = status;
	}

	public void setStartDate(ZonedDateTime startDate) {
		this.startDate = startDate;
	}

	public void setEndDate(ZonedDateTime endDate) {
		this.endDate = endDate;
	}
}
