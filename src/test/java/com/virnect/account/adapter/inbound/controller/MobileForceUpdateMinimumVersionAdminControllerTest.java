package com.virnect.account.adapter.inbound.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.apache.http.HttpHeaders;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.appversion.MobileForceUpdateMinimumVersionUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/mobile_force_update_minimum_versions.sql",
	"classpath:data/admin_users.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MobileForceUpdateMinimumVersionAdminControllerTest {
	@Autowired
	private MockMvc mockMvc;

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
		userSetup();
	}

	void userSetup() {
		Long adminUserId = 1000000001L;
		String email = "admin-user2@virnect.com";
		adminTokenResponseDto = tokenProvider.createAdminToken(
			adminUserId, email, null, List.of(Role.ROLE_ADMIN_MASTER, Role.ROLE_ADMIN_USER));

		redisRepository.setAdminRefreshToken(
			adminUserId.toString(), adminTokenResponseDto.getRefreshToken(),
			adminTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteAdminRefreshToken(
			tokenProvider.getUserNameFromJwtToken(adminTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("GET Mobile force update minimum version admin 상세 조회 - 성공 200 OK")
	void getMobileManagementForAdmin() throws Exception {
		String url = "/api/admin/mobile-managements/force-update-minimum-versions";

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@Sql(scripts = {"classpath:data/mobile_force_update_minimum_versions_truncate.sql",
		"classpath:data/admin_users.sql"},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = {"classpath:data/mobile_force_update_minimum_versions_truncate.sql",
		"classpath:data/admin_users.sql"},
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("GET Mobile force update minimum version admin 상세 조회 - 실패 404 NOT_FOUND")
	void getMobileManagementForAdminButNotFound() throws Exception {
		String url = "/api/admin/mobile-managements/force-update-minimum-versions";

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_MOBILE_FORCE_UPDATE_MINIMUM_VERSION.name()));
	}

	@Test
	@DisplayName("PUT Mobile force update minimum version admin 수정 - 성공 200 OK")
	void update() throws Exception {
		String url = "/api/admin/mobile-managements/force-update-minimum-versions";
		MobileForceUpdateMinimumVersionUpdateRequestDto mobileForceUpdateMinimumVersionUpdateRequestDto = new MobileForceUpdateMinimumVersionUpdateRequestDto();
		mobileForceUpdateMinimumVersionUpdateRequestDto.setVersion("1.0.0");
		mobileForceUpdateMinimumVersionUpdateRequestDto.setBundleId("com.virnect.app");
		mobileForceUpdateMinimumVersionUpdateRequestDto.setForceUpdateType("VERSION_CHECK");

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mobileForceUpdateMinimumVersionUpdateRequestDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Mobile force update minimum version admin 수정 - 버전의 형식이 맞지 않아 실패 400 BAD_REQUEST")
	void updateButBadRequestBecauseVersion() throws Exception {
		String url = "/api/admin/mobile-managements/force-update-minimum-versions";
		MobileForceUpdateMinimumVersionUpdateRequestDto mobileForceUpdateMinimumVersionUpdateRequestDto = new MobileForceUpdateMinimumVersionUpdateRequestDto();
		mobileForceUpdateMinimumVersionUpdateRequestDto.setVersion("1.0#0");
		mobileForceUpdateMinimumVersionUpdateRequestDto.setBundleId("com.virnect.app");
		mobileForceUpdateMinimumVersionUpdateRequestDto.setForceUpdateType("VERSION_CHECK");

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mobileForceUpdateMinimumVersionUpdateRequestDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Mobile force update minimum version admin 수정 - 번들ID의 형식이 맞지 않아 실패 400 BAD_REQUEST")
	void updateButBadRequestBecauseBundleId() throws Exception {
		String url = "/api/admin/mobile-managements/force-update-minimum-versions";
		MobileForceUpdateMinimumVersionUpdateRequestDto mobileForceUpdateMinimumVersionUpdateRequestDto = new MobileForceUpdateMinimumVersionUpdateRequestDto();
		mobileForceUpdateMinimumVersionUpdateRequestDto.setVersion("1.0.0");
		mobileForceUpdateMinimumVersionUpdateRequestDto.setBundleId("comvirnectapp");
		mobileForceUpdateMinimumVersionUpdateRequestDto.setForceUpdateType("VERSION_CHECK");

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mobileForceUpdateMinimumVersionUpdateRequestDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@Sql(scripts = {"classpath:data/mobile_force_update_minimum_versions_truncate.sql",
		"classpath:data/admin_users.sql"},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = {"classpath:data/mobile_force_update_minimum_versions_truncate.sql",
		"classpath:data/admin_users.sql"},
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("PUT Mobile force update minimum version admin 수정 - 실패 404 NOT_FOUND")
	void updateButNotFound() throws Exception {
		String url = "/api/admin/mobile-managements/force-update-minimum-versions";
		MobileForceUpdateMinimumVersionUpdateRequestDto mobileForceUpdateMinimumVersionUpdateRequestDto = new MobileForceUpdateMinimumVersionUpdateRequestDto();
		mobileForceUpdateMinimumVersionUpdateRequestDto.setVersion("1.0.0");
		mobileForceUpdateMinimumVersionUpdateRequestDto.setBundleId("com.virnect.app");
		mobileForceUpdateMinimumVersionUpdateRequestDto.setForceUpdateType("VERSION_CHECK");

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mobileForceUpdateMinimumVersionUpdateRequestDto))
			).andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_MOBILE_FORCE_UPDATE_MINIMUM_VERSION.name()));
	}

	@Test
	@DisplayName("GET Mobile force update minimum version admin 기록 조회 - 성공 200 OK")
	void getRevision() throws Exception {
		String url = "/api/admin/mobile-managements/force-update-minimum-versions/revisions";

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk());
	}
}