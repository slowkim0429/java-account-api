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

import com.virnect.account.adapter.inbound.dto.request.mobilemanagement.MobileManagementPasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/users.sql",
	"classpath:data/mobile_managements.sql",
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MobileManagementControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private RedisRepository redisRepository;

	private TokenResponseDto userTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeAll
	void setUp() {
		userSetup();
	}

	void userSetup() {
		Long userId = 1000000001L;
		Long organizationId = 1000000000L;
		String email = "organiowner@virnect.com";
		userTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, null, email, null, List.of(Role.ROLE_ORGANIZATION_OWNER));

		redisRepository.setObjectValue(
			userId.toString(), userTokenResponseDto.getRefreshToken(),
			userTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(userTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("POST Mobile management 비밀번호 검증 - 성공 200 OK")
	void passwordVerification() throws Exception {
		// given
		String url = "/api/mobile-managements/password/verification";
		MobileManagementPasswordVerificationRequestDto mobileManagementPasswordVerificationDto =
			new MobileManagementPasswordVerificationRequestDto("12345678");

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto))
					.content(objectMapper.writeValueAsString(mobileManagementPasswordVerificationDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST Mobile management 비밀번호 검증 - 비밀번호가 틀린 경우 실패 400 BAD_REQUEST")
	void passwordVerificationButDoesNotMatched() throws Exception {
		// given
		String url = "/api/mobile-managements/password/verification";
		MobileManagementPasswordVerificationRequestDto mobileManagementPasswordVerificationDto =
			new MobileManagementPasswordVerificationRequestDto("11111111");

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto))
					.content(objectMapper.writeValueAsString(mobileManagementPasswordVerificationDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(
				jsonPath("$.customError").value(ErrorCode.INVALID_MOBILE_MANAGEMENT_NOT_MATCHED_PASSWORD.name()));
	}

	@Test
	@Sql(scripts = {
		"classpath:data/mobile_managements_is_exposed.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = {"classpath:data/mobile_managements.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("GET Mobile management 공지 조회 - 노출된 공지가 존재하는 경우 성공 200 OK")
	void getLatestNotice() throws Exception {
		// given
		String url = "/api/mobile-managements/notices/expose";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.message").isNotEmpty());
	}

	@Test
	@DisplayName("GET Mobile management 공지 조회 - 노출된 공지가 존재하지 않는 경우 성공 200 OK")
	void getLatestNoticeButNotExist() throws Exception {
		// given
		String url = "/api/mobile-managements/notices/expose";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}
}