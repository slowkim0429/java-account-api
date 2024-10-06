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

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationUpdateRequestDto;
import com.virnect.account.domain.enumclass.CustomerType;
import com.virnect.account.domain.enumclass.OrganizationStatus;
import com.virnect.account.security.AES256Util;

@Entity
@Getter
@Audited
@Table(name = "organizations",
	indexes = {
		@Index(name = "idx_name", columnList = "name"),
		@Index(name = "idx_email", columnList = "email"),
		@Index(name = "idx_vat_identification_number", columnList = "vat_identification_number"),
		@Index(name = "idx_status", columnList = "status"),
		@Index(name = "idx_created_by", columnList = "created_by"),
		@Index(name = "idx_updated_by", columnList = "updated_by"),
		@Index(name = "hubspot_company_id", columnList = "hubspot_company_id"),
		@Index(name = "idx_email_domain", columnList = "email_domain"),
		@Index(name = "idx_state_name", columnList = "state_name"),
		@Index(name = "idx_service_region_locale_mapping_name", columnList = "service_region_locale_mapping_name"),
		@Index(name = "idx_province", columnList = "province")
	}
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Organization extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "customer_type", length = 30)
	@Enumerated(EnumType.STRING)
	private CustomerType customerType;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "vat_identification_number", length = 200)
	private String vatIdentificationNumber;

	@Column(name = "email", nullable = false, length = 200)
	private String email;

	@Column(name = "email_domain", length = 50)
	private String emailDomain;

	@Column(name = "service_region_locale_mapping_id", nullable = false)
	private Long localeId;

	@Column(name = "service_region_locale_mapping_code", nullable = false, length = 30)
	private String localeCode;

	@Column(name = "service_region_locale_mapping_name", nullable = false, length = 100)
	private String localeName;

	@Column(name = "address", length = 200)
	private String address;

	@Column(name = "postal_code", length = 20)
	private String postalCode;

	@Column(name = "city", length = 50)
	private String city;

	@Column(name = "state_code", length = 30)
	private String stateCode;

	@Column(name = "state_name", length = 100)
	private String stateName;

	@Column(name = "province", length = 50)
	private String province;

	@Column(name = "first_name", length = 100)
	private String firstName;

	@Column(name = "last_name", length = 100)
	private String lastName;

	@Column(name = "country_calling_code")
	private Integer countryCallingCode;

	@Column(name = "phone_number", length = 100)
	private String phoneNumber;

	@Column(name = "corporate_number", length = 30)
	private String corporateNumber;

	@Column(name = "status", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private OrganizationStatus status = OrganizationStatus.APPROVED;

	@Column(name = "hubspot_company_id")
	private Long hubspotCompanyId;

	@Column(name = "is_encrypted", nullable = false)
	private Boolean isEncrypted;

	private Organization(
		String name, String email, Long localeId, String localeCode, String localeName, Boolean isEncrypted
	) {
		this.name = name;
		this.email = email;
		this.emailDomain = email.split("@")[1];
		this.localeId = localeId;
		this.localeCode = localeCode;
		this.localeName = localeName;
		this.isEncrypted = isEncrypted;
	}

	public static Organization create(String name, String email, Long localeId, String localeCode, String localeName) {
		return new Organization(name, email, localeId, localeCode, localeName, true);
	}

	public void update(
		OrganizationUpdateRequestDto organizationUpdateRequestDto, ServiceRegionLocaleMapping organizationLocale
	) {
		this.name = organizationUpdateRequestDto.getName();
		this.vatIdentificationNumber = AES256Util.encrypt(organizationUpdateRequestDto.getVatIdentificationNumber());
		this.email = organizationUpdateRequestDto.getEmail();
		this.emailDomain = email.split("@")[1];
		this.address = AES256Util.encrypt(organizationUpdateRequestDto.getAddress());
		this.city = organizationUpdateRequestDto.getCity();
		this.province = organizationUpdateRequestDto.getProvince();
		this.postalCode = organizationUpdateRequestDto.getPostalCode();
		this.localeId = organizationLocale.getId();
		this.localeCode = organizationLocale.getCode();
		this.localeName = organizationLocale.getName();
		this.customerType = organizationUpdateRequestDto.valueOfCustomerType();
		this.firstName = AES256Util.encrypt(organizationUpdateRequestDto.getFirstName());
		this.lastName = AES256Util.encrypt(organizationUpdateRequestDto.getLastName());
		this.countryCallingCode = organizationLocale.getCountryCallingCode();
		this.phoneNumber = AES256Util.encrypt(organizationUpdateRequestDto.getPhoneNumber());
		this.corporateNumber = organizationUpdateRequestDto.getCorporateNumber();
		this.isEncrypted = true;
	}

	public void updateOrganizationState(
		String organizationStateCode, String organizationStateName
	) {
		this.stateCode = organizationStateCode;
		this.stateName = organizationStateName;
	}

	public void delete() {
		this.customerType = null;
		this.firstName = "";
		this.lastName = "";
		this.phoneNumber = "";
		this.corporateNumber = "";
		this.name = "";
		this.vatIdentificationNumber = null;
		this.email = "";
		this.emailDomain = "";
		this.localeCode = "";
		this.localeName = "";
		this.stateCode = "";
		this.stateName = "";
		this.address = "";
		this.postalCode = "";
		this.city = "";
		this.province = "";
		this.status = OrganizationStatus.DELETE;
	}

	public void setHubspotCompanyId(Long hubspotCompanyId) {
		this.hubspotCompanyId = hubspotCompanyId;
	}

	// TODO: 2023/03/23 모든 데이터가 암호화 되면 해당 메서드는 제거합니다.
	public void encodeOrganizationData() {
		if (isEncrypted) {
			return;
		}

		firstName = AES256Util.encrypt(firstName);
		lastName = AES256Util.encrypt(lastName);
		phoneNumber = AES256Util.encrypt(phoneNumber);
		address = AES256Util.encrypt(address);
		vatIdentificationNumber = AES256Util.encrypt(vatIdentificationNumber);
		isEncrypted = true;
	}
}
