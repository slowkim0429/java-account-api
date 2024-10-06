package com.virnect.account.adapter.inbound.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/admin_users.sql",
	"classpath:data/license_attributes.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LicenseAttributeAdminControllerTest {

	@Autowired
	public MockMvc mockMvc;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private RedisRepository redisRepository;

	private TokenResponseDto adminTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeAll
	void setUp() {
		adminSetUp();
	}

	void adminSetUp() {
		Long adminUserId = 1000000001L;
		String email = "admin-user2@virnect.com";
		adminTokenResponseDto = tokenProvider.createAdminToken(
			adminUserId, email, null, List.of(Role.ROLE_ADMIN_USER)
		);

		redisRepository.setAdminRefreshToken(
			adminUserId.toString(), adminTokenResponseDto.getRefreshToken(), tokenProvider.getRefreshTokenExpireTime()
		);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteAdminRefreshToken(
			tokenProvider.getUserNameFromJwtToken(adminTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("GET License attributes - 조회 성공 OK 200")
	void getLicenseAttributes() throws Exception {
		// given
		String url = "/api/admin/licenses/1000000002/attributes/attribute";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	@DisplayName("GET License attributes - 존재하지 않는 attribute 조회 성공 OK 200")
	void getLicenseAttributesButNotFound() throws Exception {
		// given
		String url = "/api/admin/licenses/9999999999/attributes/attribute";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	@DisplayName("GET License additional attributes - additionalAttribute 조회 성공 OK 200")
	void getLicenseAdditionalAttributes() throws Exception {
		// given
		String url = "/api/admin/licenses/1000000002/attributes/additional";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isNotEmpty());
	}

	@Test
	@DisplayName("GET License additional attributes - 존재하지 않는 additionalAttribute 조회 성공 OK 200")
	void getLicenseAdditionalAttributesButNotFound() throws Exception {
		// given
		String url = "/api/admin/licenses/9999999999/attributes/additional";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$").isEmpty());
	}

	@Test
	@DisplayName("GET License Attribute Revision 목록 조회")
	void getLicenseAttributeRevisions() throws Exception {
		String licenseId = "1000000000";
		LicenseAttributeType licenseAttributeType = LicenseAttributeType.MAXIMUM_GROUP;
		// when
		String url = String.format(
			"/api/admin/licenses/%s/attributes/revisions?licenseAttributeType=%s", licenseId,
			licenseAttributeType.name()
		);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET License Additional Attribute Revision 목록 조회")
	void getLicenseAdditionalAttributeRevisions() throws Exception {
		String licenseId = "1000000000";
		LicenseAdditionalAttributeType licenseAdditionalAttributeType = LicenseAdditionalAttributeType.MAXIMUM_VIEW;
		// when
		String url = String.format(
			"/api/admin/licenses/%s/attributes/additional/revisions?licenseAdditionalAttributeType=%s", licenseId,
			licenseAdditionalAttributeType.name()
		);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk());
	}
}
