package com.virnect.account.adapter.outbound;

import java.util.concurrent.TimeUnit;

import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import lombok.RequiredArgsConstructor;

import com.virnect.account.port.outbound.RedisRepository;

@Service
@RequiredArgsConstructor
public class RedisRepositoryImpl implements RedisRepository {
	private final RedisTemplate<String, Object> redisTemplate;

	private final String ADMIN_PREFIX_FOR_REFRESH_TOKEN = "admin:%s:refresh-token";
	private final String ADMIN_PREFIX_FOR_RESET_PASSWORD_AUTH_KEY = "admin:%s:reset-password:auth-key";

	@Override
	public void setObjectValue(String key, Object value) {
		redisTemplate.opsForValue().set(key, value);
	}

	@Override
	public void setObjectValue(String key, Object value, long timeout) {
		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
	}

	@Override
	public void setObjectValueAsSecond(String key, Object value, long timeout) {
		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
	}

	@Override
	public void setAdminRefreshToken(String adminId, Object value, long timeout) {
		String key = String.format(ADMIN_PREFIX_FOR_REFRESH_TOKEN, adminId);
		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.MILLISECONDS);
	}

	@Override
	public void setAdminResetPasswordAsSecond(String email, Object value, long timeout) {
		String key = String.format(ADMIN_PREFIX_FOR_RESET_PASSWORD_AUTH_KEY, email);
		redisTemplate.opsForValue().set(key, value, timeout, TimeUnit.SECONDS);
	}

	@Override
	public String getStringValue(String key) {
		return (String)redisTemplate.opsForValue().get(key);
	}

	@Override
	public String getAdminRefreshToken(String adminId) {
		String key = String.format(ADMIN_PREFIX_FOR_REFRESH_TOKEN, adminId);
		return (String)redisTemplate.opsForValue().get(key);
	}

	@Override
	public String getAdminResetPassword(String email) {
		String key = String.format(ADMIN_PREFIX_FOR_RESET_PASSWORD_AUTH_KEY, email);
		return (String)redisTemplate.opsForValue().get(key);
	}

	@Override
	public boolean deleteObjectValue(String key) {
		return redisTemplate.delete(key);
	}

	@Override
	public Boolean deleteAdminRefreshToken(String adminId) {
		String key = String.format(ADMIN_PREFIX_FOR_REFRESH_TOKEN, adminId);
		return redisTemplate.delete(key);
	}
}
