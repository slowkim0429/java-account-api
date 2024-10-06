package com.virnect.account.adapter.inbound.controller;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.InviteStatus;
import com.virnect.account.domain.enumclass.InviteType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/admin_users.sql", "classpath:data/invite.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InviteAdminControllerTest {
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
	@DisplayName("GET 초대 내역 목록 조회 - 조회 성공 OK 200")
	void getInvites() throws Exception {
		//given

		String url = String.format("/api/admin/invites?inviteType=%s", InviteType.WORKSPACE.name());

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(7));
	}

	@Test
	@DisplayName("GET 초대 내역 목록 조회 - 만료된 초대 조회 성공 OK 200")
	void getInvites_withExpiredInvite() throws Exception {
		//given

		String url = String.format("/api/admin/invites?inviteStatus=%s", InviteStatus.EXPIRED.name());

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET 초대 정보 조회 - 200 OK")
	void getInvite() throws Exception {
		//given
		String inviteId = "1000000000";
		String url = String.format("/api/admin/invites/%s", inviteId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET 초대 정보 조회 - 404 NotFound")
	void getInvite_withNotFoundInvitation() throws Exception {
		//given
		String inviteId = "999999999999";
		String url = String.format("/api/admin/invites/%s", inviteId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_INVITATION.name()));
	}

	@Test
	@DisplayName("GET 초대 정보 히스토리 조회 - 200 OK")
	void getInviteRevisions() throws Exception {
		//given
		String inviteId = "1000000000";
		String url = String.format("/api/admin/invites/%s/revisions", inviteId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isOk());
	}

}
