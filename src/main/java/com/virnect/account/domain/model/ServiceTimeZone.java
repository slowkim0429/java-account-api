package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Getter
@Table(name = "service_time_zones",
	indexes = {
		@Index(name = "idx_locale_code", columnList = "locale_code")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceTimeZone {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "locale_code", length = 30)
	private String localeCode;

	@Column(name = "zone_id", nullable = false, length = 50)
	private String zoneId;

	@Column(name = "utc_offset", nullable = false, length = 10)
	private String utcOffset;

	@Column(name = "name", length = 60)
	private String name;
}
