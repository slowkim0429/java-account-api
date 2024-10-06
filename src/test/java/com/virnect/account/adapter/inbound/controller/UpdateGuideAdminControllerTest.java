package com.virnect.account.adapter.inbound.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideExposeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.updateguide.UpdateGuideRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.FileType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.ServiceType;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.FileRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/update_guides.sql",
	"classpath:data/admin_users.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class UpdateGuideAdminControllerTest {

	@Autowired
	public MockMvc mockMvc;

	@Autowired
	public ObjectMapper objectMapper;

	@MockBean
	FileRepository fileRepository;

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
	@DisplayName("GET update guide 목록 조회 - 성공 OK 200")
	void getUpdateGuideListOk() throws Exception {
		// given
		String url = "/api/admin/update-guides";

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
	@DisplayName("GET update guide 목록 조회 - id 조회 성공 OK 200")
	void getUpdateGuideListOkSearchId() throws Exception {
		// given
		String url = "/api/admin/update-guides?id=1000000000";

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
	@DisplayName("GET update guide 목록 조회 - isExposed 조회 성공 OK 200")
	void getUpdateGuideListOkSearchIsExposed() throws Exception {
		// given
		String url = "/api/admin/update-guides?isExposed=false";

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
	@DisplayName("GET update guide 목록 조회 - name 조회 성공 OK 200")
	void getUpdateGuideListOkSearchName() throws Exception {
		// given
		String url = "/api/admin/update-guides?name=name4";

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
	@DisplayName("GET update guide 목록 조회 - 한번에 조회 성공 OK 200")
	void getUpdateGuideListOkSearchAll() throws Exception {
		// given
		String url = "/api/admin/update-guides?name=name1&isExposed=true&id=1000000000";

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
	@DisplayName("GET update guide 상세 조회 - 조회 성공 OK 200")
	void getUpdateGuideById() throws Exception {
		// given
		String url = "/api/admin/update-guides/1000000000";

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
	@DisplayName("GET update guide 상세 조회 - 조회 실패 NOT FOUND 404")
	void getUpdateGuideByIdByButNotFound() throws Exception {
		// given
		String url = "/api/admin/update-guides/9999999999";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isNotFound());
	}

	@Test
	@DisplayName("POST 이미지 업로드 - ok 200")
	void uploadImage() throws Exception {
		//given
		byte[] maximumSize = new byte[20 * 1024 * 1024];
		MockMultipartFile file = new MockMultipartFile(
			"file", "imagefile.jpg", "image/jpg", maximumSize);

		// when
		String url = "/api/admin/update-guides/media";

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(url);
		builder.with(request -> {
			request.setMethod("POST");
			request.addHeader(
				HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto));
			return request;
		});

		mockMvc.perform(builder
				.file(file))
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 문서 업로드 - 실패 BAD_REQUEST 400")
	void uploadText() throws Exception {
		//given
		byte[] maximumSize = new byte[20 * 1024 * 1024];
		MockMultipartFile file = new MockMultipartFile(
			"file", "plain.txt", "text/plain", maximumSize);

		// when
		String url = "/api/admin/update-guides/media";

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(url);
		builder.with(request -> {
			request.setMethod("POST");
			request.addHeader(
				HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto));
			return request;
		});

		mockMvc.perform(builder
				.file(file))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_MEDIA.name()));
	}

	@Test
	@DisplayName("POST 너무 큰 사진 업로드 - 실패 BAD_REQUEST 400")
	void uploadBigSize() throws Exception {
		//given
		byte[] bigSize = new byte[21 * 1024 * 1024];
		MockMultipartFile file = new MockMultipartFile(
			"file", "imagefile.jpg", "image/jpg", bigSize);

		// when
		String url = "/api/admin/update-guides/media";

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(url);
		builder.with(request -> {
			request.setMethod("POST");
			request.addHeader(
				HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto));
			return request;
		});

		mockMvc.perform(builder
				.file(file))
			.andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_MEDIA_SIZE.name()));
	}

	@Test
	@DisplayName("POST update guide 생성 - 성공 OK 200")
	void create() throws Exception {
		// given
		String url = "/api/admin/update-guides";
		UpdateGuideRequestDto updateGuideRequestDto = UpdateGuideRequestDto.builder()
			.name("test name")
			.description("description")
			.title("title")
			.subTitle("sub title")
			.subDescription("sub description")
			.dateByUpdate(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
			.serviceType(ServiceType.WORKSPACE.name())
			.fileType(FileType.IMAGE.name())
			.fileUrl("https://www.squars.io")
			.build();

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.content(objectMapper.writeValueAsString(updateGuideRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST update guide 생성 - 실패 BAD_REQUEST 200")
	void createButDateOfUpdateIsNotDateType() throws Exception {
		// given
		String url = "/api/admin/update-guides";
		UpdateGuideRequestDto updateGuideRequestDto = UpdateGuideRequestDto.builder()
			.name("test name")
			.description("description")
			.title("title")
			.subTitle("sub title")
			.subDescription("sub description")
			.dateByUpdate("2023-01--01")
			.serviceType(ServiceType.WORKSPACE.name())
			.fileType(FileType.IMAGE.name())
			.fileUrl("https://www.squars.io")
			.build();

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.content(objectMapper.writeValueAsString(updateGuideRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT update guide 수정 - 성공 OK 200")
	void update() throws Exception {
		// given
		String url = String.join("/", "/api/admin/update-guides", "1000000000");
		UpdateGuideRequestDto updateGuideRequestDto = UpdateGuideRequestDto.builder()
			.name("test name")
			.description("description")
			.title("title")
			.subTitle("sub title")
			.subDescription("sub description")
			.dateByUpdate(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
			.serviceType(ServiceType.WORKSPACE.name())
			.fileType(FileType.IMAGE.name())
			.fileUrl("https://www.squars.io")
			.build();

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.content(objectMapper.writeValueAsString(updateGuideRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT 존재하지 않는 update guide 수정 - 실패 NOT_FOUND 404")
	void updateButNotFound() throws Exception {
		// given
		String url = String.join("/", "/api/admin/update-guides", "99999999");
		UpdateGuideRequestDto updateGuideRequestDto = UpdateGuideRequestDto.builder()
			.name("test name")
			.description("description")
			.title("title")
			.subTitle("sub title")
			.subDescription("sub description")
			.dateByUpdate(LocalDate.now().format(DateTimeFormatter.ISO_DATE))
			.serviceType(ServiceType.WORKSPACE.name())
			.fileType(FileType.IMAGE.name())
			.fileUrl("https://www.squars.io")
			.build();

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.content(objectMapper.writeValueAsString(updateGuideRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_UPDATE_GUIDE.name()));
	}

	@Test
	@DisplayName("PUT update guide 노출 수정 - 성공 OK 200")
	void updateExposureStatus() throws Exception {
		// given
		String url = String.join("/", "/api/admin/update-guides", "1000000000", "expose");
		UpdateGuideExposeRequestDto updateGuideExposeRequestDto = new UpdateGuideExposeRequestDto(true);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.content(objectMapper.writeValueAsString(updateGuideExposeRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT 존재하지 않는 update guide 노출 수정 - 실패 NOT_FOUND 404")
	void updateExposureStatusButNotFound() throws Exception {
		// given
		String url = String.join("/", "/api/admin/update-guides", "99999999", "expose");
		UpdateGuideExposeRequestDto updateGuideExposeRequestDto = new UpdateGuideExposeRequestDto(true);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.content(objectMapper.writeValueAsString(updateGuideExposeRequestDto))
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_UPDATE_GUIDE.name()));
	}
}