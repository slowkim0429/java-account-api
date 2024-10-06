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

import com.virnect.account.adapter.inbound.dto.request.UseStatusUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupModifyRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/admin_users.sql",
	"classpath:data/authority_groups.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthorityGroupAdminControllerTest {

	private static final String BASE_URL = "/api/admin/authority-groups";

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
	@DisplayName("GET Authority Group 목록 조회 - 조회 성공 200 OK")
	void getAuthorityGroups() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(3));
	}

	@Test
	@DisplayName("GET Authority Group 목록 조회 - 쿼리 스트링(status)으로 조회 성공 200 OK")
	void getAuthorityGroupsByStatus() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL)
					.queryParam("status", "USE")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET Authority group 조회 - 조회 성공 200 OK")
	void getAuthorityGroupResponse() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL + "/10000000000")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET Authority group 조회 - 조회 실패 404 NOT_FOUND")
	void getAuthorityGroupResponseButNotFound() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL + "/99999999999")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_AUTHORITY_GROUP.name()));
	}

	@Test
	@DisplayName("POST Authority Group 생성 - 생성 성공 200 OK")
	void create() throws Exception {
		String name = "Admin master";
		AuthorityGroupCreateRequestDto authorityGroupCreateRequestDto = AuthorityGroupCreateRequestDto.of(name, null);

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authorityGroupCreateRequestDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Authority Group 수정 - 수정 성공 200 OK")
	void modify() throws Exception {
		String name = "Admin master";
		String description = "description";
		AuthorityGroupModifyRequestDto authorityGroupModifyRequestDto = AuthorityGroupModifyRequestDto.of(
			name, description);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_URL + "/10000000000")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authorityGroupModifyRequestDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Authority Group 수정 - 해당 데이터가 존재하지 않는 경우 404 NOT_FOUND")
	void modifyByNonExistentId() throws Exception {
		String name = "Admin master";
		AuthorityGroupModifyRequestDto authorityGroupModifyRequestDto = AuthorityGroupModifyRequestDto.of(name, null);

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL + "/99999999999")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authorityGroupModifyRequestDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_AUTHORITY_GROUP.name()));
	}

	@Test
	@DisplayName("PUT Authority Group 수정 - 필수값이 누락된 경우 400 BAD_REQUEST")
	void modifyWithoutRequiredData() throws Exception {
		String description = "description";
		AuthorityGroupModifyRequestDto authorityGroupModifyRequestDto = AuthorityGroupModifyRequestDto.of(
			null, description);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_URL + "/10000000000")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(authorityGroupModifyRequestDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("GET Authority group 변경 이력 조회 - 성공 200 OK")
	void getAuthorityGroupRevisions() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL + "/10000000000/revisions")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Authority Group 상태 수정 - 수정 성공 200 OK")
	void updateStatus() throws Exception {
		UseStatusUpdateRequestDto useStatusUpdateRequestDto = UseStatusUpdateRequestDto.from(UseStatus.USE.name());

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_URL + "/10000000000/status")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(useStatusUpdateRequestDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Authority Group 상태 수정 - 해당 데이터가 존재하지 않을 경우 404 NOT_FOUND")
	void updateStatusByNonExistentId() throws Exception {
		UseStatusUpdateRequestDto useStatusUpdateRequestDto = UseStatusUpdateRequestDto.from(UseStatus.USE.name());

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_URL + "/99999999999/status")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(useStatusUpdateRequestDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_AUTHORITY_GROUP.name()));
	}

	@Test
	@DisplayName("PUT Authority Group 상태 수정 - 해당 권한 그룹에 할당된 활성화 상태의 어드민 유저가 존재하지만 UNUSE 상태로 변경하려는 경우 400 BAD_REQUEST")
	void updateStatusByUsingAuthorityGroupId() throws Exception {
		UseStatusUpdateRequestDto useStatusUpdateRequestDto = UseStatusUpdateRequestDto.from(UseStatus.UNUSE.name());

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_URL + "/10000000002/status")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(useStatusUpdateRequestDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_UPDATE_USING_AUTHORITY_GROUP.name()));
	}
}
