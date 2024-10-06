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

import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupModifyRequestDto;
import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Audited
@Getter
@Table(name = "authority_groups",
	indexes = {
		@Index(name = "idx_status", columnList = "status")
	})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AuthorityGroup extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "name", nullable = false, length = 50)
	private String name;

	@Column(name = "description", length = 250)
	private String description;

	@Column(name = "status", nullable = false, length = 10)
	@Enumerated(EnumType.STRING)
	private UseStatus status = UseStatus.UNUSE;

	private AuthorityGroup(String name, String description) {
		this.name = name;
		this.description = description;
	}

	public static AuthorityGroup create(AuthorityGroupCreateRequestDto authorityGroupCreateRequestDto) {
		return new AuthorityGroup(
			authorityGroupCreateRequestDto.getName(),
			authorityGroupCreateRequestDto.getDescription()
		);
	}

	public void update(AuthorityGroupModifyRequestDto authorityGroupModifyRequestDto) {
		this.name = authorityGroupModifyRequestDto.getName();
		this.description = authorityGroupModifyRequestDto.getDescription();
	}

	public void updateStatus(UseStatus status) {
		this.status = status;
	}
}

