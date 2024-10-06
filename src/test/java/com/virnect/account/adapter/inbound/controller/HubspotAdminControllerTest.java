package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.exception.ErrorCode.*;
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

import com.virnect.account.adapter.inbound.dto.request.hubspot.ExternalServiceMappingSyncableRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/admin_users.sql",
	"classpath:data/external_service_mappings.sql",
	"classpath:data/items.sql",
	"classpath:data/organizations.sql",
	"classpath:data/users.sql",
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HubspotAdminControllerTest {
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
	@DisplayName("GET Hubspot Service Mappings - 200 OK")
	void getHubspotServiceMappings() throws Exception {
		// given
		String url = "/api/admin/hubspot/external-service-mappings";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET Hubspot Service Mappings With SearchDto- 200 OK")
	void getHubspotServiceMappingsWithSearchDto() throws Exception {
		// given

		String url = "/api/admin/hubspot/external-service-mappings?isLatestMappingSucceeded=" + false;

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(0));
	}

	@Test
	@DisplayName("POST Hubspot Service Mappings Synchronization (User Domain)- 200 OK")
	void synchronizeHubspotMappingWithUser() throws Exception {
		// given

		long userId = 1000000000L;
		String url = "/api/admin/hubspot/external-service-mappings/users/" + userId + "/synchronize";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST Hubspot Service Mappings Synchronization (Organization Domain) - 200 OK")
	void synchronizeHubspotMappingWithOrganization() throws Exception {
		// given

		long organizationId = 1000000000L;
		String url = "/api/admin/hubspot/external-service-mappings/organizations/" + organizationId + "/synchronize";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST Hubspot Service Mappings Synchronization (Item Domain) - 200 OK")
	void synchronizeHubspotMappingWithItem() throws Exception {
		// given

		long itemId = 1000000000L;
		String url = "/api/admin/hubspot/external-service-mappings/items/" + itemId + "/synchronize";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST Hubspot Service Mappings Synchronization - 404 NotFound")
	void synchronizeHubspotMappingWithNotfound() throws Exception {
		// given

		long userId = 1000000999L;
		String url = "/api/admin/hubspot/external-service-mappings/users/" + userId + "/synchronize";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_USER.name()));
	}

	@Test
	@DisplayName("GET External Service Mapping 변경 이력 조회 - 조회 성공 OK 200")
	void getRevisions() throws Exception {
		// given
		Long externalServiceMappingId = 1000000000L;
		String url = String.format(
			"/api/admin/hubspot/external-service-mappings/%s/revisions", externalServiceMappingId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(
						org.springframework.http.HttpHeaders.AUTHORIZATION,
						getAuthorizationBearerToken(adminTokenResponseDto)
					)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT External Service Mapping syncable 변경 - 성공 OK 200")
	void updateSyncable() throws Exception {
		// given
		Long externalServiceMappingId = 1000000000L;
		String url = String.format(
			"/api/admin/hubspot/external-service-mappings/%s/syncable", externalServiceMappingId);

		ExternalServiceMappingSyncableRequestDto requestDto = ExternalServiceMappingSyncableRequestDto.from(false);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(
						HttpHeaders.AUTHORIZATION,
						getAuthorizationBearerToken(adminTokenResponseDto)
					)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT External Service Mapping syncable 변경 - 404 NotFound")
	void updateSyncableWithNotfound() throws Exception {
		// given
		Long externalServiceMappingId = 1000000099L;
		String url = String.format(
			"/api/admin/hubspot/external-service-mappings/%s/syncable", externalServiceMappingId);

		ExternalServiceMappingSyncableRequestDto requestDto = ExternalServiceMappingSyncableRequestDto.from(false);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(
						HttpHeaders.AUTHORIZATION,
						getAuthorizationBearerToken(adminTokenResponseDto)
					)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_EXTERNAL_SERVICE_MAPPING.name()));
	}

	@Test
	@DisplayName("PUT External Service Mapping syncable 변경 - 400 Invalid input")
	void updateSyncableWithInvalidInput() throws Exception {
		// given
		Long externalServiceMappingId = 1000000000L;
		String url = String.format(
			"/api/admin/hubspot/external-service-mappings/%s/syncable", externalServiceMappingId);

		ExternalServiceMappingSyncableRequestDto requestDto = ExternalServiceMappingSyncableRequestDto.from(true);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(
						HttpHeaders.AUTHORIZATION,
						getAuthorizationBearerToken(adminTokenResponseDto)
					)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_SYNCABLE.name()));
	}
}
