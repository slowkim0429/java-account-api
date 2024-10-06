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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.DateRangeType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.outbound.ContractAPIRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.SquarsAPIRepository;
import com.virnect.account.port.outbound.WorkspaceAPIRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/admin_users.sql", "classpath:data/organizations.sql", "classpath:data/products.sql",
	"classpath:data/licenses.sql", "classpath:data/license_grades.sql", "classpath:data/organization_licenses.sql",
	"classpath:data/organization_license_attributes.sql",
	"classpath:data/organization_license_additional_attributes.sql",
	"classpath:data/organization_contracts.sql",
	"classpath:data/license_attributes.sql",
	"classpath:data/items.sql",
	"classpath:data/service_regions.sql", "classpath:data/service_region_locale_mappings.sql",
	"classpath:data/users.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganizationAdminControllerTest {
	@Autowired
	public MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@MockBean(name = "workspaceAPIRepository")
	private WorkspaceAPIRepository workspaceAPIRepository;

	@MockBean(name = "squarsAPIRepository")
	private SquarsAPIRepository squarsAPIRepository;

	@Autowired
	private TokenProvider tokenProvider;
	@Autowired
	private RedisRepository redisRepository;

	@MockBean
	private ContractAPIRepository contractAPIRepository;

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
	@DisplayName("GET Organization param organizationId")
	void getOrganizationById() throws Exception {
		//given
		String organizationId = "1000000000";

		// when
		String url = String.format("/api/admin/organizations/%s", organizationId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("support1@virnect.com"))
			.andReturn();
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - OK 200")
	void getOrganizations() throws Exception {
		// when
		String url = "/api/admin/organizations";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(16));
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - organizationName 조회 OK 200")
	void getOrganizationsByOrganizationName() throws Exception {
		// when
		String url = "/api/admin/organizations?OrganizationName=virnect";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(13));
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - emailDomain 조회 OK 200")
	void getOrganizationsByEmailDomain() throws Exception {
		// when
		String url = "/api/admin/organizations?emailDomain=gmail";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - localeName 조회 OK 200")
	void getOrganizationsByLocaleName() throws Exception {
		// when
		String url = "/api/admin/organizations?localeName=United States";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - province 조회 OK 200")
	void getOrganizationsByProvince() throws Exception {
		// when
		String url = "/api/admin/organizations?province=Boston";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - stateName 조회 OK 200")
	void getOrganizationsByStateName() throws Exception {
		// when
		String url = "/api/admin/organizations?stateName=Massachusetts";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - 쿼리스트링(dateRangeType)으로 조회 성공 200 OK")
	void getOrganizationsByDateRangeTypeOfAll() throws Exception {
		// when
		String url = "/api/admin/organizations";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.queryParam("dateRangeType", DateRangeType.ALL.name())
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(16));
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - 쿼리스트링(dateRangeType)으로 조회 성공 200 OK")
	void getOrganizationsByDateRangeTypeOfWeek() throws Exception {
		// when
		String url = "/api/admin/organizations";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.queryParam("dateRangeType", DateRangeType.WEEK.name())
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - 쿼리스트링(dateRangeType)으로 조회 성공 200 OK")
	void getOrganizationsByDateRangeTypeOfMonth() throws Exception {
		// when
		String url = "/api/admin/organizations";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.queryParam("dateRangeType", DateRangeType.MONTH.name())
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(2));
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - 쿼리스트링(dateRangeType)으로 조회 성공 200 OK")
	void getOrganizationsByDateRangeTypeOfCustom() throws Exception {
		// when
		String url = "/api/admin/organizations";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.queryParam("dateRangeType", DateRangeType.CUSTOM.name())
					.queryParam("startDate", "2021-08-01")
					.queryParam("endDate", "2021-12-25")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(5));
	}

	@Test
	@DisplayName("GET Organization 목록 조회 - Custom 기간으로 조회하지만 날짜 관련 필수 데이터가 입력되지 않았을 경우 400 BAD_REQUEST")
	void getOrganizationsByDateRangeTypeOfCustomWithoutRequiredData() throws Exception {
		// when
		String url = "/api/admin/organizations";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.queryParam("dateRangeType", DateRangeType.CUSTOM.name())
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("GET Organization Contract - 200 OK")
	void getOrganizationLicenseByContract() throws Exception {
		// when
		String url = "/api/admin/organizations/contracts/1000000000";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET Organization Contract - 404 NotFound")
	void getOrganizationContract_withNotFound() throws Exception {
		// when
		String url = "/api/admin/organizations/contracts/10000099999";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ORGANIZATION_CONTRACT.name()));
	}

	@Test
	@DisplayName("POST Organization Data Synchronize - 200 OK")
	void synchronizeOrganization() throws Exception {
		// given
		String url = "/api/admin/organizations/1000000000/synchronize";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST Organization Data Synchronize - 존재하지 않는 organization일 경우 404 NOT_FOUND")
	void synchronizeOrganizationNotFound() throws Exception {
		// given
		String url = "/api/admin/organizations/9000000000/synchronize";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ORGANIZATION.name()));
	}

	@Test
	@DisplayName("GET Organization Revision 목록 조회 - 조회 성공 OK 200")
	void getOrganizationRevisions() throws Exception {
		// given
		String organizationId = "1000000000";
		String url = String.format("/api/admin/organizations/%s/revisions", organizationId);

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

}
