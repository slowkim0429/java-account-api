package com.virnect.account.domain.enumclass;

import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;

public enum RevisionType {
	CREATE,
	UPDATE,
	DELETE;

	public static RevisionType valueOf(Byte representation) {
		switch (representation) {
			case 0: {
				return RevisionType.CREATE;
			}
			case 1: {
				return RevisionType.UPDATE;
			}
			case 2: {
				return RevisionType.DELETE;
			}
			default: {
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR);
			}
		}
	}
}
