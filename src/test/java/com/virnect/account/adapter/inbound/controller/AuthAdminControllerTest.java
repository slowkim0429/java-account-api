package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.exception.ErrorCode.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;
import java.util.List;
import java.util.Set;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.EmailAuthRequestDto;
import com.virnect.account.adapter.inbound.dto.request.TokenRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.AdminUserRequestDto;
import com.virnect.account.adapter.inbound.dto.request.validate.AdminUserLoginRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {"classpath:data/admin_users.sql", "classpath:data/domains.sql",
	"classpath:data/service_regions.sql", "classpath:data/service_region_locale_mappings.sql"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthAdminControllerTest {
	@Autowired
	public MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private RedisRepository redisRepository;

	private static final String BASE_API_URL = "/api/admin/auth";

	private final long duration = Duration.ofMinutes(30).getSeconds();

	private TokenResponseDto adminTokenResponseDto;

	@Autowired
	private RedisTemplate<String, Object> redisTemplate;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeEach
	void loginSessionDelete() {
		Set<String> keys = redisTemplate.keys("*");
		keys.forEach(key -> redisTemplate.delete(key));
	}

	@BeforeAll
	void setUp() {
		adminSetUp();
	}

	void adminSetUp() {
		Long adminUserId = 1000000001L;
		String email = "admin-user2@virnect.com";
		adminTokenResponseDto = tokenProvider.createAdminToken(
			adminUserId, email, null, List.of(Role.ROLE_ADMIN_USER));

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
	@DisplayName("POST 어드민 회원 가입 - 성공 OK 200")
	void signUp() throws Exception {
		//given
		String email = "admin-user@squars.io";
		String password = "admin1234!";
		Long localeId = 1000000118L;
		AdminUserRequestDto adminUserRequestDto = AdminUserRequestDto.of(email, password, localeId);

		String url = BASE_API_URL + "/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminUserRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 어드민 회원 가입 - 유효한 어드민 도메인이 아닌 경우 BAD_REQUEST 400")
	void signUpByInvalidEmailDomain() throws Exception {
		//given
		String email = "admin-user@test.com";
		String password = "admin1234!";
		Long localeId = 1000000118L;
		AdminUserRequestDto adminUserRequestDto = AdminUserRequestDto.of(email, password, localeId);

		String url = BASE_API_URL + "/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminUserRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST 어드민 회원 가입 - 이미 어드민 회원 가입을 요청한 경우 CONFLICT 409")
	void signUpByExistRegisteredEmail() throws Exception {
		//given
		String email = "admin-user1@virnect.com";
		String password = "admin1234!";
		Long localeId = 1000000118L;
		AdminUserRequestDto adminUserRequestDto = AdminUserRequestDto.of(email, password, localeId);

		String url = BASE_API_URL + "/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminUserRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_APPLIED_USER.name()));
	}

	@Test
	@DisplayName("POST 어드민 회원 가입 - 이미 가입한 유저인 경우 CONFLICT 409")
	void signUpByAlreadyJoinedAdminUser() throws Exception {
		//given
		String email = "admin-user2@virnect.com";
		String password = "admin1234!";
		Long localeId = 1000000118L;
		AdminUserRequestDto adminUserRequestDto = AdminUserRequestDto.of(email, password, localeId);

		String url = BASE_API_URL + "/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminUserRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_USER.name()));
	}

	@Test
	@DisplayName("POST 어드민 회원 가입 - 이메일이 대소문자만 다르게 이미 가입되어 있는 경우 CONFLICT 409")
	void signupByAlreadyExistedInsensitiveEmail() throws Exception {
		//given
		String email = "Admin-User2@virnect.com";
		String password = "admin1234!";
		Long localeId = 1000000118L;
		AdminUserRequestDto adminUserRequestDto = AdminUserRequestDto.of(email, password, localeId);

		String url = BASE_API_URL + "/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminUserRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_USER.name()));
	}

	@Test
	@DisplayName("POST 어드민 로그인 - 성공 OK 200")
	void login() throws Exception {
		//given
		String email = "admin-user2@virnect.com";
		String password = "qwer123!";
		AdminUserLoginRequestDto adminLoginRequestDto = AdminUserLoginRequestDto.of(email, password);

		String url = BASE_API_URL + "/login";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminLoginRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 어드민 로그인 - 비밀번호가 틀린경우 BAD_REQUEST 400")
	void loginByInvalidPassword() throws Exception {
		//given
		String email = "admin-user2@squars.io";
		String password = "admin12345!";
		AdminUserLoginRequestDto adminLoginRequestDto = AdminUserLoginRequestDto.of(email, password);

		String url = BASE_API_URL + "/login";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminLoginRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.customError").value(INVALID_USER.name()));
	}

	@Test
	@DisplayName("POST 어드민 로그인 - 존재하지 않는 유저일 경우 BAD_REQUEST 400")
	void loginByNonExistentEmail() throws Exception {
		//given
		String email = "admin-userrr@squars.io";
		String password = "admin12345!";
		AdminUserLoginRequestDto adminLoginRequestDto = AdminUserLoginRequestDto.of(email, password);

		String url = BASE_API_URL + "/login";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(adminLoginRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.customError").value(INVALID_USER.name()));
	}

	@Test
	@DisplayName("POST 어드민 로그아웃 - 성공 OK 200")
	void logout() throws Exception {
		//given
		adminSetUp();
		String logoutUrl = BASE_API_URL + "/logout";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(logoutUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 어드민 토큰 재발급 - 성공 OK 200")
	void reissue() throws Exception {
		//given
		TokenRequestDto tokenRequestDto = TokenRequestDto.of(
			adminTokenResponseDto.getAccessToken(), adminTokenResponseDto.getRefreshToken());

		String url = BASE_API_URL + "/reissue";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(tokenRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 비밀번호 재설정 인증코드 전송 - 200 OK")
	void sendResetPasswordEmailAuthCode() throws Exception {
		// given
		EmailAuthRequestDto emailAuthRequestDto = new EmailAuthRequestDto();
		emailAuthRequestDto.setEmail("admin-user1@virnect.com");
		String url = BASE_API_URL + "/password/email";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(emailAuthRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 비밀번호 재설정 인증코드 전송 - 회원이 존재하지 않는경우 404")
	void sendResetPasswordEmailAuthCodeWithNotFound() throws Exception {
		// given
		EmailAuthRequestDto emailAuthRequestDto = new EmailAuthRequestDto();
		emailAuthRequestDto.setEmail("admin-user13@virnect.com");
		String url = BASE_API_URL + "/password/email";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(emailAuthRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ADMIN_USER.name()));
	}

	@Test
	@DisplayName("GET 이메일 인증 코드 확인 - 확인 성공 OK 200")
	void verifyResetPasswordEmailAuthCode() throws Exception {
		//given
		String email = "slowkim@aaaa.com";
		String authCode = "123456";
		redisRepository.setAdminResetPasswordAsSecond(email, authCode, duration);
		String url = String.format(BASE_API_URL + "/password/email/verification?email=%s&code=%s", email, authCode);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET 이메일 인증 코드 확인 - 인증 코드가 일치하지 않는 경우 BAD_REQUEST 400")
	void verifyEmailAuthCodeWithNotMatchedValue() throws Exception {
		//given
		String email = "slowkim@aaaa.com";
		String authCode = "123456";
		redisRepository.setAdminResetPasswordAsSecond(email, authCode, duration);
		String url = String.format(BASE_API_URL + "/password/email/verification?email=%s&code=%s", email, "123211");

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_EMAIL_AUTH_CODE.name()));
	}

	@Test
	@DisplayName("PUT 비밀번호 재설정 - 확인 성공 OK 200")
	void updatePasswordWithoutAuthentication() throws Exception {
		//given
		UpdatePasswordRequestDto updatePasswordRequestDto = new UpdatePasswordRequestDto();
		updatePasswordRequestDto.setEmail("admin-user2@virnect.com");
		updatePasswordRequestDto.setCode("123456");
		updatePasswordRequestDto.setNewPassword("qwer123@");
		updatePasswordRequestDto.setCheckPassword("qwer123@");
		redisRepository.setAdminResetPasswordAsSecond("admin-user2@virnect.com", "123456", duration);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_API_URL + "/password")
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT 비밀번호 재설정 with does not exist code - 인증코드가 만료되었을때 GONE 410")
	void updatePasswordWithoutAuthenticationWithDoesNotExistCodeTest() throws Exception {
		//given
		UpdatePasswordRequestDto updatePasswordRequestDto = new UpdatePasswordRequestDto();
		updatePasswordRequestDto.setEmail("admin-user2@virnect.com");
		updatePasswordRequestDto.setCode("123456");
		updatePasswordRequestDto.setNewPassword("qwer123@");
		updatePasswordRequestDto.setCheckPassword("qwer123@");

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_API_URL + "/password")
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isGone())
			.andExpect(jsonPath("$.customError").value(EXPIRED_EMAIL_SESSION_CODE.name()));
	}

	@Test
	@DisplayName("PUT 비밀번호 재설정 with does not exist code - 인증 코드가 일치하지 않는 경우 BAD_REQUEST 400")
	void updatePasswordWithoutAuthenticationWithNotMatchedValue() throws Exception {
		//given
		UpdatePasswordRequestDto updatePasswordRequestDto = new UpdatePasswordRequestDto();
		updatePasswordRequestDto.setEmail("admin-user2@virnect.com");
		updatePasswordRequestDto.setCode("123456");
		updatePasswordRequestDto.setNewPassword("qwer123@");
		updatePasswordRequestDto.setCheckPassword("qwer123@");
		redisRepository.setAdminResetPasswordAsSecond(
			"admin-user2@virnect.com", "423523", duration);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_API_URL + "/password")
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_EMAIL_AUTH_CODE.name()));
	}
}
