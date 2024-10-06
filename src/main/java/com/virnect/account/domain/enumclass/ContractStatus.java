package com.virnect.account.domain.enumclass;

public enum ContractStatus {
	REQUEST,
	PENDING,
	FAILED,
	CANCELED,
	PROCESSING,
	COMPLETED,
	TERMINATION;

	public boolean isRequest() {
		return this.equals(REQUEST);
	}

	public boolean isPending() {
		return this.equals(PENDING);
	}

	public boolean isFailed() {
		return this.equals(FAILED);
	}

	public boolean isCanceled() {
		return this.equals(CANCELED);
	}

	public boolean isProcessing() {
		return this.equals(PROCESSING);
	}

	public boolean isCompleted() {
		return this.equals(COMPLETED);
	}

	public boolean isTermination() {
		return this.equals(TERMINATION);
	}

	public boolean isOrganizationLicenseHandleStatus() {
		return isProcessing() || isCanceled() || isTermination() || isPending();
	}
}
