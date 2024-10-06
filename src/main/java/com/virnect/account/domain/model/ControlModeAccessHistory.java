package com.virnect.account.domain.model;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Table;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.AccessResultType;

@Entity
@Getter
@Table(name = "control_mode_access_histories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ControlModeAccessHistory extends BaseEntity {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Enumerated(EnumType.STRING)
	@Column(name = "access_result_type", length = 30, nullable = false)
	private AccessResultType accessResultType;

	private ControlModeAccessHistory(AccessResultType accessResultType) {
		this.accessResultType = accessResultType;
	}

	public static ControlModeAccessHistory createFailedHistory() {
		return new ControlModeAccessHistory(AccessResultType.FAILED);
	}

	public static ControlModeAccessHistory createSucceededHistory() {
		return new ControlModeAccessHistory(AccessResultType.SUCCEEDED);
	}
}
