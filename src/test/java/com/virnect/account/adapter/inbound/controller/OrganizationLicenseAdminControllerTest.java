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

import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseAttributeType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.outbound.RedisRepository;
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
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganizationLicenseAdminControllerTest {
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
	@DisplayName("GET In Use Organization License - 200 OK")
	void getInUseOrganizationLicense() throws Exception {
		// given
		String url = "/api/admin/organizations/1000000000/licenses/in-use";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST Synchronize Organization License- 200 OK")
	void syncOrganizationLicense() throws Exception {
		// given
		String url = "/api/admin/organizations/1000000000/licenses/1000000000/synchronize";

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
	@DisplayName("GET Organization License Responses - 200 OK")
	void getOrganizationLicenseResponses() throws Exception {
		// given
		String url = "/api/admin/organizations/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET Organization License 조회 - 쿼리스트링(productType)으로 조회 성공 200 OK")
	void getOrganizationLicensesByProductType() throws Exception {
		// given
		String url = "/api/admin/organizations/licenses?productType=SQUARS";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(5));
	}

	@Test
	@DisplayName("GET Organization License Responses With Search For Organization License Id - 200 OK")
	void getOrganizationLicenseResponsesWithIdSearch() throws Exception {
		// given
		String url = "/api/admin/organizations/licenses?organizationLicenseId=1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET Organization License Responses With Search For Contract Id - 200 OK")
	void getOrganizationLicenseResponsesWithContractIdSearch() throws Exception {
		// given
		String url = "/api/admin/organizations/licenses?contractId=1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET Organization License Responses With Search For Organization Id - 200 OK")
	void getOrganizationLicenseResponsesWithOrganizationIdSearch() throws Exception {
		// given
		String url = "/api/admin/organizations/licenses?organizationId=1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET Organization License Response - 200 OK")
	void getOrganizationLicenseResponse() throws Exception {
		// given
		String url = "/api/admin/organizations/licenses/1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET Organization License Response - 404 NotFound")
	void getOrganizationLicenseResponse_withNotfound() throws Exception {
		// given
		String url = "/api/admin/organizations/licenses/9999999999";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ORGANIZATION_LICENSE.name()));
	}

	@Test
	@DisplayName("GET Organization License Attribute 목록 조회 - 조회 성공 OK 200")
	void getOrganizationLicenseAttributes() throws Exception {
		// given
		String organizationLicenseId = "1000000000";
		String url = String.format("/api/admin/organizations/licenses/%s/attributes", organizationLicenseId);

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
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(8));
	}

	@Test
	@DisplayName("GET Organization License Additional Attribute 목록 조회 - 조회 성공 OK 200")
	void getOrganizationLicenseAdditionalAttributes() throws Exception {
		// given
		String organizationLicenseId = "1000000000";
		String url = String.format("/api/admin/organizations/licenses/%s/additional-attributes", organizationLicenseId);

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
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	@DisplayName("GET Organization License Revision 목록 조회 - 조회 성공 OK 200")
	void getOrganizationLicenseRevisions() throws Exception {
		// given
		String organizationLicenseId = "1000000000";
		String url = String.format("/api/admin/organizations/licenses/%s/revisions", organizationLicenseId);

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
	@DisplayName("GET Organization License Additional Attribute Revision 목록 조회 - 조회 성공 OK 200")
	void getOrganizationLicenseAdditionalAttributeRevisions() throws Exception {
		// given
		String organizationLicenseId = "1000000000";
		LicenseAdditionalAttributeType licenseAdditionalAttributeType = LicenseAdditionalAttributeType.MAXIMUM_VIEW;
		String url = String.format(
			"/api/admin/organization-licenses/%s/additional-attributes/revisions?licenseAdditionalAttributeType=%s",
			organizationLicenseId, licenseAdditionalAttributeType
		);

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
	@DisplayName("GET Organization License Attribute Revision 목록 조회 - 조회 성공 OK 200")
	void getOrganizationLicenseAttributeRevisions() throws Exception {
		// given
		String organizationLicenseId = "1000000000";
		LicenseAttributeType licenseAttributeType = LicenseAttributeType.MAXIMUM_GROUP;
		String url = String.format(
			"/api/admin/organization-licenses/%s/attributes/revisions?licenseAttributeType=%s",
			organizationLicenseId, licenseAttributeType.name()
		);

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
	@DisplayName("GET Organization License Track Sdk Usage 목록 조회 - 조회 성공 OK 200")
	void getTrackSdkUsageHistories() throws Exception {
		// given
		String url = "/api/admin/organization-licenses/track-sdk-usage-histories";

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
