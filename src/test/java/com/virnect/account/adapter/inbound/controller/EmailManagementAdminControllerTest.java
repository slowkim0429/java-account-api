package com.virnect.account.adapter.inbound.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

import org.apache.http.HttpHeaders;
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
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.emailmanagement.EmailManagementUseStatusUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.Mail;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.FileRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/email_customizing_managements.sql",
	"classpath:data/admin_users.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EmailManagementAdminControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private RedisRepository redisRepository;

	@MockBean
	private FileRepository fileRepository;

	private TokenResponseDto adminTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeAll
	void setUp() {
		userSetup();
	}

	void userSetup() {
		Long adminUserId = 1000000001L;
		String email = "admin-user2@virnect.com";
		adminTokenResponseDto = tokenProvider.createAdminToken(
			adminUserId, email, null, List.of(Role.ROLE_ADMIN_MASTER, Role.ROLE_ADMIN_USER));

		redisRepository.setAdminRefreshToken(
			adminUserId.toString(), adminTokenResponseDto.getRefreshToken(),
			adminTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteAdminRefreshToken(
			tokenProvider.getUserNameFromJwtToken(adminTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("GET Email Management - 200 OK")
	void getEmailManagements() throws Exception {
		String url = "/api/admin/email-managements";

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(2));
	}

	@Test
	@DisplayName("Post Email Management - 200 OK")
	void createEmailManagement() throws Exception {
		when(fileRepository.upload(anyString(), any(MultipartFile.class)))
			.thenReturn(("https://file.squars.io/service/email-customizing-media/test.jpg"));

		String url = "/api/admin/email-managements";

		MockMultipartFile file = new MockMultipartFile(
			"contentsInlineImage", "file.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("emailType", Mail.WELCOME.name());

		mockMvc.perform(
				MockMvcRequestBuilders
					.multipart(url)
					.file(file)
					.params(params)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("Post Email Management - 400 BadRequest - 필수 입력 값 누락")
	void createEmailManagement_withBadRequest() throws Exception {
		String url = "/api/admin/email-managements";

		MockMultipartFile file = new MockMultipartFile(
			"contentsInlineImage", "file.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);

		mockMvc.perform(
				MockMvcRequestBuilders
					.multipart(url)
					.file(file)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Update UseStatus Email Management")
	void updateUseStatusOfEmailManagement() throws Exception {
		String url = String.format("/api/admin/email-managements/%s/use-status", "1000000000");

		EmailManagementUseStatusUpdateRequestDto emailManagementUseStatusUpdateRequestDto = new EmailManagementUseStatusUpdateRequestDto();
		emailManagementUseStatusUpdateRequestDto.setUseStatus(UseStatus.UNUSE);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(emailManagementUseStatusUpdateRequestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Update UseStatus Email Management - 404 NotFound")
	void updateUseStatusOfEmailManagement_withNotFound() throws Exception {
		String url = String.format("/api/admin/email-managements/%s/use-status", "99999999999");

		EmailManagementUseStatusUpdateRequestDto emailManagementUseStatusUpdateRequestDto = new EmailManagementUseStatusUpdateRequestDto();
		emailManagementUseStatusUpdateRequestDto.setUseStatus(UseStatus.UNUSE);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(emailManagementUseStatusUpdateRequestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_EMAIL_CUSTOMIZING_MANAGEMENT.name()));
	}

	@Test
	@DisplayName("GET Email Management revisions - 200 OK")
	void getEmailManagementRevision() throws Exception {
		String url = "/api/admin/email-managements/1000000000/revisions";

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Update Email Management - 200 OK")
	void updateEmailManagement() throws Exception {
		String url = String.format("/api/admin/email-managements/%s", "1000000000");

		EmailManagementUpdateRequestDto emailManagementUpdateRequestDto = new EmailManagementUpdateRequestDto();
		emailManagementUpdateRequestDto.setEmailType(null);
		emailManagementUpdateRequestDto.setContentsInlineImage(null);
		emailManagementUpdateRequestDto.setDescription("설명만 업데이트");

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(emailManagementUpdateRequestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Update Email Management - 404 NotFound")
	void updateEmailManagement_withNotFound() throws Exception {
		String url = String.format("/api/admin/email-managements/%s", "99999999999");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("description", "설명");
		params.add("emailType", null);
		params.add("contentsInlineImage", null);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
					.params(params)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_EMAIL_CUSTOMIZING_MANAGEMENT.name()));
	}

	@Test
	@DisplayName("PUT Update Email Management - 400 BadRequest")
	void updateEmailManagement_withBadRequest() throws Exception {
		String url = String.format("/api/admin/email-managements/%s", "1000000001");

		MultiValueMap<String, String> params = new LinkedMultiValueMap<>();
		params.add("description", "설명");
		params.add("emailType", null);
		params.add("contentsInlineImage", null);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.MULTIPART_FORM_DATA_VALUE)
					.params(params)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("GET Email Management - 200 OK")
	void getEmailManagement() throws Exception {
		String url = "/api/admin/email-managements/1000000000";

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET Email Management - 404 NotFound")
	void getEmailManagement_withNotFund() throws Exception {
		String url = "/api/admin/email-managements/9990000000";

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_EMAIL_CUSTOMIZING_MANAGEMENT.name()));
	}
}
