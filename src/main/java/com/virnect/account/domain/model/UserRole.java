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
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Getter
@Audited
@Table(name = "user_roles",
	   indexes = {
		   @Index(name = "idx_user_id", columnList = "user_id"),
		   @Index(name = "idx_role", columnList = "role"),
		   @Index(name = "idx_status", columnList = "status"),
	   }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserRole extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "user_id", nullable = false)
	private Long userId;

	@Column(name = "role", nullable = false, length = 100)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private UseStatus status = UseStatus.USE;

	@Builder(builderClassName = "UserRoleBuilder", builderMethodName = "userRoleBuilder")
	public UserRole(Long userId, Role role) {
		this.userId = userId;
		this.role = role;
	}

	public void setUseRole(UseStatus status) {
		this.status = status;
	}

	public boolean isAdminRole() {
		return Role.ROLE_ADMIN_MASTER.equals(role) || Role.ROLE_ADMIN_USER.equals(role);
	}
}
