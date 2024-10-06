package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.appversion.MobileForceUpdateMinimumVersionUpdateRequestDto;
import com.virnect.account.domain.enumclass.ForceUpdateType;

@Entity
@Getter
@Audited
@Table(name = "mobile_force_update_minimum_versions")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MobileForceUpdateMinimumVersion extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "bundle_id", nullable = false)
	private String bundleId;

	@Column(name = "version", length = 50, nullable = false)
	private String version;

	@Enumerated(EnumType.STRING)
	@Column(name = "force_update_type", length = 50, nullable = false)
	private ForceUpdateType forceUpdateType;

	public void update(
		MobileForceUpdateMinimumVersionUpdateRequestDto mobileForceUpdateMinimumVersionUpdateRequestDto
	) {
		this.bundleId = mobileForceUpdateMinimumVersionUpdateRequestDto.getBundleId();
		this.version = mobileForceUpdateMinimumVersionUpdateRequestDto.getVersion();
		this.forceUpdateType = mobileForceUpdateMinimumVersionUpdateRequestDto.valueOfForceUpdateType();
	}
}
