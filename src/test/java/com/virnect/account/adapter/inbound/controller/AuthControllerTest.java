package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.exception.ErrorCode.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.Duration;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jayway.jsonpath.JsonPath;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;

import com.virnect.account.adapter.inbound.dto.request.EmailAuthRequestDto;
import com.virnect.account.adapter.inbound.dto.request.LoginRequestDto;
import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.TokenRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.password.EmailChangePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.AcceptOrReject;
import com.virnect.account.domain.enumclass.InviteRole;
import com.virnect.account.domain.enumclass.InviteType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.outbound.ContractAPIRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.SquarsAPIRepository;
import com.virnect.account.port.outbound.WorkspaceAPIRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {"classpath:data/users.sql", "classpath:data/organizations.sql", "classpath:data/invite.sql",
	"classpath:data/service_regions.sql", "classpath:data/service_region_locale_mappings.sql",
	"classpath:data/license_grades.sql", "classpath:data/licenses.sql", "classpath:data/products.sql",
	"classpath:data/admin_users.sql", "classpath:data/domains.sql", "classpath:data/items.sql",
	"classpath:data/service_time_zones.sql"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class AuthControllerTest {
	private final long duration = Duration.ofMinutes(30).getSeconds();
	@Autowired
	public MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;
	@Autowired
	private TokenProvider tokenProvider;
	@MockBean
	private WorkspaceAPIRepository workspaceAPIRepository;
	@MockBean
	private SquarsAPIRepository squarsAPIRepository;
	@MockBean
	private ContractAPIRepository contractAPIRepository;
	@Autowired
	private RedisTemplate<String, Object> redisTemplate;
	@Autowired
	private RedisRepository redisRepository;
	@Value("${security.jwt-config.secret}")
	private String secret;

	@BeforeEach
	void loginSessionDelete() {
		Set<String> keys = redisTemplate.keys("*");
		keys.forEach(key -> redisTemplate.delete(key));
	}

	@Test
	@DisplayName("POST emailAuthentication")
	void emailAuthentication() throws Exception {
		// given
		EmailAuthRequestDto emailAuthRequestDto = new EmailAuthRequestDto();
		emailAuthRequestDto.setEmail("slowkim@gggggg.com");
		String url = "/api/auth/email";

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
	@DisplayName("POST 회원 가입 이메일 인증 메일 전송 - 이미 가입된 유저일 경우 CONFLICT 409")
	void emailAuthByAlreadyExistedEmail() throws Exception {
		// given
		EmailAuthRequestDto emailAuthRequestDto = new EmailAuthRequestDto();
		emailAuthRequestDto.setEmail("slowkim@gggg.com");
		String url = "/api/auth/email";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(emailAuthRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_USER.name()));
	}

	@Test
	@DisplayName("POST 회원 가입 이메일 인증 메일 전송 - 대소문자만 다르게 이미 가입된 유저일 경우 CONFLICT 409")
	void emailAuthByAlreadyExistedInsensitiveEmail() throws Exception {
		// given
		EmailAuthRequestDto emailAuthRequestDto = new EmailAuthRequestDto();
		emailAuthRequestDto.setEmail("SLOWkim@gggg.com");
		String url = "/api/auth/email";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(emailAuthRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_USER.name()));
	}

	@Test
	@DisplayName("GET 이메일 인증 코드 확인 - 확인 성공 OK 200")
	void emailAuthCodeVerification() throws Exception {
		//given
		String email = "slowkim@aaaa.com";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);
		String url = String.format("/api/auth/email/verification?email=%s&code=%s", email, authCode);

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
	@DisplayName("GET 이메일 인증 코드 확인 - 존재하지 않거나 만료된 경우 GONE 410")
	void emailAuthCodeVerificationWithNonExistentKey() throws Exception {
		//given
		String email = "slowkim@aaaa.com";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);
		String url = String.format("/api/auth/email/verification?email=%s&code=%s", "slowkim@xxxx.com", authCode);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isGone())
			.andExpect(jsonPath("$.customError").value(EXPIRED_EMAIL_SESSION_CODE.name()));
	}

	@Test
	@DisplayName("GET 이메일 인증 코드 확인 - 인증 코드가 일치하지 않는 경우 BAD_REQUEST 400")
	void emailAuthCodeVerificationWithNotMatchedValue() throws Exception {
		//given
		String email = "slowkim@aaaa.com";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);
		String url = String.format("/api/auth/email/verification?email=%s&code=%s", email, "123211");

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
	@DisplayName("POST password emailAuthentication - 200 OK")
	void passwordEmailAuthentication() throws Exception {
		// given
		EmailChangePasswordRequestDto emailChangePasswordRequestDto = new EmailChangePasswordRequestDto();
		emailChangePasswordRequestDto.setEmail("slowkim@gggg.com");
		String url = "/api/auth/password/email";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(emailChangePasswordRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST password emailAuthentication - 회원이 아닌경우 404")
	void passwordEmailAuthenticationNotFound() throws Exception {
		// given
		EmailChangePasswordRequestDto emailChangePasswordRequestDto = new EmailChangePasswordRequestDto();
		emailChangePasswordRequestDto.setEmail("slowkim@ggggggggg.com");
		String url = "/api/auth/password/email";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(emailChangePasswordRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_USER.name()));
	}

	@Test
	@DisplayName("POST 회원 가입 - 가입 성공 OK 200")
	void signup() throws Exception {
		//given

		String email = "slowkim@aaaa.com";
		String password = "qwer123!";
		String localeCode = "US";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);

		UserCreateRequestDto requestDto = UserCreateRequestDto.of(
			email, password, localeCode, AcceptOrReject.ACCEPT.name(), AcceptOrReject.ACCEPT.name(),
			AcceptOrReject.ACCEPT.name(), authCode, null
		);

		String url = "/api/auth/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 회원 가입 - 이름이 50자 이상인 locale name으로 가입 성공 OK 200 ")
	void signup_withTooLongLocaleName() throws Exception {
		//given

		String email = "slowkim@aaaa.com";
		String password = "qwer123!";
		String localeCode = "US";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);

		UserCreateRequestDto requestDto = UserCreateRequestDto.of(
			email, password, localeCode, AcceptOrReject.ACCEPT.name(), AcceptOrReject.ACCEPT.name(),
			AcceptOrReject.ACCEPT.name(), authCode, null
		);

		String url = "/api/auth/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 회원 가입 - 인증 번호가 다를 경우 BAD_REQUEST_400")
	void signupWithNotMatchedAuthValue() throws Exception {
		//given
		String email = "slowkim@aaaa.com";
		String password = "qwer123!";
		String localeCode = "US";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);

		UserCreateRequestDto requestDto = UserCreateRequestDto.of(
			email, password, localeCode, AcceptOrReject.ACCEPT.name(), AcceptOrReject.ACCEPT.name(),
			AcceptOrReject.ACCEPT.name(), "543132", null
		);

		String url = "/api/auth/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_EMAIL_AUTH_CODE.name()));
	}

	@Test
	@DisplayName("POST 회원 가입 - 존재하지 않거나 만료된 경우 GONE 410")
	void signupWithNonExistentKey() throws Exception {
		//given
		String email = "slowkim@aaaa.com";
		String password = "qwer123!";
		String localeCode = "US";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);

		UserCreateRequestDto requestDto = UserCreateRequestDto.of(
			"slowkim@xxxx.com", password, localeCode, AcceptOrReject.ACCEPT.name(), AcceptOrReject.ACCEPT.name(),
			AcceptOrReject.ACCEPT.name(), authCode, null
		);

		String url = "/api/auth/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isGone())
			.andExpect(jsonPath("$.customError").value(EXPIRED_EMAIL_SESSION_CODE.name()));
	}

	@Test
	@DisplayName("POST 회원 가입 - 개인 정보 처리 방침에 동의하지 않은 경우 BAD_REQUEST 400")
	void signupWithInvalidPrivacyPolicyValue() throws Exception {
		//given
		String email = "slowkim@aaaa.com";
		String password = "qwer123!";
		String localeCode = "US";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);

		UserCreateRequestDto requestDto = UserCreateRequestDto.of(
			email, password, localeCode, AcceptOrReject.ACCEPT.name(), AcceptOrReject.REJECT.name(),
			AcceptOrReject.ACCEPT.name(), authCode, null
		);

		String url = "/api/auth/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST 회원 가입 - 서비스 이용 약관에 동의하지 않은 경우 BAD_REQUEST 400")
	void signupWithTermsOfServiceValue() throws Exception {
		//given
		String email = "slowkim@aaaa.com";
		String password = "qwer123!";
		String localeCode = "US";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);

		UserCreateRequestDto requestDto = UserCreateRequestDto.of(
			email, password, localeCode, AcceptOrReject.ACCEPT.name(), AcceptOrReject.ACCEPT.name(),
			AcceptOrReject.REJECT.name(), authCode, null
		);

		String url = "/api/auth/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST 회원 가입 - 이미 가입되어 있는 유저일 경우 CONFLICT 409")
	void signupByAlreadyExistedEmail() throws Exception {
		//given
		String email = "slowkim@gggg.com";
		String password = "qwer123!";
		String localeCode = "US";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);

		UserCreateRequestDto requestDto = UserCreateRequestDto.of(
			email, password, localeCode, AcceptOrReject.ACCEPT.name(), AcceptOrReject.ACCEPT.name(),
			AcceptOrReject.ACCEPT.name(), authCode, null
		);

		String url = "/api/auth/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_USER.name()));
	}

	@Test
	@DisplayName("POST 회원 가입 - 이메일이 대소문자만 다르게 이미 가입되어 있는 경우 CONFLICT 409")
	void signupByAlreadyExistedInsensitiveEmail() throws Exception {
		//given
		String email = "OrganiOwner@virnect.com";
		String password = "qwer123!";
		String localeCode = "US";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email.toLowerCase(), authCode, duration);

		UserCreateRequestDto requestDto = UserCreateRequestDto.of(
			email, password, localeCode, AcceptOrReject.ACCEPT.name(), AcceptOrReject.ACCEPT.name(),
			AcceptOrReject.ACCEPT.name(), authCode, null
		);

		String url = "/api/auth/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_USER.name()));
	}

	@Test
	@DisplayName("POST 회원 가입 - 400 Error - 초대받아서 가입하려는 이메일과 초대받은 이메일이 동일하지 않은 경우")
	void signupWithInvalidEmail() throws Exception {
		//given
		String email = "slowkim@aaaa.com";
		String password = "qwer123!";
		String localeCode = "US";
		String authCode = "123456";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);

		String inviteToken = createInviteTicket(
			"inviteUser@virnect.com", 1000000001L, 1000000000L, 1000000000L, InviteType.GROUP,
			InviteRole.ROLE_GROUP_USER, 86400000L
		);

		UserCreateRequestDto requestDto = UserCreateRequestDto.of(
			email, password, localeCode, AcceptOrReject.ACCEPT.name(), AcceptOrReject.ACCEPT.name(),
			AcceptOrReject.ACCEPT.name(), authCode, inviteToken
		);

		String url = "/api/auth/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST 회원 가입 - referrer url 이 길이 제한을 넘는 경우 BAD_REQUEST 400")
	void signupByInvalidReferrerUrl() throws Exception {
		//given

		String email = "slowkim@aaaa.com";
		String password = "qwer123!";
		String localeCode = "US";
		String authCode = "123456";
		String referrerUrl = "https://www.llanfairpwllgwyngyllgogerychwyrndrobwllllantysiliogogogochuchaf.eu/query=icecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamicecreamice";
		redisRepository.setObjectValueAsSecond(email, authCode, duration);

		UserCreateRequestDto requestDto = UserCreateRequestDto.of(
			email, password, localeCode, AcceptOrReject.ACCEPT.name(), AcceptOrReject.ACCEPT.name(),
			AcceptOrReject.ACCEPT.name(), authCode, null
		);

		requestDto.setReferrerUrl(referrerUrl);

		String url = "/api/auth/signup";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST login")
	void signin() throws Exception {
		//given
		LoginRequestDto loginRequestDto = new LoginRequestDto();
		loginRequestDto.setEmail("organiowner-one@virnect.com");
		loginRequestDto.setPassword("12345678");

		// when
		String url = "/api/auth/login";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(loginRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.grantType").value("Bearer"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
			.andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty())
			.andExpect(MockMvcResultMatchers.jsonPath("$.accessTokenExpiresIn").isNotEmpty())
			.andReturn();
	}

	@Test
	@DisplayName("POST login UNAUTHORIZED_401")
	void signin_UNAUTHORIZED_401() throws Exception {
		//given
		LoginRequestDto loginRequestDto = new LoginRequestDto();
		loginRequestDto.setEmail("slowkim1@gggg.com");
		loginRequestDto.setPassword("12345678");

		// when
		String url = "/api/auth/login";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(loginRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isUnauthorized())
			.andReturn();
	}

	@Test
	@DisplayName("POST logout")
	void logout() throws Exception {
		//given
		LoginRequestDto loginRequestDto = new LoginRequestDto();
		loginRequestDto.setEmail("organiowner-one@virnect.com");
		loginRequestDto.setPassword("12345678");

		// when
		String loginUrl = "/api/auth/login";
		MvcResult loginResult = mockMvc.perform(
				MockMvcRequestBuilders
					.post(loginUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(loginRequestDto))
			)
			.andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.grantType").value("Bearer"))
			.andExpect(jsonPath("$.accessToken").isNotEmpty())
			.andExpect(jsonPath("$.refreshToken").isNotEmpty())
			.andExpect(jsonPath("$.accessTokenExpiresIn").isNotEmpty())
			.andReturn();

		TokenResponseDto loginResponse = objectMapper.readValue(
			loginResult.getResponse().getContentAsString(), TokenResponseDto.class
		);

		String logoutUrl = "/api/auth/logout";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(logoutUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, loginResponse.getGrantType() + " " + loginResponse.getAccessToken()))
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST logout FORBIDDEN_403")
	void logout_unauthorized() throws Exception {
		// given
		String logoutUrl = "/api/auth/logout";

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(logoutUrl)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isForbidden());
	}

	@Test
	@DisplayName("POST reissue")
	void reissue() throws Exception {
		//given
		LoginRequestDto loginRequestDto = new LoginRequestDto();
		loginRequestDto.setEmail("organiowner-one@virnect.com");
		loginRequestDto.setPassword("12345678");

		// when
		String url = "/api/auth/login";
		MvcResult result = mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(loginRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.grantType").value("Bearer"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
			.andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty())
			.andExpect(MockMvcResultMatchers.jsonPath("$.accessTokenExpiresIn").isNotEmpty())
			.andReturn();

		//given
		String accessToken = JsonPath.read(result.getResponse().getContentAsString(), "$.accessToken");
		String refreshToken = JsonPath.read(result.getResponse().getContentAsString(), "$.refreshToken");
		TokenRequestDto tokenRequestDto = TokenRequestDto.of(accessToken, refreshToken);

		// when
		String reissueUrl = "/api/auth/reissue";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(reissueUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(tokenRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.grantType").value("Bearer"))
			.andExpect(MockMvcResultMatchers.jsonPath("$.accessToken").isNotEmpty())
			.andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").isNotEmpty())
			.andExpect(MockMvcResultMatchers.jsonPath("$.refreshToken").value(refreshToken))
			.andExpect(MockMvcResultMatchers.jsonPath("$.accessTokenExpiresIn").isNotEmpty())
			.andReturn();
	}

	@Test
	@DisplayName("POST reissue - 401 Unauthorized - Access Token이 누락된 경우")
	void reissue_Unauthorized_withEmptyAccessToken() throws Exception {
		//given
		TokenRequestDto tokenRequestDto = TokenRequestDto.of(
			null, "refresh-token");

		// when
		String reissueUrl = "/api/auth/reissue";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(reissueUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(tokenRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.customError").value(INVALID_TOKEN.name()));
	}

	@Test
	@DisplayName("POST reissue - 401 Unauthorized - Refresh Token이 누락된 경우")
	void reissue_Unauthorized_withEmptyRefreshToken() throws Exception {
		//given
		TokenRequestDto tokenRequestDto = TokenRequestDto.of(
			"access-token", null);

		// when
		String reissueUrl = "/api/auth/reissue";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(reissueUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(tokenRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.customError").value(INVALID_TOKEN.name()));
	}

	@Test
	@DisplayName("POST reissue - 401 Unauthorized - Refresh Token이 만료된 경우")
	void reissue_Unauthorized_withExpiredRefreshToken() throws Exception {
		//given
		TokenResponseDto tokenResponseDto = tokenProvider.createToken(1000000000L, 1000000001L, "mock-nickname",
			"mock-email@virnect.com", "ko_KR",
			List.of(Role.ROLE_USER), -1L
		);
		TokenRequestDto tokenRequestDto = TokenRequestDto.of(
			tokenResponseDto.getAccessToken(), tokenResponseDto.getRefreshToken());

		// when
		String reissueUrl = "/api/auth/reissue";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(reissueUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(tokenRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.customError").value(INVALID_TOKEN.name()));
	}

	@Test
	@DisplayName("POST reissue - 401 Unauthorized - Refresh Token이 Redis에 없는 경우")
	void reissue_Unauthorized_withNotFoundRefreshToken() throws Exception {
		//given
		TokenResponseDto tokenResponseDto = tokenProvider.createToken(1000000000L, 1000000001L, "mock-nickname",
			"mock-email@virnect.com", "ko_KR",
			List.of(Role.ROLE_USER), 2592000000L
		);
		TokenRequestDto tokenRequestDto = TokenRequestDto.of(
			tokenResponseDto.getAccessToken(), tokenResponseDto.getRefreshToken());

		// when
		String reissueUrl = "/api/auth/reissue";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(reissueUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(tokenRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.customError").value(INVALID_TOKEN.name()));
	}

	@Test
	@DisplayName("POST reissue - 401 Unauthorized - Refresh Token이 Redis에 저장된 값과 상이한 경우")
	void reissue_Unauthorized_withInvalidRefreshToken() throws Exception {
		//given
		redisRepository.setObjectValue("1000000000", "signed-redis-token");

		TokenResponseDto tokenResponseDto = tokenProvider.createToken(1000000000L, 1000000001L, "mock-nickname",
			"mock-email@virnect.com", "ko_KR",
			List.of(Role.ROLE_USER), 2592000000L
		);
		TokenRequestDto tokenRequestDto = TokenRequestDto.of(
			tokenResponseDto.getAccessToken(), tokenResponseDto.getRefreshToken());

		// when
		String reissueUrl = "/api/auth/reissue";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(reissueUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(tokenRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isUnauthorized())
			.andExpect(jsonPath("$.customError").value(INVALID_TOKEN.name()));
	}

	@Test
	@DisplayName("POST resign")
	void resign() throws Exception {
		// given
		Long userId = 1000000001L;
		Long organizationId = 1000000000L;
		String nickname = "organization nickname";
		String email = "organiowner@virnect.com";
		String language = "ko_KR";
		List<Role> roles = Arrays.stream("ROLE_USER,ROLE_ORGANIZATION_OWNER".split(","))
			.map(Role::valueOf)
			.collect(Collectors.toList());

		TokenResponseDto loginResponse = tokenProvider.createToken(
			userId, organizationId, nickname, email, language, roles);

		redisRepository.setObjectValue(
			userId.toString(), loginResponse.getRefreshToken(),
			loginResponse.getAccessTokenExpiresIn()
		);

		PasswordVerificationRequestDto passwordVerificationRequestDto = new PasswordVerificationRequestDto();
		passwordVerificationRequestDto.setPassword("qwer123!");

		// when
		String resignUrl = "/api/auth/resign";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(resignUrl)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(passwordVerificationRequestDto))
					.header(HttpHeaders.AUTHORIZATION, loginResponse.getGrantType() + " " + loginResponse.getAccessToken()))
			.andDo(print())
			// then
			.andExpect(status().isOk())
			.andReturn();
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

	@Test
	@DisplayName("PUT update password")
	void updatePasswordTest() throws Exception {
		//given
		UpdatePasswordRequestDto updatePasswordRequestDto = new UpdatePasswordRequestDto();
		updatePasswordRequestDto.setNewPassword("q1w2e3r4T%");
		updatePasswordRequestDto.setCheckPassword("q1w2e3r4T%");
		updatePasswordRequestDto.setCode("123456");
		updatePasswordRequestDto.setEmail("updatePassword@virnect.com");

		redisRepository.setObjectValueAsSecond(
			updatePasswordRequestDto.getEmail(), updatePasswordRequestDto.getCode(), duration
		);

		String url = "/api/auth/password";

		// when
		mockMvc.perform(MockMvcRequestBuilders
				.put(url)
				.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT update password with does not exist code")
	void updatePasswordWithDoesNotExistCodeTest() throws Exception {
		//given
		UpdatePasswordRequestDto updatePasswordRequestDto = new UpdatePasswordRequestDto();
		updatePasswordRequestDto.setNewPassword("q1w2e3r4T%");
		updatePasswordRequestDto.setCheckPassword("q1w2e3r4T%");
		updatePasswordRequestDto.setCode("123456");
		updatePasswordRequestDto.setEmail("updatePassword@virnect.com");

		// when
		String url = "/api/auth/password";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			// then
			.andExpect(status().isGone())
			.andExpect(jsonPath("$.customError").value(EXPIRED_EMAIL_SESSION_CODE.name()));
	}

	@Test
	@DisplayName("PUT update password with duplicate password")
	void updatePassword_withDuplicatePassword() throws Exception {
		//given
		UpdatePasswordRequestDto updatePasswordRequestDto = new UpdatePasswordRequestDto();
		updatePasswordRequestDto.setNewPassword("qwer123!");
		updatePasswordRequestDto.setCheckPassword("qwer123!");
		updatePasswordRequestDto.setCode("123456");
		updatePasswordRequestDto.setEmail("organiowner@virnect.com");
		redisRepository.setObjectValueAsSecond(
			updatePasswordRequestDto.getEmail(), updatePasswordRequestDto.getCode(), duration
		);

		String url = "/api/auth/password";

		// when
		mockMvc.perform(MockMvcRequestBuilders
				.put(url)
				.content(objectMapper.writeValueAsString(updatePasswordRequestDto))
				.contentType(MediaType.APPLICATION_JSON))
			.andDo(print())
			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_PASSWORD.name()));
	}
}
