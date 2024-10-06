package com.virnect.account.port.outbound;

public interface RedisRepository {

	void setObjectValue(String key, Object value);

	void setObjectValue(String key, Object value, long timeout);

	void setObjectValueAsSecond(String key, Object value, long timeout);

	void setAdminRefreshToken(String adminId, Object value, long timeout);

	void setAdminResetPasswordAsSecond(String email, Object value, long timeout);

	String getStringValue(String key);

	String getAdminRefreshToken(String adminId);

	String getAdminResetPassword(String email);

	boolean deleteObjectValue(String key);

	Boolean deleteAdminRefreshToken(String adminId);
}
