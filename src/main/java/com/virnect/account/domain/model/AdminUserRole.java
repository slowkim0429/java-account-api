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

import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.UseStatus;

@Entity
@Getter
@Audited
@Table(name = "admin_user_roles",
	   indexes = {
		   @Index(name = "idx_admin_user_id", columnList = "admin_user_id"),
		   @Index(name = "idx_role", columnList = "role"),
		   @Index(name = "idx_status", columnList = "status"),
	   }
)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class AdminUserRole extends BaseEntity {
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(name = "admin_user_id", nullable = false)
	private Long adminUserId;

	@Column(name = "role", nullable = false, length = 100)
	@Enumerated(EnumType.STRING)
	private Role role;

	@Column(name = "status", nullable = false, length = 50)
	@Enumerated(EnumType.STRING)
	private UseStatus status = UseStatus.USE;

	public static AdminUserRole of(Long adminUserId, Role role) {
		AdminUserRole adminUserRole = new AdminUserRole();
		adminUserRole.adminUserId = adminUserId;
		adminUserRole.role = role;
		return adminUserRole;
	}

	public void delete() {
		this.status = UseStatus.DELETE;
	}
}
