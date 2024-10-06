package com.virnect.account.domain.enumclass;

public enum EventType {
	MARKETING, SUBMISSION;

	public static EventType nullableValueOf(String name) {
		if (name == null || name.isBlank()) {
			return null;
		}
		return EventType.valueOf(name);
	}

	public boolean isMarketing() {
		return EventType.MARKETING.equals(this);
	}

	public boolean isSubmission() {
		return EventType.SUBMISSION.equals(this);
	}
}
