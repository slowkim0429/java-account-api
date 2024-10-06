package com.virnect.account.security;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("test")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
class AES256UtilTest {
	@Test
	@DisplayName("AES256Util 인코딩/디코딩 테스트")
	void AES256Util_암호화_테스트() {
		// given
		String email = "abc@virnect.com";
		String encryptedEmail = AES256Util.encrypt(email);

		// when
		String decryptedEmail = AES256Util.decrypt(encryptedEmail);

		// then
		Assertions.assertEquals(email, decryptedEmail);
	}
}
