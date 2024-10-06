package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.exception.ErrorCode.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.adapter.outbound.request.HubSpotTokenRefreshRequestDto;
import com.virnect.account.adapter.outbound.response.HubSpotTokenResponse;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.inbound.FileService;
import com.virnect.account.port.inbound.UserService;
import com.virnect.account.port.outbound.HubspotApiRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.WorkspaceAPIRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {"classpath:data/users.sql", "classpath:data/admin_users.sql",
	"classpath:data/service_region_locale_mappings.sql", "classpath:data/service_regions.sql"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserControllerTest {
	@Autowired
	public MockMvc mockMvc;
	@Autowired
	public UserService userService;
	@MockBean
	FileService fileService;
	@MockBean
	WorkspaceAPIRepository workspaceAPIRepository;
	@MockBean
	HubspotApiRepository hubspotApiRepository;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private RedisRepository redisRepository;
	private TokenResponseDto adminTokenResponseDto;
	private TokenResponseDto userTokenResponseDto;
	private TokenResponseDto organizationOwnerTokenResponseDto;
	private TokenResponseDto organizationRegisterTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeAll
	void setUp() throws Exception {
		adminSetUp();
		userSetup();
		organizationOwnerSetup();
		organizationRegisterSetup();
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

	void userSetup() {
		Long userId = 1000000005L;
		Long organizationId = 0L;
		String nickname = "user1";
		String email = "user1@guest.com";
		String language = "ko_KR";
		List<Role> roles = Arrays.stream("ROLE_USER".split(","))
			.map(Role::valueOf)
			.collect(Collectors.toList());

		userTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, nickname, email, language, roles);

		redisRepository.setObjectValue(
			userId.toString(), userTokenResponseDto.getRefreshToken(),
			userTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	void organizationRegisterSetup() {
		Long userId = 1000000001L;
		Long organizationId = 1000000003L;
		String nickname = "organizationRegister nickname";
		String email = "organizationRegister@virnect.com";
		String language = "ko_KR";
		List<Role> roles = Arrays.stream("ROLE_USER,ROLE_ORGANIZATION_OWNER".split(","))
			.map(Role::valueOf)
			.collect(Collectors.toList());

		organizationRegisterTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, nickname, email, language, roles);

		redisRepository.setObjectValue(
			userId.toString(), organizationRegisterTokenResponseDto.getRefreshToken(),
			organizationRegisterTokenResponseDto.getAccessTokenExpiresIn()
		);
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

	void hubSpotMockUp() {
		HubSpotTokenResponse hubSpotTokenResponse = new HubSpotTokenResponse();
		hubSpotTokenResponse.setAccessToken("accessToken");
		hubSpotTokenResponse.setTokenType("bearer");
		hubSpotTokenResponse.setExpiresIn(1800L);
		when(hubspotApiRepository.getHubspotToken(any(HubSpotTokenRefreshRequestDto.class))).thenReturn(
			hubSpotTokenResponse);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(adminTokenResponseDto.getAccessToken()));
		redisRepository.deleteObjectValue(tokenProvider.getUserNameFromJwtToken(userTokenResponseDto.getAccessToken()));
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(organizationOwnerTokenResponseDto.getAccessToken()));
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(organizationRegisterTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("PUT User Update - name update ok 200")
	void updateByName() throws Exception {
		//given
		hubSpotMockUp();

		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
		userUpdateRequestDto.setName("test1");

		// when
		String url = "/api/users";

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT User Update - locale update ok 200")
	void updateByLocale() throws Exception {
		//given
		hubSpotMockUp();

		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
		userUpdateRequestDto.setLocaleId(1000000000L);

		// when
		String url = "/api/users";

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT User Update - marketInfoReceive update ok 200")
	void updateByMarketInfoReceive() throws Exception {
		//given
		hubSpotMockUp();

		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
		userUpdateRequestDto.setMarketInfoReceive("ACCEPT");

		// when
		String url = "/api/users";

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT 회원 정보 수정 - zone id 수정 성공 OK 200")
	void updateByZoneId() throws Exception {
		//given
		hubSpotMockUp();

		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
		userUpdateRequestDto.setZoneId("Africa/Lome");

		// when
		String url = "/api/users";

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT 회원 정보 수정 - 존재하지 않는 zone id 일 경우 NOT_FOUND 404")
	void updateByNonExistentZoneId() throws Exception {
		//given
		hubSpotMockUp();

		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
		userUpdateRequestDto.setZoneId("zzz/zzz");

		// when
		String url = "/api/users";

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_SERVICE_TIME_ZONE.name()));
	}

	@Test
	@DisplayName("PUT User Update - 잘못된 요청 400")
	void updateInvalidInputValue() throws Exception {
		//given
		hubSpotMockUp();

		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();

		// when
		String url = "/api/users";

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT User Update - locale id 를 찾지 못할경우 404")
	void updateNotfoundLocaleId() throws Exception {
		//given
		UserUpdateRequestDto userUpdateRequestDto = new UserUpdateRequestDto();
		userUpdateRequestDto.setLocaleId(9000000000L);

		// when
		String url = "/api/users";

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(userUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_LOCALE.name()));
	}

	@Test
	@DisplayName("POST 이미지 업로드 - ok 200")
	void imageUpdate() throws Exception {
		//given
		MockMultipartFile profileImage = new MockMultipartFile(
			"profileImage", "imagefile.jpeg", "image/jpeg", "<<jpeg data>>".getBytes());

		// when
		String url = "/api/users/profile-image";

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(url);
		builder.with(request -> {
			request.setMethod("POST");
			request.addHeader(
				HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto));
			return request;
		});

		mockMvc.perform(builder
				.file(profileImage))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT 이미지 삭제 - ok 200")
	void deleteCurrentUserProfileImage() throws Exception {

		// when
		String url = "/api/users/profile-image/delete";

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
	@DisplayName("GET me UNAUTHORIZED_401")
	void getMy_UNAUTHORIZED_401() throws Exception {
		//given

		// when
		String url = "/api/users/me";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isUnauthorized())
			.andReturn();
	}

	@Test
	@DisplayName("GET me")
	void getMy() throws Exception {
		// when
		String url = "/api/users/me";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("user1@guest.com"))
			.andExpect(jsonPath("$.nickname").value("user"))
			.andExpect(jsonPath("$.localeId").value("1000000118"))
			.andExpect(jsonPath("$.localeCode").value("KR"));
	}

	@Test
	@DisplayName("GET user param userId NOT_FOUND_404")
	void getUser_NOT_FOUND_404() throws Exception {
		//given
		String userId = "123456987412";

		// when
		String url = String.format("/api/users/%s", userId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andReturn();
	}

	@Test
	@DisplayName("GET user param email")
	void getUser_byEmail() throws Exception {
		//given
		String email = "slowkim@gggg.com";

		// when
		String url = String.format("/api/users/email/%s/", email);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("slowkim@gggg.com"))
			.andExpect(jsonPath("$.nickname").value("admin"))
			.andReturn();
	}

	@Test
	@DisplayName("GET user exist by email")
	void getExistUserByEmail() throws Exception {
		//given
		String email = "slowkim@gggg.com";

		// when
		String url = String.format("/api/users/exist?email=%s", email);
		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("GET user exist by email NOT_FOUND_404_URL_INCORRECT")
	void getExistUserByEmail_NOT_FOUND_404_URL_INCORRECT() throws Exception {
		//given
		String email = "fgdfdgg@6mail.com";

		// when
		String url = String.format("/api/users1/exists?email=%s", email);
		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andReturn();
	}

	@Test
	@DisplayName("GET user exist by email METHOD_NOT_ALLOWED_405")
	void getUserByEmail_METHOD_NOT_ALLOWED_405() throws Exception {
		//given
		String email = "fgdfdgg@6mail.com";

		// when
		String url = String.format("/api/users/exist?email=%s", email);
		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isMethodNotAllowed())
			.andReturn();
	}

	@Test
	@DisplayName("Post passwordVerification")
	void passwordVerification() throws Exception {
		//given
		String password = "12345678";

		PasswordVerificationRequestDto passwordVerificationRequestDto = new PasswordVerificationRequestDto();
		passwordVerificationRequestDto.setPassword(password);

		// when
		String url = "/api/users/password/verification";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto))
					.content(objectMapper.writeValueAsString(passwordVerificationRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("Post passwordVerification with invalid input")
	void passwordVerification_withInvalidInput() throws Exception {
		//given
		String password = "1111";

		PasswordVerificationRequestDto passwordVerificationRequestDto = new PasswordVerificationRequestDto();
		passwordVerificationRequestDto.setPassword(password);

		// when
		String url = "/api/users/password/verification";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto))
					.content(objectMapper.writeValueAsString(passwordVerificationRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.customError").value(INVALID_USER.name()))
			.andReturn();
	}

	@Test
	@DisplayName("PUT 로그인 유저 password 재설정 - ok 200")
	void updatePassword() throws Exception {
		//given
		UserUpdatePasswordRequestDto updatePasswordRequestDto = new UserUpdatePasswordRequestDto();
		updatePasswordRequestDto.setPassword("qwer123!");
		updatePasswordRequestDto.setNewPassword("qwer123@");
		updatePasswordRequestDto.setCheckPassword("qwer123@");

		// when
		String url = "/api/users/password";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT 로그인 유저 password 재설정 - 현재 비밀번호가 틀린경우 401")
	void updatePasswordInvalidUser() throws Exception {
		//given
		UserUpdatePasswordRequestDto updatePasswordRequestDto = new UserUpdatePasswordRequestDto();
		updatePasswordRequestDto.setPassword("qwer123#");
		updatePasswordRequestDto.setNewPassword("qwer123@");
		updatePasswordRequestDto.setCheckPassword("qwer123@");

		// when
		String url = "/api/users/password";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.customError").value(INVALID_USER.name()));
	}

	@Test
	@DisplayName("PUT 로그인 유저 password 재설정 - 바꾸려는 비밀번호가 현재비밀번호인 경우 409")
	void updatePasswordDuplicatePassword() throws Exception {
		//given
		UserUpdatePasswordRequestDto updatePasswordRequestDto = new UserUpdatePasswordRequestDto();
		updatePasswordRequestDto.setPassword("qwer123!");
		updatePasswordRequestDto.setNewPassword("qwer123!");
		updatePasswordRequestDto.setCheckPassword("qwer123!");

		// when
		String url = "/api/users/password";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_PASSWORD.name()));
	}
}
