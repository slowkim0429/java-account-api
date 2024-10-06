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
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.mobilemanagement.MobileManagementUpdateRequestDto;

@Entity
@Getter
@Audited
@Table(name = "mobile_managements")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class MobileManagement extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Setter
	@Column(name = "control_mode_password", length = 100, insertable = false, nullable = false)
	private String password;

	@Column(name = "notice_message", length = 200, insertable = false)
	private String message;

	@Column(name = "notice_is_exposed", nullable = false, insertable = false)
	private Boolean isExposed;

	public void updateNotice(MobileManagementUpdateRequestDto mobileManagementUpdateRequestDto) {
		this.message = mobileManagementUpdateRequestDto.getMessage();
		this.isExposed = mobileManagementUpdateRequestDto.getIsExposed();
	}
}
