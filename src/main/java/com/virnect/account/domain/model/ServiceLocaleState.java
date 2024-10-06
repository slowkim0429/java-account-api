package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import org.hibernate.envers.Audited;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Entity
@Audited
@Table(name = "service_locale_states")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ServiceLocaleState extends BaseEntity{
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "code", nullable = false, length = 30)
	private String code;

	@Column(name = "name", nullable = false, length = 100)
	private String name;

	@Column(name = "locale_code", nullable = false, length = 30)
	private String localeCode;
}
