package com.virnect.account.domain.enumclass;

public enum Role {
	ROLE_USER,
	ROLE_ORGANIZATION_OWNER,
	ROLE_ADMIN_MASTER,
	ROLE_ADMIN_USER;

	public boolean isOrganizationRole() {
		return this.equals(ROLE_ORGANIZATION_OWNER);
	}
}
