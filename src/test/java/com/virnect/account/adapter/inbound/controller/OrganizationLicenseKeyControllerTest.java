package com.virnect.account.adapter.inbound.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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
@Sql(scripts =
	{
		"classpath:data/organizations.sql", "classpath:data/users.sql", "classpath:data/organization_license_keys.sql",
		"classpath:data/organization_licenses.sql", "classpath:data/items.sql", "classpath:data/licenses.sql",
		"classpath:data/license_grades.sql", "classpath:data/products.sql",
	}
)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganizationLicenseKeyControllerTest {
	@Autowired
	public MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private RedisRepository redisRepository;

	private TokenResponseDto organizationOwnerTokenResponseDto;

	private TokenResponseDto anotherOrganizationOwnerTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeAll
	void setUp() throws Exception {
		organizationSetup();
		anotherOrganizationSetup();
	}

	void organizationSetup() {
		Long userId = 1000000000L;
		Long organizationId = 1000000001L;
		String nickname = "organization nickname";
		String email = "slowkim@gggg.com";
		String language = "ko_KR";
		List<Role> roles = Arrays.stream("ROLE_USER,ROLE_ORGANIZATION_OWNER".split(","))
			.map(Role::valueOf)
			.collect(Collectors.toList());

		organizationOwnerTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, nickname, email, language, roles);

		redisRepository.setObjectValue(
			userId.toString(), organizationOwnerTokenResponseDto.getRefreshToken(),
			organizationOwnerTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	void anotherOrganizationSetup() {
		Long userId = 1000000001L;
		Long organizationId = 1000000000L;
		String nickname = "organization nickname";
		String email = "organiowner@virnect.com";
		String language = "ko_KR";
		List<Role> roles = Arrays.stream("ROLE_USER,ROLE_ORGANIZATION_OWNER".split(","))
			.map(Role::valueOf)
			.collect(Collectors.toList());

		anotherOrganizationOwnerTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, nickname, email, language, roles);

		redisRepository.setObjectValue(
			userId.toString(), anotherOrganizationOwnerTokenResponseDto.getRefreshToken(),
			anotherOrganizationOwnerTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(organizationOwnerTokenResponseDto.getAccessToken()));

		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(anotherOrganizationOwnerTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("Track SDK License Key 발급 신청 - 신규 발급 신청 성공 OK 200")
	void applyNewOrganizationLicenseKey() throws Exception {
		// given
		String url = "/api/organization-license-keys";

		//when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(
						HttpHeaders.AUTHORIZATION,
						getAuthorizationBearerToken(anotherOrganizationOwnerTokenResponseDto)
					)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Track SDK License Key 발급 신청 - 재발급 신청 성공 OK 200")
	void applyOrganizationLicenseKey() throws Exception {
		// given
		String url = "/api/organization-license-keys";

		//when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(
						HttpHeaders.AUTHORIZATION,
						getAuthorizationBearerToken(organizationOwnerTokenResponseDto)
					)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@Sql(scripts = {
		"classpath:data/organizations.sql", "classpath:data/users.sql", "classpath:data/organization_license_keys.sql",
		"classpath:data/organization_licenses.sql", "classpath:data/items_truncate.sql", "classpath:data/licenses.sql",
		"classpath:data/license_grades.sql", "classpath:data/products.sql",
	}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@DisplayName("Track SDK License Key 발급 신청 - Track SDK 무료 아이템이 존재하지 않을 경우 NOT_FOUND 404")
	void applyOrganizationLicenseKeyButNonExistentTrackFreeItem() throws Exception {
		// given
		String url = "/api/organization-license-keys";

		//when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(
						HttpHeaders.AUTHORIZATION,
						getAuthorizationBearerToken(anotherOrganizationOwnerTokenResponseDto)
					)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_ITEM.name()));
	}
}
