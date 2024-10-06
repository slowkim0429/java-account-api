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

import com.virnect.account.adapter.inbound.dto.request.mobilemanagement.MobileManagementUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/mobile_managements.sql",
	"classpath:data/admin_users.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MobileManagementAdminControllerTest {
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
	@DisplayName("GET Mobile management 조회 - 성공 200 OK")
	void getMobileManagement() throws Exception {
		String url = "/api/admin/mobile-managements";

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@Sql(scripts = {
		"classpath:data/mobile_managements_truncate.sql",
		"classpath:data/admin_users.sql"
	},
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD
	)
	@Sql(scripts = {
		"classpath:data/mobile_managements.sql",
		"classpath:data/admin_users.sql"
	},
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD
	)
	@DisplayName("GET Mobile management 조회 - 데이터가 하나도 존재하지 않을 경우 404 NOT_FOUND")
	void getMobileManagementButNotFound() throws Exception {
		String url = "/api/admin/mobile-managements";

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("PUT Mobile management 수정 - 성공 200 OK")
	void update() throws Exception {
		String url = "/api/admin/mobile-managements";

		MobileManagementUpdateRequestDto mobileManagementUpdateRequestDto = new MobileManagementUpdateRequestDto();
		mobileManagementUpdateRequestDto.setIsExposed(false);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mobileManagementUpdateRequestDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Mobile management 수정 - 모든 데이터 수정 성공 200 OK")
	void updateByAllData() throws Exception {
		String url = "/api/admin/mobile-managements";

		MobileManagementUpdateRequestDto mobileManagementUpdateRequestDto = new MobileManagementUpdateRequestDto();
		mobileManagementUpdateRequestDto.setNewPassword("12qwer!@34");
		mobileManagementUpdateRequestDto.setMessage("점검 중입니다");
		mobileManagementUpdateRequestDto.setIsExposed(true);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mobileManagementUpdateRequestDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Mobile management 수정 - 에러 공지를 활성화 시, 빈 메시지 수정 요청 실패 400 BAD_REQUEST")
	void updateForNoticeButBlankMessage() throws Exception {
		String url = "/api/admin/mobile-managements";

		MobileManagementUpdateRequestDto mobileManagementUpdateRequestDto = new MobileManagementUpdateRequestDto();
		mobileManagementUpdateRequestDto.setNewPassword("12qwer!@34");
		mobileManagementUpdateRequestDto.setMessage("");
		mobileManagementUpdateRequestDto.setIsExposed(true);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mobileManagementUpdateRequestDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Mobile management 수정 - 메시지만 수정 성공 200 OK")
	void updateNoticeByOnlyMessage() throws Exception {
		String url = "/api/admin/mobile-managements";

		MobileManagementUpdateRequestDto mobileManagementUpdateRequestDto = new MobileManagementUpdateRequestDto();
		mobileManagementUpdateRequestDto.setMessage("test");
		mobileManagementUpdateRequestDto.setIsExposed(false);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(mobileManagementUpdateRequestDto))
			).andDo(print())
			.andExpect(status().isOk());
	}
}