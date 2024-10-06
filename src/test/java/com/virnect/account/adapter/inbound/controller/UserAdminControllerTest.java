package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.exception.ErrorCode.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.util.List;

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
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.inbound.FileService;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.WorkspaceAPIRepository;
import com.virnect.account.security.jwt.TokenProvider;
import com.virnect.account.util.ZonedDateTimeUtil;

@ActiveProfiles("test")
@Sql(scripts = {"classpath:data/users.sql", "classpath:data/service_region_locale_mappings.sql",
	"classpath:data/organizations.sql", "classpath:data/admin_users.sql", "classpath:data/domains.sql",})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UserAdminControllerTest {
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
	private TokenResponseDto userTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeAll
	void setUp() throws Exception {
		adminSetUp();
		userSetUp();
	}

	void adminSetUp() {
		Long adminUserId = 1000000001L;
		String email = "admin-user2@virnect.com";
		adminTokenResponseDto = tokenProvider.createAdminToken(adminUserId, email, null, List.of(Role.ROLE_ADMIN_USER));

		redisRepository.setAdminRefreshToken(
			adminUserId.toString(), adminTokenResponseDto.getRefreshToken(), tokenProvider.getRefreshTokenExpireTime());
	}

	void userSetUp() {
		Long userId = 1000000005L;
		Long organizationId = 0L;
		String email = "user1@guest.com";
		userTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, "user1", email, "ko_KR", List.of(Role.ROLE_USER));
		redisRepository.setObjectValue(userId.toString(), userTokenResponseDto.getRefreshToken(),
			userTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	@Test
	@DisplayName("GET users")
	void getUsers() throws Exception {
		// when
		String url = "/api/admin/users";
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET users with email")
	void getUsers_WithEmail() throws Exception {
		//given
		String email = "slowkim@gggg.com";

		// when
		String url = String.format("/api/admin/users?email=%s", email);
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk()).andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET users - with Locale id")
	void getUsersWithLocaleId() throws Exception {
		// when
		String localeId = "1000000118";
		String url = "/api/admin/users?localeId=" + localeId;
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET users - with user Status")
	void getUsersWithUserStatus() throws Exception {
		// when
		String status = MembershipStatus.RESIGN.name();
		String url = "/api/admin/users?status=" + status;
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(0));
	}

	@Test
	@DisplayName("GET 유저 목록 조회 - 쿼리스트링으로(referrerUrl) 조회 성공 OK 200")
	void getUsersByReferrerUrl() throws Exception {
		// when
		String url = "/api/admin/users?referrerUrl=https://www.naver";
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(3));
	}

	@Test
	@DisplayName("GET 유저 목록 조회 - 쿼리스트링으로(emailDomain) 조회 성공 OK 200")
	void getUsersByEmailDomain() throws Exception {
		// when
		String url = "/api/admin/users?emailDomain=gmail";
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET 유저 목록 조회 - 쿼리스트링으로(localeName) 조회 성공 OK 200")
	void getUsersByLocaleName() throws Exception {
		// when
		String url = "/api/admin/users?localeName=United States";
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET 유저 목록 조회 - 쿼리스트링으로(marketInfoReceive) 조회 성공 OK 200")
	void getUsersByMarketInfoReceive() throws Exception {
		// when
		String url = "/api/admin/users?marketInfoReceive=REJECT";
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET 유저 목록 조회 - 쿼리스트링으로(regionCode) 조회 성공 OK 200")
	void getUsersByRegionCode() throws Exception {
		// when
		String url = "/api/admin/users?regionCode=US";
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET 유저 목록 조회 - 쿼리스트링으로(WEEK 단위) 조회 성공 OK 200")
	void getUsersByWeekRangeType() throws Exception {
		// when
		String url = "/api/admin/users?dateRangeType=WEEK";
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET 유저 목록 조회 - 쿼리스트링으로(커스텀 단위) 조회 성공 OK 200")
	void getUsersByCustomRangeType() throws Exception {
		// when
		LocalDate startDate = ZonedDateTimeUtil.zoneOffsetOfUTC().toLocalDate().minusDays(30);
		LocalDate endDate = ZonedDateTimeUtil.zoneOffsetOfUTC().toLocalDate().minusDays(1);
		String url = String.format(
			"/api/admin/users?dateRangeType=%s&startDateOfCreatedDate=%s&endDateOfCreatedDate=%s", "CUSTOM", startDate,
			endDate
		);
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk()).andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(2));
	}

	@Test
	@DisplayName("GET user")
	void getUserById() throws Exception {
		// when
		String userId = "1000000014";
		String url = String.format("/api/admin/users/%s", userId);
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("organiowner-one@virnect.com"))
			.andExpect(jsonPath("$.nickname").value("partnermaster2"))
			.andExpect(jsonPath("$.referrerUrl").value("https://www.naver.com/"));
	}

	@Test
	@DisplayName("GET user param userId FORBIDDEN_403")
	void getUser_FORBIDDEN_403() throws Exception {
		//given
		String userId = "1000000000";

		// when
		String url = String.format("/api/admin/users/%s", userId);
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(userTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isForbidden()).andReturn();
	}

	@Test
	@DisplayName("GET user param userId BAD_REQUEST_400")
	void getUser_BAD_REQUEST_400_MIN() throws Exception {
		//given
		String userId = "10000";

		// when
		String url = String.format("/api/admin/users/%s", userId);
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isBadRequest()).andReturn();
	}

	@Test
	@DisplayName("GET user param userId")
	void getUser_byUserId() throws Exception {
		//given
		String userId = "1000000000";

		// when
		String url = String.format("/api/admin/users/%s", userId);
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("slowkim@gggg.com"))
			.andExpect(jsonPath("$.nickname").value("admin"))
			.andReturn();
	}

	@Test
	@DisplayName("GET user param userId BAD_REQUEST_400")
	void getUser_BAD_REQUEST_400_STRING() throws Exception {
		//given
		String userId = "gfdgfgf";

		// when
		String url = String.format("/api/admin/users/%s", userId);
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isBadRequest()).andReturn();
	}

	@Test
	@DisplayName("GET User Revision 목록 조회 - 조회 성공 OK 200")
	void getOrganizationRevisions() throws Exception {
		// given
		String userId = "1000000000";
		String url = String.format("/api/admin/users/%s/revisions", userId);

		// when
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(
					org.springframework.http.HttpHeaders.AUTHORIZATION,
					getAuthorizationBearerToken(adminTokenResponseDto)
				)
				.contentType(MediaType.APPLICATION_JSON)).andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET 유저 통계 조회 - 성공 OK 200")
	void getStatistics() throws Exception {
		// given
		String url = "/api/admin/users/statistics?dateRangeType=ALL";

		//when
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.signupUserCount").value(16))
			.andExpect(jsonPath("$.resignedUserCount").value(0));
	}

	@Test
	@DisplayName("GET 유저 통계 조회 - dateRangeType이 null인 경우 BAD_REQUEST 400")
	void getStatisticsNotDateRangeType() throws Exception {
		// given
		String url = "/api/admin/users/statistics";

		//when
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("GET 유저 통계 조회 - 쿼리스트링(dateRangeType) WEEK, 성공 OK 200")
	void getStatisticsByWeekDateRangeType() throws Exception {
		// given
		String url = "/api/admin/users/statistics?dateRangeType=WEEK";

		//when
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.signupUserCount").value(1))
			.andExpect(jsonPath("$.resignedUserCount").value(0));
	}

	@Test
	@DisplayName("GET 유저 통계 조회 - 쿼리스트링(dateRangeType) MONTH, 성공 OK 200")
	void getStatisticsByMonthDateRangeType() throws Exception {
		// given
		String url = "/api/admin/users/statistics?dateRangeType=MONTH";

		//when
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.signupUserCount").value(2))
			.andExpect(jsonPath("$.resignedUserCount").value(0));
	}

	@Test
	@DisplayName("GET 유저 통계 조회 - 쿼리스트링(dateRangeType) CUSTOM, 성공 OK 200")
	void getStatisticsByCustomDateRangeType() throws Exception {
		// given
		LocalDate startDate = ZonedDateTimeUtil.zoneOffsetOfUTC().toLocalDate().minusMonths(1);
		LocalDate endDate = ZonedDateTimeUtil.zoneOffsetOfUTC().toLocalDate().plusDays(1);
		String url = String.format(
			"/api/admin/users/statistics?dateRangeType=CUSTOM&startDate=%s&endDate=%s", startDate, endDate);

		//when
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.signupUserCount").value(2))
			.andExpect(jsonPath("$.resignedUserCount").value(0));
	}

	@Test
	@DisplayName("GET joined 유저 통계 조회 - 성공 OK 200")
	void getStatisticsByJoinedUser() throws Exception {
		// given
		String url = "/api/admin/users/statistics/joined-users";

		//when
		mockMvc.perform(MockMvcRequestBuilders.get(url)
				.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
				.accept(MediaType.APPLICATION_JSON))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.joinedUserCount").value(16));
	}
}
