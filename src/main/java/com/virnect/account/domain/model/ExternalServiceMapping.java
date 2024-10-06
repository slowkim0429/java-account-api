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

import com.virnect.account.domain.enumclass.ExternalDomain;
import com.virnect.account.domain.enumclass.ExternalService;
import com.virnect.account.domain.enumclass.InternalDomain;

@Entity
@Getter
@Audited
@Table(name = "external_service_mappings",
	indexes = {
		@Index(name = "idx_external_domain", columnList = "external_domain"),
		@Index(name = "idx_internal_domain", columnList = "internal_domain"),
		@Index(name = "idx_internal_mapping_id", columnList = "internal_mapping_id"),
		@Index(name = "idx_is_latest_mapping_succeeded", columnList = "is_latest_mapping_succeeded"),
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ExternalServiceMapping extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "external_service", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private ExternalService externalService = ExternalService.HUBSPOT;

	@Column(name = "external_domain", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private ExternalDomain externalDomain;

	@Column(name = "internal_domain", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private InternalDomain internalDomain;

	@Column(name = "external_mapping_id", length = 100)
	private String externalMappingId;

	@Column(name = "internal_mapping_id", nullable = false)
	private Long internalMappingId;

	@Column(name = "is_latest_mapping_succeeded", nullable = false)
	private Boolean isLatestMappingSucceeded;

	@Column(name = "is_syncable", nullable = false)
	private Boolean isSyncable;

	public ExternalServiceMapping(
		ExternalDomain externalDomain, InternalDomain internalDomain, Long internalMappingId,
		Boolean isLatestMappingSucceeded
	) {
		this.externalDomain = externalDomain;
		this.internalDomain = internalDomain;
		this.internalMappingId = internalMappingId;
		this.isLatestMappingSucceeded = isLatestMappingSucceeded;
		this.isSyncable = true;
	}

	public void setExternalMappingId(Long externalMappingId) {
		this.externalMappingId = String.valueOf(externalMappingId);
	}

	public void setLatestMappingSucceeded(Boolean latestMappingSucceeded) {
		isLatestMappingSucceeded = latestMappingSucceeded;
	}

	public void setIsSyncable(Boolean isSyncable) {
		this.isSyncable = isSyncable;
	}

	public boolean isSyncableSame(Boolean isSyncable) {
		return this.isSyncable.equals(isSyncable);
	}
}
