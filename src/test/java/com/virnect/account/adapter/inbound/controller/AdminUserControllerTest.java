package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.exception.ErrorCode.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.adminuser.AdminUserAuthorityGroupRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.FileService;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.WorkspaceAPIRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/service_region_locale_mappings.sql",
	"classpath:data/admin_users.sql",
	"classpath:data/domains.sql",
	"classpath:data/authority_groups.sql",
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AdminUserControllerTest {
	@Autowired
	public MockMvc mockMvc;

	@MockBean
	FileService fileService;
	@MockBean
	WorkspaceAPIRepository workspaceAPIRepository;
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private RedisRepository redisRepository;
	private TokenResponseDto adminTokenResponseDto;
	private TokenResponseDto adminMasterTokenResponseDto;
	private TokenResponseDto userTokenResponseDto;
	@Autowired
	private ObjectMapper objectMapper;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeEach
	void setUp() throws Exception {
		adminSetUp();
		userSetUp();
		adminMasterSetUp();
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

	void adminMasterSetUp() {
		Long adminUserId = 1000000003L;
		String email = "admin-master@virnect.com";
		adminMasterTokenResponseDto = tokenProvider.createAdminToken(
			adminUserId, email, null, List.of(Role.ROLE_ADMIN_MASTER)
		);

		redisRepository.setAdminRefreshToken(
			adminUserId.toString(), adminMasterTokenResponseDto.getRefreshToken(),
			tokenProvider.getRefreshTokenExpireTime()
		);
	}

	void userSetUp() {
		Long userId = 1000000005L;
		Long organizationId = 0L;
		String email = "user1@guest.com";
		userTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, "user1", email, "ko_KR", List.of(Role.ROLE_USER));
		redisRepository.setObjectValue(
			userId.toString(), userTokenResponseDto.getRefreshToken(),
			userTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	@Test
	@DisplayName("GET getAdminUsers - 200 OK")
	void getAdminUsers() throws Exception {
		// when
		String url = "/api/admin/admin-users?approvalStatus=" + ApprovalStatus.REGISTER;
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(2));
	}

	@Test
	@DisplayName("PUT updateAdminApprovalStatus - 200 OK")
	void updateAdminApprovalStatus() throws Exception {
		// when
		String adminUserId = "1000000000";
		String url = String.format(
			"/api/admin/admin-users/%s/approval-status/%s", adminUserId, ApprovalStatus.APPROVED);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT 어드민 유저 승인 상태 변경 - 탈퇴한 유저일 경우 404 NOT_FOUND")
	void updateApprovalStatusOfResignedAdminUser() throws Exception {
		// when
		String adminUserId = "1000000002";
		String url = String.format(
			"/api/admin/admin-users/%s/approval-status/%s", adminUserId, ApprovalStatus.APPROVED);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ADMIN_USER.name()));
	}

	@Test
	@DisplayName("GET admin user me - 200 성공 OK")
	void getMy() throws Exception {
		// when
		String url = "/api/admin/users/me";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("admin-user2@virnect.com"));
	}

	@Test
	@DisplayName("GET Admin User By Id - 200 OK")
	void getAdminUser() throws Exception {
		//given
		String userId = "1000000000";

		// when
		String url = String.format("/api/admin/admin-users/%s", userId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("admin-user1@virnect.com"));
	}

	@Test
	@DisplayName("GET Admin User By Id - 404 NotFound")
	void getAdminUser_withNotfound() throws Exception {
		//given
		String userId = "1000009999";

		// when
		String url = String.format("/api/admin/admin-users/%s", userId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ADMIN_USER.name()));

	}

	@Test
	@DisplayName("PUT 로그인 어드민유저 password 재설정 - ok 200")
	void updatePassword() throws Exception {
		//given
		UserUpdatePasswordRequestDto updatePasswordRequestDto = new UserUpdatePasswordRequestDto();
		updatePasswordRequestDto.setPassword("qwer123!");
		updatePasswordRequestDto.setNewPassword("qwer123@");
		updatePasswordRequestDto.setCheckPassword("qwer123@");

		// when
		String url = "/api/admin/admin-users/password";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT 로그인 어드민유저 password 재설정 - 현재 비밀번호가 틀린경우 400")
	void updatePasswordWithInvalidInput() throws Exception {
		//given
		UserUpdatePasswordRequestDto updatePasswordRequestDto = new UserUpdatePasswordRequestDto();
		updatePasswordRequestDto.setPassword("qwer123#");
		updatePasswordRequestDto.setNewPassword("qwer123@");
		updatePasswordRequestDto.setCheckPassword("qwer123@");

		// when
		String url = "/api/admin/admin-users/password";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT 로그인 어드민유저 password 재설정 - 새 비밀번호와 체크비밀번호가 맞지않은경우 400")
	void updatePasswordWithInvalidPasswordMatching() throws Exception {
		//given
		UserUpdatePasswordRequestDto updatePasswordRequestDto = new UserUpdatePasswordRequestDto();
		updatePasswordRequestDto.setPassword("qwer123!");
		updatePasswordRequestDto.setNewPassword("qwer123@");
		updatePasswordRequestDto.setCheckPassword("qwer123#");

		// when
		String url = "/api/admin/admin-users/password";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_PASSWORD_MATCHING.name()));
	}

	@Test
	@DisplayName("PUT 로그인 어드민유저 password 재설정 - 바꾸려는 비밀번호가 현재비밀번호인 경우 409")
	void updatePasswordDuplicatePassword() throws Exception {
		//given
		UserUpdatePasswordRequestDto updatePasswordRequestDto = new UserUpdatePasswordRequestDto();
		updatePasswordRequestDto.setPassword("qwer123!");
		updatePasswordRequestDto.setNewPassword("qwer123!");
		updatePasswordRequestDto.setCheckPassword("qwer123!");

		// when
		String url = "/api/admin/admin-users/password";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_PASSWORD.name()));
	}

	@Test
	@DisplayName("POST resign")
	void resign() throws Exception {
		// given
		PasswordVerificationRequestDto passwordVerificationRequestDto = new PasswordVerificationRequestDto();
		passwordVerificationRequestDto.setPassword("qwer123!");

		// when
		String url = "/api/admin/admin-users/resign";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(passwordVerificationRequestDto)))
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST resign - 비밀번호가 틀린경우 400")
	void resignBadRequest() throws Exception {
		// given
		PasswordVerificationRequestDto passwordVerificationRequestDto = new PasswordVerificationRequestDto();
		passwordVerificationRequestDto.setPassword("qwer123@");

		// when
		String url = "/api/admin/admin-users/resign";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(passwordVerificationRequestDto)))
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST withdrawAdminUser")
	void withdrawAdminUser() throws Exception {
		// given
		String adminUserId = "1000000000";

		// when
		String url = String.format("/api/admin/admin-users/%s/withdrawal", adminUserId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminMasterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST withdrawAdminUser - 401 BadRequest - 마스터 관리자를 내보내려는 경우")
	void withdrawAdminUser_withInvalidInput() throws Exception {
		// given
		String adminUserId = "1000000003";

		// when
		String url = String.format("/api/admin/admin-users/%s/withdrawal", adminUserId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminMasterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST withdrawAdminUser - 403 Forbidden - 요청한 사람이 마스터 관리자가 아닌 경우")
	void withdrawAdminUser_withInvalidRequestRole() throws Exception {
		// given
		String adminUserId = "1000000000";

		// when
		String url = String.format("/api/admin/admin-users/%s/withdrawal", adminUserId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.customError").value(INVALID_AUTHORIZATION.name()));
	}

	@Test
	@DisplayName("POST withdrawAdminUser - 404 NotFound - 삭제하려는 관리자가 없는 경우")
	void withdrawAdminUser_withNotfound() throws Exception {
		// given
		String adminUserId = "1000009999";

		// when
		String url = String.format("/api/admin/admin-users/%s/withdrawal", adminUserId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminMasterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ADMIN_USER.name()));
	}

	@Test
	@DisplayName("GET Admin User 변경 이력 조회 - 조회 성공 OK 200")
	void getCouponRevisions() throws Exception {
		// given
		String url = "/api/admin/admin-users/1000000000/revisions";

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
	@DisplayName("POST Admin user 의 authority group 업데이트 - 성공 200 OK")
	void updateAuthorityGroupForAdminUser() throws Exception {

		AdminUserAuthorityGroupRequestDto adminUserAuthorityGroupRequestDto = new AdminUserAuthorityGroupRequestDto();
		adminUserAuthorityGroupRequestDto.setAuthorityGroupId(10000000002L);
		// when
		String url = "/api/admin/admin-users/1000000001/authority-group";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminMasterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminUserAuthorityGroupRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST Admin user 의 authority group 업데이트 - 존재하지 않음 실패 404 NOT_FOUND")
	void updateAuthorityGroupForAdminUserButNotFount() throws Exception {

		AdminUserAuthorityGroupRequestDto adminUserAuthorityGroupRequestDto = new AdminUserAuthorityGroupRequestDto();
		adminUserAuthorityGroupRequestDto.setAuthorityGroupId(10999999999L);
		// when
		String url = "/api/admin/admin-users/1000000001/authority-group";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminMasterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminUserAuthorityGroupRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_AUTHORITY_GROUP.name()));
	}

	@Test
	@DisplayName("POST Admin user 의 authority group 업데이트 - USE 상태가 아님 실패 400 BAD_REQUEST")
	void updateAuthorityGroupForAdminUserButNotUsed() throws Exception {

		AdminUserAuthorityGroupRequestDto adminUserAuthorityGroupRequestDto = new AdminUserAuthorityGroupRequestDto();
		adminUserAuthorityGroupRequestDto.setAuthorityGroupId(10000000001L);
		// when
		String url = "/api/admin/admin-users/1000000001/authority-group";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminMasterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminUserAuthorityGroupRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_AUTHORITY_GROUP_NOT_USED.name()));
	}

	@Test
	@DisplayName("POST Admin user 의 authority group 업데이트 - 계정의 상태가 사용가능 상태가 아님 실패 400 BAD_REQUEST")
	void updateAuthorityGroupForAdminUserButUserIsNotAvailableStatus() throws Exception {

		AdminUserAuthorityGroupRequestDto adminUserAuthorityGroupRequestDto = new AdminUserAuthorityGroupRequestDto();
		adminUserAuthorityGroupRequestDto.setAuthorityGroupId(10000000001L);
		// when
		String url = "/api/admin/admin-users/1000000000/authority-group";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminMasterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminUserAuthorityGroupRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_ADMIN_USER_STATUS.name()));
	}
}
