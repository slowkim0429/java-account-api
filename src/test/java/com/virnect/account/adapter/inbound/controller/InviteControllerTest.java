package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.exception.ErrorCode.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZonedDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import com.virnect.account.adapter.inbound.dto.request.InviteUserAssignRequestDto;
import com.virnect.account.adapter.inbound.dto.request.invite.InviteRequestDto;
import com.virnect.account.adapter.inbound.dto.request.invite.WorkspaceInviteRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.InviteRole;
import com.virnect.account.domain.enumclass.InviteStatus;
import com.virnect.account.domain.enumclass.InviteType;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.SquarsAPIRepository;
import com.virnect.account.port.outbound.WorkspaceAPIRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/users.sql", "classpath:data/invite.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class InviteControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private RedisRepository redisRepository;

	@MockBean
	private WorkspaceAPIRepository workspaceAPIRepository;

	@MockBean
	private SquarsAPIRepository squarsAPIRepository;

	private TokenResponseDto organizationOwnerTokenResponseDto;

	@Value("${security.jwt-config.secret}")
	private String secret;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeAll
	void setUp() {
		organizationOwnerSetup();
	}

	void organizationOwnerSetup() {
		Long userId = 1000000001L;
		Long organizationId = 1000000000L;
		String email = "organiowner@virnect.com";
		organizationOwnerTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, null, email, null, List.of());

		redisRepository.setObjectValue(
			userId.toString(), organizationOwnerTokenResponseDto.getRefreshToken(),
			organizationOwnerTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(organizationOwnerTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("POST invite 생성 - 성공 200 OK")
	void createInvite() throws Exception {
		// given
		InviteRequestDto inviteRequestDto = InviteRequestDto.of(
			List.of("abc@virnect.com"), InviteRole.ROLE_WORKSPACE_USER.name(), 1000000000L, 1000000000L,
			"workspace name", InviteType.WORKSPACE.name(), null
		);

		String url = "/api/invites";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.content(objectMapper.writeValueAsString(inviteRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST invite 생성 - 초대 대상이 중복되는 경우 400 BAD_REQUEST")
	void createInviteByDuplicatedEmailList() throws Exception {
		// given
		InviteRequestDto inviteRequestDto = InviteRequestDto.of(
			List.of("abc@virnect.com", "abc@virnect.com"), InviteRole.ROLE_WORKSPACE_USER.name(), 1000000000L,
			1000000000L, "workspace name", InviteType.WORKSPACE.name(), null
		);

		String url = "/api/invites";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.content(objectMapper.writeValueAsString(inviteRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST invite 생성 - 초대 대상이 없는 경우 400 BAD_REQUEST")
	void createInviteByEmptyEmailList() throws Exception {
		// given
		InviteRequestDto inviteRequestDto = InviteRequestDto.of(
			Collections.emptyList(), InviteRole.ROLE_WORKSPACE_USER.name(), 1000000000L,
			1000000000L, "workspace name", InviteType.WORKSPACE.name(), null
		);

		String url = "/api/invites";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.content(objectMapper.writeValueAsString(inviteRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST assign Invite Group User - 200 OK")
	void assignGroupUser() throws Exception {
		//given
		String inviteToken = createInviteTicket(
			"inviteUser@virnect.com", 1000000001L, 1000000000L, 1000000000L, InviteType.GROUP,
			InviteRole.ROLE_GROUP_USER, 86400000L
		);

		InviteUserAssignRequestDto inviteUserAssignRequestDto = new InviteUserAssignRequestDto();
		inviteUserAssignRequestDto.setInviteToken(inviteToken);

		// when
		String url = "/api/invites/assign";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(inviteUserAssignRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("POST assign Invite Group User - 404 Error - 비회원 유저가 초대 수락한 경우")
	void assignGroupUser_withNotFoundUser() throws Exception {
		//given
		String inviteToken = createInviteTicket(
			"notFoundUser@virnect.com", 1000000001L, 1000000001L, 1000000001L, InviteType.GROUP,
			InviteRole.ROLE_GROUP_USER, 86400000L
		);

		InviteUserAssignRequestDto inviteUserAssignRequestDto = new InviteUserAssignRequestDto();
		inviteUserAssignRequestDto.setInviteToken(inviteToken);

		// when
		String url = "/api/invites/assign";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(inviteUserAssignRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_USER.name()));
	}

	@Test
	@DisplayName("POST assign Invite Group User - 400 Error - 만료된 토큰으로 초대 수락한 경우")
	void assignGroupUser_withExpiredToken() throws Exception {
		//given
		String inviteToken = createInviteTicket(
			"inviteUser@virnect.com", 1000000001L, 1000000000L, 1000000000L, InviteType.GROUP,
			InviteRole.ROLE_GROUP_USER, -1L
		);

		InviteUserAssignRequestDto inviteUserAssignRequestDto = new InviteUserAssignRequestDto();
		inviteUserAssignRequestDto.setInviteToken(inviteToken);

		// when
		String url = "/api/invites/assign";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(inviteUserAssignRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INVITE_TOKEN.name()));
	}

	@Test
	@DisplayName("POST 워크스페이스 유저 초대 승인 - 승인 성공 OK 200")
	void assignWorkspaceUser() throws Exception {
		//given
		String receiverEmail = "inviteUser@virnect.com";
		Long senderId = 1000000001L;
		Long workspaceId = 1000000000L;
		String workspaceName = "workspace 1";
		ProductType productType = ProductType.SQUARS;
		InviteType inviteType = InviteType.WORKSPACE;
		InviteRole role = InviteRole.ROLE_WORKSPACE_USER;
		long inviteTokenExpireTime = 86400000;
		Date now = new Date();
		Date inviteTokenExpiresIn = new Date(now.getTime() + inviteTokenExpireTime);

		String inviteToken = createWorkspaceUserInviteTicket(receiverEmail, senderId,
			workspaceId, workspaceName, productType, inviteType, role, inviteTokenExpiresIn
		);

		InviteUserAssignRequestDto inviteUserAssignRequestDto = new InviteUserAssignRequestDto();
		inviteUserAssignRequestDto.setInviteToken(inviteToken);

		String url = "/api/invites/assign";
		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(inviteUserAssignRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 워크스페이스 유저 초대 승인 - 존재하지 않는 유저일 경우 NOT_FOUND 404")
	void assignWorkspaceUserNonExistentUserId() throws Exception {
		//given
		String receiverEmail = "notexist@virnect.com";
		Long senderId = 1000000001L;
		Long workspaceId = 1000000000L;
		String workspaceName = "workspace 1";
		ProductType productType = ProductType.SQUARS;
		InviteType inviteType = InviteType.WORKSPACE;
		InviteRole role = InviteRole.ROLE_WORKSPACE_USER;
		long inviteTokenExpireTime = 86400000;
		Date now = new Date();
		Date inviteTokenExpiresIn = new Date(now.getTime() + inviteTokenExpireTime);

		String inviteToken = createWorkspaceUserInviteTicket(receiverEmail, senderId,
			workspaceId, workspaceName, productType, inviteType, role, inviteTokenExpiresIn
		);

		InviteUserAssignRequestDto inviteUserAssignRequestDto = new InviteUserAssignRequestDto();
		inviteUserAssignRequestDto.setInviteToken(inviteToken);

		// when
		String url = "/api/invites/assign";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(inviteUserAssignRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_USER.name()));
	}

	@Test
	@DisplayName("POST 워크스페이스 유저 초대 승인 - 초대 유효기간이 경과했을 경우 400")
	void assignWorkspaceUserWithExpiredToken() throws Exception {
		//given
		String receiverEmail = "inviteUser@virnect.com";
		Long senderId = 1000000001L;
		Long workspaceId = 1000000000L;
		String workspaceName = "workspace 1";
		long currentTimeMillis = System.currentTimeMillis();
		Date now = new Date(currentTimeMillis);
		ProductType productType = ProductType.SQUARS;
		InviteType inviteType = InviteType.WORKSPACE;
		InviteRole role = InviteRole.ROLE_WORKSPACE_USER;

		String inviteToken = createWorkspaceUserInviteTicket(receiverEmail, senderId,
			workspaceId, workspaceName, productType, inviteType, role, now
		);

		InviteUserAssignRequestDto inviteUserAssignRequestDto = new InviteUserAssignRequestDto();
		inviteUserAssignRequestDto.setInviteToken(inviteToken);

		String url = "/api/invites/assign";
		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(inviteUserAssignRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INVITE_TOKEN.name()));
	}

	@Test
	@DisplayName("POST 워크스페이스 유저 초대 승인 - 토큰이 공백인 경우 BAD_REQUEST 400")
	void assignWorkspaceUserWithBlankToken() throws Exception {
		//given
		InviteUserAssignRequestDto inviteUserAssignRequestDto = new InviteUserAssignRequestDto();
		inviteUserAssignRequestDto.setInviteToken(" ");

		String url = "/api/invites/assign";
		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(inviteUserAssignRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	private String createInviteTicket(
		String email, Long userId, Long workspaceId, Long groupId,
		InviteType inviteType, InviteRole role, Long expiredTime
	) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS512");

		Date now = new Date();
		Date inviteTokenExpiresIn = new Date(now.getTime() + expiredTime);

		return Jwts.builder()
			.setHeader(headers)
			.setSubject(inviteType.toString())
			.setIssuer("virnect")
			.setIssuedAt(now)
			.claim("send", userId)
			.claim("email", email)
			.claim("role", role)
			.claim("workspaceId", workspaceId)
			.claim("groupId", groupId)
			.setExpiration(inviteTokenExpiresIn)
			.signWith(
				Keys.hmacShaKeyFor(secret.getBytes()),
				SignatureAlgorithm.HS512
			)
			.compact();
	}

	private String createWorkspaceUserInviteTicket(
		String email,
		Long userId,
		Long workspaceId,
		String workspaceName,
		ProductType productType,
		InviteType inviteType,
		InviteRole role,
		Date inviteTokenExpiresIn
	) {
		Map<String, Object> headers = new HashMap<>();
		headers.put("typ", "JWT");
		headers.put("alg", "HS512");

		Date now = new Date();

		return Jwts.builder()
			.setHeader(headers)
			.setSubject(inviteType.toString())
			.setIssuer(workspaceName)
			.setIssuedAt(now)
			.claim("send", userId)
			.claim("email", email)
			.claim("role", role)
			.claim("workspaceId", workspaceId)
			.claim("productType", productType)
			.setExpiration(inviteTokenExpiresIn)
			.signWith(Keys.hmacShaKeyFor(secret.getBytes()), SignatureAlgorithm.HS512)
			.compact();
	}

	@Test
	@DisplayName("GET 워크스페이스 초대 내역 목록 조회 - 조회 성공 OK 200")
	void getWorkspaceInvitedUsers() throws Exception {
		//given
		String workspaceId = "1000000000";
		String url = String.format("/api/invites/workspaces/%s", workspaceId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(6));
	}

	@Test
	@DisplayName("PUT 초대 철회 (WORKSPACE) - 철회 성공 200 OK")
	void cancelInvite() throws Exception {
		//given
		String inviteId = "1000000001";
		String url = String.format("/api/invites/%s/cancel", inviteId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT 초대 철회 (WORKSPACE) - invite가 존재하지 않을 경우 NOT_FOUND 404")
	void cancelInviteByNonExistentId() throws Exception {
		//given
		String inviteId = "99999999999";
		String url = String.format("/api/invites/%s/cancel", inviteId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_INVITATION.name()));
	}

	@Test
	@DisplayName("PUT 초대 철회 (WORKSPACE) - 요청 권한이 없는 경우 FORBIDDEN 403")
	void cancelInviteBy() throws Exception {
		//given
		String inviteId = "1000000007";
		String url = String.format("/api/invites/%s/cancel", inviteId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.customError").value(INVALID_AUTHORIZATION.name()));
	}

	@Test
	@DisplayName("POST workspace invite 생성 - 200 OK")
	void createWorkspaceInvite() throws Exception {
		// given
		WorkspaceInviteRequestDto workspaceInviteRequestDto = new WorkspaceInviteRequestDto();
		workspaceInviteRequestDto.setWorkspaceInviteId(1000000000L);
		workspaceInviteRequestDto.setInviteRole(InviteRole.ROLE_WORKSPACE_USER);
		workspaceInviteRequestDto.setInviteType(InviteType.WORKSPACE);
		workspaceInviteRequestDto.setEmail("test@virnect.com");
		workspaceInviteRequestDto.setOrganizationId(1000000000L);
		workspaceInviteRequestDto.setWorkspaceId(1000000000L);
		workspaceInviteRequestDto.setInviteStatus(InviteStatus.PENDING);
		workspaceInviteRequestDto.setExpiredDate(ZonedDateTime.now().plusMinutes(30));

		String url = "/api/invites/workspaces";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.content(objectMapper.writeValueAsString(workspaceInviteRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}
}
