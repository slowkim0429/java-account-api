package com.virnect.account.domain.enumclass;

public enum ApprovalStatus {
	REGISTER,
	REVIEWING,
	PENDING,
	REJECT,
	CANCEL,
	APPROVED;

	public boolean isImmutableStatus() {
		return this.equals(APPROVED) || this.equals(REJECT) || this.equals(CANCEL);
	}

	public boolean isApproved() {
		return this.equals(APPROVED);
	}

	public boolean isRejected() {
		return this.equals(REJECT);
	}

	public boolean isNotApproved() {
		return !this.isApproved();
	}

	public boolean isNotRegister() {
		return !this.equals(REGISTER);
	}

	public boolean isRegister() {
		return this.equals(REGISTER);
	}

	public static boolean isAlterableAuthorityStatus(String maybeStatus) {
		ApprovalStatus result = null;

		for (ApprovalStatus approvalStatus : ApprovalStatus.values()) {
			if (approvalStatus.toString().equals(maybeStatus)) {
				result = ApprovalStatus.valueOf(maybeStatus);
				break;
			}
		}

		return ApprovalStatus.APPROVED.equals(result) || ApprovalStatus.REJECT.equals(result);
	}

}
