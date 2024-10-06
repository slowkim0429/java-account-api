package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.domain.enumclass.DataType.*;
import static com.virnect.account.domain.enumclass.LicenseAttributeType.*;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.license.LicenseAdditionalAttributeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseAttributeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.license.LicenseRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.LicenseAdditionalAttributeType;
import com.virnect.account.domain.enumclass.LicenseGradeType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/admin_users.sql",
	"classpath:data/licenses.sql",
	"classpath:data/license_grades.sql",
	"classpath:data/products.sql",
	"classpath:data/license_attributes.sql",
	"classpath:data/users.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class LicenseAdminControllerTest {
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
	@DisplayName("POST License 등록")
	void create() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setLicenseGradeId(1000000001L);
		licenseRequestDto.setName("Free plus License");
		licenseRequestDto.setDescription("Free plus License 입니다.");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_PUBLISHING_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(EXCLUSION_WATERMARK, DataType.BOOL, "false"),
			new LicenseAttributeRequestDto(MAXIMUM_VIEW_PER_MONTH, DataType.NUMBER, "1000"),
			new LicenseAttributeRequestDto(CUSTOMIZING_SPLASH_SCREEN, DataType.BOOL, "false")
		));

		licenseRequestDto.setLicenseAdditionalAttributes(List.of(
			new LicenseAdditionalAttributeRequestDto(
				LicenseAdditionalAttributeType.MAXIMUM_VIEW, DataType.NUMBER, "1000")
		));

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("POST Free plus License 등록")
	void createFreePlusLicense() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setLicenseGradeId(1000000000L);
		licenseRequestDto.setName("Free plus License");
		licenseRequestDto.setDescription("Free plus License 입니다.");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_PUBLISHING_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(EXCLUSION_WATERMARK, DataType.BOOL, "false"),
			new LicenseAttributeRequestDto(MAXIMUM_VIEW_PER_MONTH, DataType.NUMBER, "1000"),
			new LicenseAttributeRequestDto(CUSTOMIZING_SPLASH_SCREEN, DataType.BOOL, "false")
		));

		licenseRequestDto.setLicenseAdditionalAttributes(List.of(
			new LicenseAdditionalAttributeRequestDto(
				LicenseAdditionalAttributeType.MAXIMUM_VIEW, DataType.NUMBER, "1000")
		));

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("POST 라이센스 등록 - Track 제품으로 라이센스 등록 성공 OK 200")
	void createTrackLicense() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000001L);
		licenseRequestDto.setLicenseGradeId(1000000000L);
		licenseRequestDto.setName("Free plus License");
		licenseRequestDto.setDescription("Free plus License 입니다.");
		licenseRequestDto.setSalesTarget("For starter");

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 라이센스 등록 - 무료 등급이 아닌 라이센스 등급으로 Track 라이센스 생성하는 경우 BAD_REQUEST 400")
	void createTrackLicenseByNoFreePlusLicenseGrade() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000001L);
		licenseRequestDto.setLicenseGradeId(1000000003L);
		licenseRequestDto.setName("License Name");
		licenseRequestDto.setDescription("License Description");
		licenseRequestDto.setSalesTarget("For XXX");

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 등록 - Track 제품이지만 속성을 입력한 경우 BAD_REQUEST 400")
	void createTrackLicenseWithAttributes() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setLicenseGradeId(1000000000L);
		licenseRequestDto.setName("License Name");
		licenseRequestDto.setDescription("License Description");
		licenseRequestDto.setSalesTarget("For XXX");
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_PUBLISHING_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(EXCLUSION_WATERMARK, BOOL, "false"),
			new LicenseAttributeRequestDto(MAXIMUM_VIEW_PER_MONTH, DataType.NUMBER, "1000")
		));

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 등록 - Track 제품이지만 부가 속성을 입력한 경우 BAD_REQUEST 400")
	void createTrackLicenseWithAdditionalAttributes() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000001L);
		licenseRequestDto.setLicenseGradeId(1000000000L);
		licenseRequestDto.setName("License Name");
		licenseRequestDto.setDescription("License Description");
		licenseRequestDto.setSalesTarget("For XXX");
		licenseRequestDto.setLicenseAdditionalAttributes(List.of(
			new LicenseAdditionalAttributeRequestDto(
				LicenseAdditionalAttributeType.MAXIMUM_VIEW, DataType.NUMBER, "1000")
		));

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST License 등록 (부가 속성 포함)")
	void createWithAdditionalAttribute() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setLicenseGradeId(1000000001L);
		licenseRequestDto.setName("Squars Standard License");
		licenseRequestDto.setDescription("Squars Standard License 입니다.");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_PUBLISHING_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(EXCLUSION_WATERMARK, DataType.BOOL, "false"),
			new LicenseAttributeRequestDto(MAXIMUM_VIEW_PER_MONTH, DataType.NUMBER, "1000"),
			new LicenseAttributeRequestDto(CUSTOMIZING_SPLASH_SCREEN, DataType.BOOL, "false")
		));
		licenseRequestDto.setLicenseAdditionalAttributes(List.of(
			new LicenseAdditionalAttributeRequestDto(
				LicenseAdditionalAttributeType.MAXIMUM_VIEW, DataType.NUMBER, "1000")
		));

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("POST License 등록 - 400 에러 - 필수 옵션을 등록하지 않은 경우")
	void create_isBadRequest_withInvalidAttributes() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setLicenseGradeId(1000000001L);
		licenseRequestDto.setName("Standard License");
		licenseRequestDto.setDescription("Standard License 입니다.");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1")
		));

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()))
			.andReturn();
	}

	@Test
	@DisplayName("POST License 등록 - 400 에러 - 라이선스의 속성 값이 충돌하는 경우")
	void create_isBadRequest_withDuplicateMeaningAttribute() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setLicenseGradeId(1000000001L);
		licenseRequestDto.setName("Standard License");
		licenseRequestDto.setDescription("Standard License 입니다.");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_PROJECT, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_PUBLISHING_PROJECT, DataType.NUMBER, "10")
		));

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()))
			.andReturn();
	}

	@Test
	@DisplayName("POST License 등록 - 400 에러 - 라이선스의 속성이 중복되는 경우")
	void create_isBadRequest_withDuplicateAttribute() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setLicenseGradeId(1000000001L);
		licenseRequestDto.setName("Standard License");
		licenseRequestDto.setDescription("Standard License 입니다.");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "10")
		));

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()))
			.andReturn();
	}

	@Test
	@DisplayName("POST License 등록 - 400 에러 - 스쿼스 제품이 아닌데 스쿼스 옵션을 선택한 경우")
	void create_isBadRequest_withInvalidRequiredAttribute() throws Exception {
		// given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setLicenseGradeId(1000000001L);
		licenseRequestDto.setName("Standard License");
		licenseRequestDto.setDescription("Standard License 입니다.");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_PUBLISHING_PROJECT, DataType.NUMBER, "1")
		));

		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()))
			.andReturn();
	}

	@Test
	@DisplayName("PUT license update - 수정 성공 OK 200")
	void update() throws Exception {
		//given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setLicenseGradeId(1000000001L);
		licenseRequestDto.setName("Free plus License");
		licenseRequestDto.setDescription("Free plus License 입니다.");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_PUBLISHING_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(EXCLUSION_WATERMARK, DataType.BOOL, "false"),
			new LicenseAttributeRequestDto(MAXIMUM_VIEW_PER_MONTH, DataType.NUMBER, "1000"),
			new LicenseAttributeRequestDto(CUSTOMIZING_SPLASH_SCREEN, DataType.BOOL, "false")
		));

		licenseRequestDto.setLicenseAdditionalAttributes(List.of(
			new LicenseAdditionalAttributeRequestDto(
				LicenseAdditionalAttributeType.MAXIMUM_VIEW, DataType.NUMBER, "1000")
		));

		String licenseId = "1000000006";

		// when
		String url = String.format("/api/admin/licenses/%s", licenseId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT license update - 수정 성공 OK 200")
	void updateAFreePlusLicense() throws Exception {
		//given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setLicenseGradeId(1000000001L);
		licenseRequestDto.setName("Free plus License");
		licenseRequestDto.setDescription("Free plus License 입니다.");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_PUBLISHING_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(EXCLUSION_WATERMARK, DataType.BOOL, "false"),
			new LicenseAttributeRequestDto(MAXIMUM_VIEW_PER_MONTH, DataType.NUMBER, "1000"),
			new LicenseAttributeRequestDto(CUSTOMIZING_SPLASH_SCREEN, DataType.BOOL, "true")
		));

		licenseRequestDto.setLicenseAdditionalAttributes(List.of(
			new LicenseAdditionalAttributeRequestDto(
				LicenseAdditionalAttributeType.MAXIMUM_VIEW, DataType.NUMBER, "1000")
		));

		String licenseId = "1000000006";

		// when
		String url = String.format("/api/admin/licenses/%s", licenseId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT license update - license를 찾을수 없는경우 404")
	void updateNotFoundLicense() throws Exception {
		//given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setName("workspace license update");
		licenseRequestDto.setDescription("this is workspace license update");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseGradeId(1000000001L);
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, NUMBER, "5"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, NUMBER, "100"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, NUMBER, "1000")
		));

		String licenseId = "9000000002";

		// when
		String url = String.format("/api/admin/licenses/%s", licenseId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_LICENSE.name()));
	}

	@Test
	@DisplayName("PUT license update - license status register가 아닐 경우 400")
	void updateLicenseStatusInvalid() throws Exception {
		//given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setName("workspace license update");
		licenseRequestDto.setDescription("this is workspace license update");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseGradeId(1000000000L);
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, NUMBER, "5"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, NUMBER, "100"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, NUMBER, "1000")
		));

		String licenseId = "1000000000";

		// when
		String url = String.format("/api/admin/licenses/%s", licenseId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("PUT license update - license useStatus가 delete일 경우 404")
	void updateLicenseUseStatusDelete() throws Exception {
		//given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setName("workspace license update");
		licenseRequestDto.setDescription("this is workspace license update");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseGradeId(1000000001L);
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, NUMBER, "5"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, NUMBER, "100"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, NUMBER, "1000")
		));

		String licenseId = "1000000002";

		// when
		String url = String.format("/api/admin/licenses/%s", licenseId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_LICENSE.name()));
	}

	@Test
	@DisplayName("PUT license update - license grade가 APPROVED 상태가 아닐경우 400")
	void updateLicenseGradeNotApprove() throws Exception {
		//given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000000L);
		licenseRequestDto.setName("workspace license update");
		licenseRequestDto.setDescription("this is workspace license update");
		licenseRequestDto.setSalesTarget("For starter");
		licenseRequestDto.setLicenseGradeId(1000000004L);
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, DataType.NUMBER, "1"),
			new LicenseAttributeRequestDto(MAXIMUM_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_PUBLISHING_PROJECT, DataType.NUMBER, "10"),
			new LicenseAttributeRequestDto(EXCLUSION_WATERMARK, DataType.BOOL, "false"),
			new LicenseAttributeRequestDto(MAXIMUM_VIEW_PER_MONTH, DataType.NUMBER, "1000"),
			new LicenseAttributeRequestDto(CUSTOMIZING_SPLASH_SCREEN, DataType.BOOL, "false")
		));

		licenseRequestDto.setLicenseAdditionalAttributes(List.of(
			new LicenseAdditionalAttributeRequestDto(
				LicenseAdditionalAttributeType.MAXIMUM_VIEW, DataType.NUMBER, "1000")
		));

		String licenseId = "1000000010";

		// when
		String url = String.format("/api/admin/licenses/%s", licenseId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("PUT license update - product Type 이 SQUARS가 아닌경우에 SQUARS 옵션선택할 경우 400")
	void updateProductTypeSquarsNotSquarsAttribute() throws Exception {
		//given
		LicenseRequestDto licenseRequestDto = new LicenseRequestDto();
		licenseRequestDto.setProductId(1000000001L);
		licenseRequestDto.setName("workspace license update");
		licenseRequestDto.setDescription("this is workspace license update");
		licenseRequestDto.setLicenseGradeId(10000000000L);
		licenseRequestDto.setLicenseAttributes(List.of(
			new LicenseAttributeRequestDto(MAXIMUM_WORKSPACE, NUMBER, "10"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP, NUMBER, "5"),
			new LicenseAttributeRequestDto(MAXIMUM_GROUP_USER, NUMBER, "100"),
			new LicenseAttributeRequestDto(STORAGE_SIZE_PER_MB, NUMBER, "1000"),
			new LicenseAttributeRequestDto(MAXIMUM_PROJECT, NUMBER, "3")
		));

		String licenseId = "100000000";

		// when
		String url = String.format("/api/admin/licenses/%s", licenseId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(licenseRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("GET License 상세 조회 - 조회 성공 OK 200")
	void getLicenseById() throws Exception {
		// given
		String url = "/api/admin/licenses/1000000005";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET License 상세 조회 - 존재하지 않는 License일 경우 NOT_FOUND 404")
	void getLicenseByNonExistentIdId() throws Exception {
		// given
		String url = "/api/admin/licenses/99999999999";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_LICENSE.name()));
	}

	@Test
	@DisplayName("GET License 상세 조회 - 이미 삭제된 license일 경우 NOT_FOUND 404")
	void getAlreadyDeletedLicense() throws Exception {
		// given
		String url = "/api/admin/licenses/1000000002";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_LICENSE.name()));
	}

	@Test
	@DisplayName("GET License 목록 조회 - 조회 성공 OK 200")
	void getLicenses() throws Exception {
		// given
		String url = "/api/admin/licenses";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET License 목록 조회 - status가 APPROVED인 License만 조회 OK 200")
	void getApprovedLicenses() throws Exception {
		// given
		String url = "/api/admin/licenses?status=APPROVED";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.contents").isNotEmpty());
	}

	@Test
	@DisplayName("GET License 목록 조회 - license Grade Id가 1000000002인 License만 조회 OK 200")
	void getLicenseWithId1000000002() throws Exception {
		// given
		String url = "/api/admin/licenses?licenseGradeId=1000000002";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.contents").isNotEmpty())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(3));
	}

	@Test
	@DisplayName("GET License 목록 조회 - licenseName 조회 OK 200")
	void getLicenseWithLicenseName() throws Exception {
		// given
		String url = "/api/admin/licenses?licenseName=squars";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.contents[0].name").value("squars free plus"))
			.andExpect(jsonPath("$.pageMeta.totalElements").value(7));
	}

	@Test
	@DisplayName("PUT License 상태 변경 - 성공 OK 200")
	void licenseStatusUpdate() throws Exception {
		//given
		String licenseId = "1000000006";
		String status = "APPROVED";

		// when
		String url = String.format("/api/admin/licenses/%s/status/%s", licenseId, status);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT License 상태 변경 - license use status가 delete일 경우 404")
	void licenseUseStatusDelte() throws Exception {
		//given
		String licenseId = "1000000002";
		String status = "APPROVED";

		// when
		String url = String.format("/api/admin/licenses/%s/status/%s", licenseId, status);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_LICENSE.name()));
	}

	@Test
	@DisplayName("PUT License 상태 변경 - license 상태가 REGISTER가 아닐경우 400")
	void licenseStatusUpdateNotRegister() throws Exception {
		//given
		String licenseId = "1000000001";
		String status = "APPROVED";

		// when
		String url = String.format("/api/admin/licenses/%s/status/%s", licenseId, status);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("PUT License 상태 변경 - 변경 status가 approve 또는 reject가 아닐경우 400")
	void licenseApproveStatusNotApproveOrReject() throws Exception {
		//given
		String licenseId = "1000000003";
		String status = "REGISTER";

		// when
		String url = String.format("/api/admin/licenses/%s/status/%s", licenseId, status);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("GET License Revision 목록 조회")
	void getLicenseRevisions() throws Exception {
		String licenseId = "1000000000";
		// when
		String url = String.format("/api/admin/licenses/%s/revisions", licenseId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET List - license grade type 검색 성공 200")
	void getLicenseGradesByLicenseGradeType() throws Exception {
		//given
		LicenseGradeType licenseGradeType = LicenseGradeType.ENTERPRISE;
		// when
		String url = String.format(
			"/api/admin/licenses?licenseGradeType=%s", licenseGradeType.name());
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(5))
			.andReturn();
	}
}
