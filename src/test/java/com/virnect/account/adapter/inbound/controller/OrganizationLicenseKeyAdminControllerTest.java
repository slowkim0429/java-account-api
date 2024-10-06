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

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/admin_users.sql", "classpath:data/organization_license_keys.sql",
	"classpath:data/organization_licenses.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganizationLicenseKeyAdminControllerTest {

	@Autowired
	public MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

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
	@DisplayName("GET Organization License Key Responses With Search For Organization License Id - 200 OK")
	void getOrganizationLicenseResponsesWithOrganizationLicenseId() throws Exception {
		// given
		String url = "/api/admin/organization-license-keys?organizationLicenseId=1000000005";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(2));
	}

	@Test
	@DisplayName("GET Organization License Key 비활성화 - 200 OK")
	void unuseOrganizationLicenseKey() throws Exception {
		// given
		Long organizationLicenseKeyId = 1000000000L;
		String url = String.format("/api/admin/organization-license-keys/%s/unuse", organizationLicenseKeyId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET Organization License Key 비활성화 - 404 NOT FOUND")
	void unuseOrganizationLicenseKeyWithNonExistKey() throws Exception {
		// given
		Long organizationLicenseKeyId = 1000000099L;
		String url = String.format("/api/admin/organization-license-keys/%s/unuse", organizationLicenseKeyId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_ORGANIZATION_LICENSE_KEY.name()));
	}

	@Test
	@DisplayName("GET Organization License Key 비활성화 - 400 BAD REQUEST")
	void unuseOrganizationLicenseKeyWithNotUse() throws Exception {
		// given
		Long organizationLicenseKeyId = 1000000001L;
		String url = String.format("/api/admin/organization-license-keys/%s/unuse", organizationLicenseKeyId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_STATUS.name()));
	}
}
