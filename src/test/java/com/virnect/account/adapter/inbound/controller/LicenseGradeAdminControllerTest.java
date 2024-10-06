package com.virnect.account.adapter.inbound.controller;

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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseGradeRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {"classpath:data/admin_users.sql", "classpath:data/license_grades.sql"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LicenseGradeAdminControllerTest {
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
	@Sql(scripts = {
		"classpath:data/license_grades_truncate.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = {"classpath:data/license_grades.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("POST create - first created by professional 성공 200")
	void create() throws Exception {
		//given
		LicenseGradeRequestDto licenseGradeRequestDto = new LicenseGradeRequestDto();
		licenseGradeRequestDto.setName("Professional");
		licenseGradeRequestDto.setGradeType("PROFESSIONAL");
		licenseGradeRequestDto.setDescription("Professional Grade");

		// when
		String url = "/api/admin/licenses/grades";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(licenseGradeRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST create 400 bad request")
	void createTypeWithInvalidParameter() throws Exception {
		//given
		LicenseGradeRequestDto licenseGradeRequestDto = new LicenseGradeRequestDto();
		licenseGradeRequestDto.setName("Enterprise");
		licenseGradeRequestDto.setGradeType("ENTERPRISE");
		licenseGradeRequestDto.setDescription("Standard Grade".repeat(30));
		// when
		String url = "/api/admin/licenses/grades";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(licenseGradeRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andReturn();
	}

	@Test
	@DisplayName("POST create - 중복된 이름 400 BAD REQUEST")
	void createDuplicate() throws Exception {
		//given
		LicenseGradeRequestDto licenseGradeRequestDto = new LicenseGradeRequestDto();
		licenseGradeRequestDto.setName("Enterprise");
		licenseGradeRequestDto.setGradeType("ENTERPRISE");
		licenseGradeRequestDto.setDescription("Standard Grade".repeat(30));
		// when
		String url = "/api/admin/licenses/grades";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(licenseGradeRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andReturn();
	}

	@Test
	@DisplayName("PUT License Grade 수정 - 성공 OK 200")
	void updateGrade() throws Exception {
		//given
		String description = "license grade 설명 수정";
		LicenseGradeRequestDto requestDto = LicenseGradeRequestDto.of(
			"test Enterprise", LicenseGradeType.ENTERPRISE.name(), description);
		String url = "/api/admin/licenses/grades/1000000004";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT License Grade 수정 - License Grade를 찾을 수 없는 경우 NOT_FOUND_LICENSE_GRADE 404")
	void updateGradeByNonExistentId() throws Exception {
		//given
		String description = "license grade 설명 수정";
		LicenseGradeRequestDto requestDto = LicenseGradeRequestDto.of(
			"test Enterprise", LicenseGradeType.ENTERPRISE.name(), description);
		String url = "/api/admin/licenses/grades/99999999999";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_LICENSE_GRADE.name()));
	}

	@Test
	@DisplayName("PUT License Grade 수정 - 거절된 License Grade를 수정하는 경우 BAD_REQUEST 400")
	void updateGradeInInvalidStatus() throws Exception {
		//given
		String description = "license grade 설명 수정";
		LicenseGradeRequestDto requestDto = LicenseGradeRequestDto.of(
			"test Enterprise", LicenseGradeType.ENTERPRISE.name(), description);
		String url = "/api/admin/licenses/grades/1000000006";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("PUT License Grade 수정 - 승인된 License Grade를 수정하는 경우 BAD_REQUEST 400")
	void updateGradeInApprovedStatus() throws Exception {
		//given
		String description = "license grade 설명 수정";
		LicenseGradeRequestDto requestDto = LicenseGradeRequestDto.of(
			"test Enterprise", LicenseGradeType.ENTERPRISE.name(), description);
		String url = "/api/admin/licenses/grades/1000000001";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("PUT updateTypeByStatus")
	void updateTypeByStatus() throws Exception {
		//given
		String gradeId = "1000000005";
		ApprovalStatus status = ApprovalStatus.APPROVED;

		// when
		String url = String.format(
			"/api/admin/licenses/grades/%s/status/%s", gradeId, status.name());
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("PUT updateTypeByStatus")
	void updateTypeByStatusConflict() throws Exception {
		//given
		String gradeId = "1000000004";
		ApprovalStatus status = ApprovalStatus.APPROVED;

		// when
		String url = String.format(
			"/api/admin/licenses/grades/%s/status/%s", gradeId, status.name());
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(ErrorCode.DUPLICATE_LICENSE_GRADE_NAME.name()));
	}

	@Test
	@DisplayName("PUT updateTypeByStatus INVALID_STATUS")
	void updateTypeByStatus_INVALID_STATUS() throws Exception {
		//given
		String gradeId = "1000000000";
		ApprovalStatus status = ApprovalStatus.REVIEWING;

		// when
		String url = String.format(
			"/api/admin/licenses/grades/%s/status/%s", gradeId, status);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andReturn();
	}

	@Test
	@DisplayName("GET License Grade 목록 조회 - 조회 성공 OK 200")
	void getLicenseGrades() throws Exception {
		//given
		String url = "/api/admin/licenses/grades";

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
			.andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(7));
	}

	@Test
	@DisplayName("GET License Grade 목록 조회 - 상태 값으로 조회 성공 OK 200")
	void getLicenseGradesByStatus() throws Exception {
		//given
		ApprovalStatus approvalStatus = ApprovalStatus.APPROVED;
		String url = String.format("/api/admin/licenses/grades?status=%s", approvalStatus);

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
			.andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(4));
	}

	@Test
	@DisplayName("GET License Grade 목록 조회 - 등급 타입으로 조회 성공 OK 200")
	void getLicenseGradesByGradeType() throws Exception {
		//given
		LicenseGradeType gradeType = LicenseGradeType.STANDARD;
		String url = String.format("/api/admin/licenses/grades?gradeType=%s", gradeType);

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
			.andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(2));
	}

	@Test
	@DisplayName("GET License Grade 조회 - 성공 OK 200")
	void getLicenseGrade() throws Exception {
		//given
		Long licenseGradeId = 1000000000L;
		String url = String.format("/api/admin/licenses/grades/%s", licenseGradeId);

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
	@DisplayName("GET License Grade 조회 - License Grade를 찾을 수 없는 경우 NOT_FOUND_LICENSE_GRADE 404")
	void getLicenseGradeByNonExistentId() throws Exception {
		//given
		Long licenseGradeId = 9000000000L;
		String url = String.format("/api/admin/licenses/grades/%s", licenseGradeId);

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
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_LICENSE_GRADE.name()));
	}

	@Test
	@DisplayName("GET License Grade Revision 목록 조회")
	void getLicenseRevisions() throws Exception {
		// given
		String licenseGradeId = "1000000000";

		// when
		String url = String.format("/api/admin/licenses/grades/%s/revisions", licenseGradeId);
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
}
