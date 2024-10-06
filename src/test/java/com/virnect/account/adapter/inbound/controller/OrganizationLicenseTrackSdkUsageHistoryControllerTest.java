package com.virnect.account.adapter.inbound.controller;

import static org.junit.jupiter.api.Assertions.*;
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

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationLicenseTrackSdkUsageRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.CustomException;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts =
	{
		"classpath:data/organizations.sql", "classpath:data/users.sql", "classpath:data/organization_license_keys.sql",
		"classpath:data/organization_licenses.sql", "classpath:data/items.sql", "classpath:data/licenses.sql",
		"classpath:data/license_grades.sql", "classpath:data/products.sql"
	}
)
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganizationLicenseTrackSdkUsageHistoryControllerTest {
	@Autowired
	public MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private RedisRepository redisRepository;

	private TokenResponseDto organizationOwnerTokenResponseDto;

	private String trackValidTokenResponseDto;

	private String trackInvalidTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	private String getAuthorizationBearerToken(String trackTokenResponseDto) {
		return "Bearer " + trackTokenResponseDto;
	}

	@BeforeAll
	void setUp() throws Exception {
		organizationSetup();
		trackValidTokenSetup();
		trackInvalidTokenSetup();
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

	void trackValidTokenSetup() {
		Long customUserDetailsId = 1000000000L;
		Long organizationLicenseKeyId = 1000000000L;
		trackValidTokenResponseDto = tokenProvider.createTrackToken(customUserDetailsId, organizationLicenseKeyId);
	}

	void trackInvalidTokenSetup() {
		Long customUserDetailsId = 1000000000L;
		Long organizationLicenseKeyId = 1000000001L;
		trackInvalidTokenResponseDto = tokenProvider.createTrackToken(customUserDetailsId, organizationLicenseKeyId);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(organizationOwnerTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("track sdk organization license 활동 기록 - 성공 OK 200")
	void createTrackSdkUsageHistory() throws Exception {
		// given
		String url = "/api/organization-licenses/track-sdk-usage-histories";

		OrganizationLicenseTrackSdkUsageRequestDto organizationLicenseTrackSdkUsageRequestDto = new OrganizationLicenseTrackSdkUsageRequestDto();
		organizationLicenseTrackSdkUsageRequestDto.setContent("track dll 사용");

		//when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(
						HttpHeaders.AUTHORIZATION,
						getAuthorizationBearerToken(trackValidTokenResponseDto)
					)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationLicenseTrackSdkUsageRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("track sdk organization license 활동 기록 - track token 없을경우 403 isForbidden")
	void createTrackSdkUsageHistoryWithoutTrackToken() throws Exception {
		// given
		String url = "/api/organization-licenses/track-sdk-usage-histories";

		OrganizationLicenseTrackSdkUsageRequestDto organizationLicenseTrackSdkUsageRequestDto = new OrganizationLicenseTrackSdkUsageRequestDto();
		organizationLicenseTrackSdkUsageRequestDto.setContent("track dll 사용");

		//when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(
						HttpHeaders.AUTHORIZATION,
						getAuthorizationBearerToken(organizationOwnerTokenResponseDto)
					)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationLicenseTrackSdkUsageRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("track sdk organization license 활동 기록 - 토큰안에 key값이 use상태가 아닐경우 401 UNAUTHORIZED")
	void createTrackSdkUsageHistoryByUnuseLicenseKey() {
		// given
		String url = "/api/organization-licenses/track-sdk-usage-histories";

		OrganizationLicenseTrackSdkUsageRequestDto organizationLicenseTrackSdkUsageRequestDto = new OrganizationLicenseTrackSdkUsageRequestDto();
		organizationLicenseTrackSdkUsageRequestDto.setContent("track dll 사용");

		//when
		assertThrows(CustomException.class, () -> {
			mockMvc.perform(
					MockMvcRequestBuilders
						.post(url)
						.header(
							HttpHeaders.AUTHORIZATION,
							getAuthorizationBearerToken(trackInvalidTokenResponseDto)
						)
						.contentType(MediaType.APPLICATION_JSON)
						.content(objectMapper.writeValueAsString(organizationLicenseTrackSdkUsageRequestDto))
				)
				.andDo(print())
				.andExpect(status().isUnauthorized());
		});
	}
}
