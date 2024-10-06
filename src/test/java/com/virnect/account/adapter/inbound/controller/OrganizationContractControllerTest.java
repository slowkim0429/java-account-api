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
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {"classpath:data/organization_contracts.sql", "classpath:data/users.sql"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganizationContractControllerTest {
	@Autowired
	public MockMvc mockMvc;

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
		organizationOwnerSetup();
		anotherOrganizationSetup();
	}

	void organizationOwnerSetup() {
		Long userId = 1000000001L;
		Long organizationId = 1000000000L;
		String nickname = "organization nickname";
		String email = "organiowner@virnect.com";
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
		Long userId = 1000000000L;
		Long organizationId = 1000000001L;
		String nickname = "organization nickname";
		String email = "slowkim@gggg.com";
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
	@DisplayName("GET Organization Contract 상세 조회 - 조회 성공 200 OK")
	void getOrganizationContract() throws Exception {
		// when
		String url = "/api/organization-contracts/1000000000";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET Organization Contract 상세 조회 - 존재하지 않는 contract일 경우 404 NOT_FOUND")
	void getOrganizationContractByNonExistentContractId() throws Exception {
		// when
		String url = "/api/organization-contracts/99999999999";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_ORGANIZATION_CONTRACT.name()));
	}

	@Test
	@DisplayName("GET Organization Contract 상세 조회 - 다른 사용자의 contract를 조회하는 경우 403 FORBIDDEN")
	void getOrganizationContractByUnauthorizedOrganization() throws Exception {
		// when
		String url = "/api/organization-contracts/1000000000";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(
						HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(anotherOrganizationOwnerTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_AUTHORIZATION.name()));
	}
}
