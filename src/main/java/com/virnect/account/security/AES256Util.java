package com.virnect.account.security;

import static java.nio.charset.StandardCharsets.*;

import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
public class AES256Util {
	private static final String ALGORITHMS = "AES/CBC/PKCS5Padding";

	private static String secretKey;
	private static String initVector;

	public static String encrypt(String value) {
		if (StringUtils.isBlank(value)) {
			return null;
		}

		try {
			Cipher cipher = Cipher.getInstance(ALGORITHMS);

			/* KEY + IV setting */
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(UTF_8));
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(UTF_8), "AES");

			/* Ciphering */
			cipher.init(Cipher.ENCRYPT_MODE, secretKeySpec, iv);
			byte[] encrypted = cipher.doFinal(value.getBytes());
			return new String(Base64.getEncoder().encode(encrypted), UTF_8);
		} catch (Exception ex) {
			log.error("AES 256 encrypt fail.", ex);
		}

		return null;
	}

	public static String decrypt(String encrypted) {
		if (StringUtils.isBlank(encrypted)) {
			return null;
		}

		try {
			IvParameterSpec iv = new IvParameterSpec(initVector.getBytes(UTF_8));
			SecretKeySpec secretKeySpec = new SecretKeySpec(secretKey.getBytes(UTF_8), "AES");

			Cipher cipher = Cipher.getInstance(ALGORITHMS);
			cipher.init(Cipher.DECRYPT_MODE, secretKeySpec, iv);
			byte[] original = cipher.doFinal(Base64.getDecoder().decode(encrypted));

			return new String(original);
		} catch (Exception ex) {
			log.error("AES 256 decrypt fail.", ex);
		}

		return null;
	}

	@Value("${encrypt.key}")
	private void setSecretKey(String key) {
		secretKey = key;
		initVector = secretKey.substring(0, 16);
	}
}
