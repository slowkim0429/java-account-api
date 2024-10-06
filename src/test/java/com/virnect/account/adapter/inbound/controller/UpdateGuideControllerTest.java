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

import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/users.sql",
	"classpath:data/update_guides.sql",
	"classpath:data/admin_users.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateGuideControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private RedisRepository redisRepository;

	private TokenResponseDto userTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeAll
	void setUp() throws Exception {
		userSetup();
	}

	void userSetup() {
		Long userId = 1000000005L;
		Long organizationId = 0L;
		String nickname = "user1";
		String email = "user1@guest.com";
		String language = "ko_KR";
		List<Role> roles = Arrays.stream("ROLE_USER".split(","))
			.map(Role::valueOf)
			.collect(Collectors.toList());

		userTokenResponseDto = tokenProvider.createToken(userId, organizationId, nickname, email, language, roles);

		redisRepository.setObjectValue(
			userId.toString(), userTokenResponseDto.getRefreshToken(),
			userTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteObjectValue(tokenProvider.getUserNameFromJwtToken(userTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("GET update guide 최신 조회 - 조회 성공 200 OK")
	void getUpdateGuideButServiceTypeIsNull() throws Exception {
		// given
		String url = "/api/update-guides/latest";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET update guide 최신 조회 - workspace 서비스 조회 성공 200 OK")
	void getUpdateGuideButServiceTypeIsWorkspace() throws Exception {
		// given
		String url = "/api/update-guides/latest?serviceType=WORKSPACE";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("name2"));
	}

	@Test
	@DisplayName("GET update guide 최신 조회 - squars 서비스 조회 성공 200 OK")
	void getUpdateGuideButServiceTypeIsSquars() throws Exception {
		// given
		String url = "/api/update-guides/latest?serviceType=SQUARS";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("name3"));
	}

	@Sql(scripts = {
		"classpath:data/update_guides_truncate.sql",
		"classpath:data/users.sql"
	}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Test
	@DisplayName("GET update guide WORKSPACE 최신 조회(최신이 존재하지 않음) - 성공 200 OK")
	void getUpdateGuideForWorkspaceIsNotFoundButDoNotThrow() throws Exception {
		// given
		String url = "/api/update-guides/latest?serviceType=WORKSPACE";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Sql(scripts = {
		"classpath:data/update_guides_truncate.sql",
		"classpath:data/users.sql"
	}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Test
	@DisplayName("GET update guide SQUARS 최신 조회(최신이 존재하지 않음) - 성공 200 OK")
	void getUpdateGuideForSquarsIsNotFoundButDoNotThrow() throws Exception {
		// given
		String url = "/api/update-guides/latest?serviceType=SQUARS";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Sql(scripts = {
		"classpath:data/update_guides_truncate.sql",
		"classpath:data/users.sql"
	}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Test
	@DisplayName("GET update guide ALL 최신 조회(최신이 존재하지 않음) - 성공 200 OK")
	void getUpdateGuideForAllIsNotFoundButDoNotThrow() throws Exception {
		// given
		String url = "/api/update-guides/latest";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}
}
