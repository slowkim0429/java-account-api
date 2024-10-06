package com.virnect.account.adapter.inbound.controller;

import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.List;

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
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMultipartHttpServletRequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.multipart.MultipartFile;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.eventpopup.EventPopupExposeRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.EventServiceType;
import com.virnect.account.domain.enumclass.EventType;
import com.virnect.account.domain.enumclass.ExposureOptionType;
import com.virnect.account.domain.enumclass.FileDirectory;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.FileService;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/admin_users.sql",
	"classpath:data/event_popups.sql",
	"classpath:data/admin_users.sql",
	"classpath:data/coupons.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventPopupAdminControllerTest {

	private static final String BASE_URL = "/api/admin/event-popups";

	@Autowired
	public MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenProvider tokenProvider;

	@MockBean
	private FileService fileService;

	@Autowired
	private RedisRepository redisRepository;

	private TokenResponseDto adminTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeEach
	void fileServiceMockUp() {
		when(fileService.upload(any(MultipartFile.class), any(FileDirectory.class)))
			.thenReturn(("https://file.squars.io"));
		when(fileService.replaceFileUrl(anyString(), any(MultipartFile.class), any(FileDirectory.class)))
			.thenReturn("https://file.squars.io");
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
	@DisplayName("GET EventPopup 목록 조회 - 전체 조회 성공 200 OK")
	void retrieveTheEventPopupListAll() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(6));
	}

	@Test
	@DisplayName("GET EventPopup 목록 조회 - id eq 조회 성공 200 OK")
	void retrieveTheEventPopupListEqualsId() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL)
					.queryParam("id", "1000000000")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET EventPopup 목록 조회 - name startsWith 조회 성공 200 OK")
	void retrieveTheEventPopupListStartWithName() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL)
					.queryParam("name", "te")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(6));
	}

	@Test
	@DisplayName("GET EventPopup 목록 조회 - couponId eq 조회 성공 200 OK")
	void retrieveTheEventPopupListEqualsCouponId() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL)
					.queryParam("couponId", "1000000000")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("GET EventPopup 목록 조회 - eventType eq 조회 성공 200 OK")
	void retrieveTheEventPopupListEqualsEventType() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL)
					.queryParam("eventType", "MARKETING")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(2));
	}

	@Test
	@DisplayName("GET EventPopup 목록 조회 - serviceType eq 조회 성공 200 OK")
	void retrieveTheEventPopupListEqualsServiceType() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL)
					.queryParam("serviceType", "SQUARS")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(6));
	}

	@Test
	@DisplayName("GET EventPopup 목록 조회 - isExposed eq 조회 성공 200 OK")
	void retrieveTheEventPopupListEqualsExpose() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(BASE_URL)
					.queryParam("isExposed", "true")
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1));
	}

	@Test
	@DisplayName("POST EventPopup 등록 - SUBMISSION 200 OK")
	void createEventPopupForSubmission() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"image", "imagefile.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MockMultipartFile emailContentInlineImage = new MockMultipartFile(
			"emailContentInlineImage", "emailContentInlineImage.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.SUBMISSION.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("name", "create test");
		multiValueMap.add("exposureOptionType", ExposureOptionType.SELECTIVE_DEACTIVATION_DAY.name());
		multiValueMap.add("exposureOptionDataType", DataType.NUMBER.name());
		multiValueMap.add("exposureOptionValue", "3");
		multiValueMap.add("couponId", "1000000004");

		mockMvc.perform(
				MockMvcRequestBuilders
					.multipart(BASE_URL)
					.file(image)
					.file(emailContentInlineImage)
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST EventPopup 등록 - SUBMISSION 노출속성 UNUSE 200 OK")
	void createEventPopupForSubmissionButExposureOptionUnUse() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"image", "imagefile.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MockMultipartFile emailContentInlineImage = new MockMultipartFile(
			"emailContentInlineImage", "emailContentInlineImage.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.SUBMISSION.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("name", "create test");
		multiValueMap.add("exposureOptionType", ExposureOptionType.UNUSE.name());
		multiValueMap.add("couponId", "1000000004");

		mockMvc.perform(
				MockMvcRequestBuilders
					.multipart(BASE_URL)
					.file(image)
					.file(emailContentInlineImage)
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST EventPopup 등록 - SUBMISSION 노출속성 존재. 하지만 값이 없음 400 BAD_REQUEST")
	void createEventPopupForSubmissionButExposureOptionValueIsNull() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"image", "imagefile.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MockMultipartFile emailContentInlineImage = new MockMultipartFile(
			"emailContentInlineImage", "emailContentInlineImage.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.SUBMISSION.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("name", "create test");
		multiValueMap.add("exposureOptionType", ExposureOptionType.SELECTIVE_DEACTIVATION_DAY.name());
		multiValueMap.add("couponId", "1000000004");

		mockMvc.perform(
				MockMvcRequestBuilders
					.multipart(BASE_URL)
					.file(image)
					.file(emailContentInlineImage)
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST EventPopup 등록 - MARKETING 200 OK")
	void createEventPopupForMarketing() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"image", "imagefile.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.MARKETING.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("name", "create test");
		multiValueMap.add("exposureOptionType", ExposureOptionType.SELECTIVE_DEACTIVATION_DAY.name());
		multiValueMap.add("exposureOptionDataType", DataType.NUMBER.name());
		multiValueMap.add("exposureOptionValue", "3");

		mockMvc.perform(
				MockMvcRequestBuilders
					.multipart(BASE_URL)
					.file(image)
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST EventPopup 등록 - MARKETING 생성 요청. 하지만 쿠폰아이디가 있음 400 BAD_REQUEST")
	void createEventPopupForMarketingButCouponIdExist() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"image", "imagefile.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MockMultipartFile emailContentInlineImage = new MockMultipartFile(
			"emailContentInlineImage", "emailContentInlineImage.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.MARKETING.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("name", "create test");
		multiValueMap.add("exposureOptionType", ExposureOptionType.SELECTIVE_DEACTIVATION_DAY.name());
		multiValueMap.add("exposureOptionDataType", DataType.NUMBER.name());
		multiValueMap.add("exposureOptionValue", "3");
		multiValueMap.add("couponId", "1000000004");

		mockMvc.perform(
				MockMvcRequestBuilders
					.multipart(BASE_URL)
					.file(image)
					.file(emailContentInlineImage)
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST EventPopup 등록 - 너무 큰 파일 400 BAD_REQUEST")
	void createEventPopupButFileToBig() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"image", "image_sample.png", "image/jpg",
			new byte[30 * 1024 * 1024]
		);
		MockMultipartFile emailContentInlineImage = new MockMultipartFile(
			"emailContentInlineImage", "emailContentInlineImage.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.SUBMISSION.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("name", "create test");
		multiValueMap.add("exposureOptionType", ExposureOptionType.SELECTIVE_DEACTIVATION_DAY.name());
		multiValueMap.add("exposureOptionDataType", DataType.NUMBER.name());
		multiValueMap.add("exposureOptionValue", "3");
		multiValueMap.add("couponId", "1000000004");

		mockMvc.perform(
				MockMvcRequestBuilders
					.multipart(BASE_URL)
					.file(image)
					.file(emailContentInlineImage)
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST EventPopup 등록 - 허용되지 않은 확장자 400 BAD_REQUEST")
	void createEventPopupButNotAllowedExtension() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"image", "image_sample.bmp", "image/bmp",
			new byte[19 * 1024 * 1024]
		);
		MockMultipartFile emailContentInlineImage = new MockMultipartFile(
			"emailContentInlineImage", "emailContentInlineImage.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.SUBMISSION.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("name", "create test");
		multiValueMap.add("exposureOptionType", ExposureOptionType.SELECTIVE_DEACTIVATION_DAY.name());
		multiValueMap.add("exposureOptionDataType", DataType.NUMBER.name());
		multiValueMap.add("exposureOptionValue", "3");
		multiValueMap.add("couponId", "1000000004");

		mockMvc.perform(
				MockMvcRequestBuilders
					.multipart(BASE_URL)
					.file(image)
					.file(emailContentInlineImage)
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("GET EventPopup true 를 - true 로 변경 200 OK")
	void changeExposeEventPopupTrueToTrue() throws Exception {
		EventPopupExposeRequestDto eventPopupExposeRequestDto = new EventPopupExposeRequestDto(true);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_URL + "/1000000004/expose")
					.content(objectMapper.writeValueAsBytes(eventPopupExposeRequestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET EventPopup true 를 - false 로 변경 200 OK")
	void changeExposeEventPopupTrueToFalse() throws Exception {
		EventPopupExposeRequestDto eventPopupExposeRequestDto = new EventPopupExposeRequestDto(false);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_URL + "/1000000004/expose")
					.content(objectMapper.writeValueAsBytes(eventPopupExposeRequestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET EventPopup false 를 - false 로 변경 200 OK")
	void changeExposeEventPopupFalseToFalse() throws Exception {
		EventPopupExposeRequestDto eventPopupExposeRequestDto = new EventPopupExposeRequestDto(false);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_URL + "/1000000000/expose")
					.content(objectMapper.writeValueAsBytes(eventPopupExposeRequestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET EventPopup false 를 - true 로 변경 200 OK")
	void changeExposeEventPopupFalseToTrue() throws Exception {
		EventPopupExposeRequestDto eventPopupExposeRequestDto = new EventPopupExposeRequestDto(true);
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(BASE_URL + "/1000000000/expose")
					.content(objectMapper.writeValueAsBytes(eventPopupExposeRequestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT EventPopup 수정 - MARKETING 정상 수정 200 OK")
	void updateEventPopupForMarketing() throws Exception {
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.MARKETING.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("name", "update test");
		multiValueMap.add("imageLinkUrl", "https://squars.io/#");
		multiValueMap.add("buttonLabel", "test button");
		multiValueMap.add("buttonUrl", "https://squars.io/#");
		multiValueMap.add("exposureOptionType", ExposureOptionType.UNUSE.name());
		multiValueMap.add("contentDescription", "<h1>test</h1>");

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(
			String.format("%s/%s", BASE_URL, "1000000000"));
		builder.with(request -> {
			request.setMethod(HttpMethod.PUT.name());
			return request;
		});

		mockMvc.perform(
				builder
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT EventPopup 수정 - SUBMISSION 정상 수정 200 OK")
	void updateEventPopupForSubmission() throws Exception {
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("imageLinkUrl", "https://squars.io/#");
		multiValueMap.add("name", "update test");
		multiValueMap.add("buttonLabel", "button");
		multiValueMap.add("exposureOptionType", ExposureOptionType.UNUSE.name());
		multiValueMap.add("couponId", "1000000004");
		multiValueMap.add("contentDescription", "<h1>test</h1>");

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(
			String.format("%s/%s", BASE_URL, "1000000002"));
		builder.with(request -> {
			request.setMethod(HttpMethod.PUT.name());
			return request;
		});

		mockMvc.perform(
				builder
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT EventPopup 수정 - 이미지 교체 200 OK")
	void updateEventPopupForSubmissionAndChangeImage() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"image", "updateImage.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MockMultipartFile emailContentInlineImage = new MockMultipartFile(
			"emailContentInlineImage", "updateEmailContentInlineImage.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.SUBMISSION.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("imageLinkUrl", "https://squars.io/#");
		multiValueMap.add("name", "update test");
		multiValueMap.add("buttonLabel", "button");
		multiValueMap.add("exposureOptionType", ExposureOptionType.UNUSE.name());
		multiValueMap.add("couponId", "1000000004");
		multiValueMap.add("contentDescription", "<h1>test</h1>");

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(
			String.format("%s/%s", BASE_URL, "1000000002"));
		builder.with(request -> {
			request.setMethod(HttpMethod.PUT.name());
			return request;
		});

		mockMvc.perform(
				builder
					.file(image)
					.file(emailContentInlineImage)
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT EventPopup 수정 - 너무 큰 파일 400 BAD_REQUEST")
	void updateEventPopupForSubmissionAndChangeImageButFileToBig() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"image", "image_sample.png", "image/jpg",
			new byte[30 * 1024 * 1024]
		);
		MockMultipartFile emailContentInlineImage = new MockMultipartFile(
			"emailContentInlineImage", "emailContentInlineImage.jpg", "image/jpg",
			new byte[30 * 1024 * 1024]
		);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.SUBMISSION.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("imageLinkUrl", "https://squars.io/#");
		multiValueMap.add("name", "update test");
		multiValueMap.add("buttonLabel", "button");
		multiValueMap.add("buttonUrl", "https://squars.io/#");
		multiValueMap.add("exposureOptionType", ExposureOptionType.UNUSE.name());
		multiValueMap.add("couponId", "1000000004");
		multiValueMap.add("contentDescription", "<h1>test</h1>");

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(
			String.format("%s/%s", BASE_URL, "1000000002"));
		builder.with(request -> {
			request.setMethod(HttpMethod.PUT.name());
			return request;
		});

		mockMvc.perform(
				builder
					.file(image)
					.file(emailContentInlineImage)
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT EventPopup 수정 - 허용되지 않은 확장자 400 BAD_REQUEST")
	void updateEventPopupForSubmissionAndChangeImageButNotAllowedExtension() throws Exception {
		MockMultipartFile image = new MockMultipartFile(
			"image", "image_sample.bmp", "image/bmp",
			new byte[19 * 1024 * 1024]
		);
		MockMultipartFile emailContentInlineImage = new MockMultipartFile(
			"emailContentInlineImage", "emailContentInlineImage.jpg", "image/jpg",
			new byte[19 * 1024 * 1024]
		);
		MultiValueMap<String, String> multiValueMap = new LinkedMultiValueMap<>();
		multiValueMap.add("eventType", EventType.SUBMISSION.name());
		multiValueMap.add("serviceType", EventServiceType.SQUARS.name());
		multiValueMap.add("imageLinkUrl", "https://squars.io/#");
		multiValueMap.add("name", "update test");
		multiValueMap.add("buttonLabel", "button");
		multiValueMap.add("buttonUrl", "https://squars.io/#");
		multiValueMap.add("exposureOptionType", ExposureOptionType.UNUSE.name());
		multiValueMap.add("couponId", "1000000004");
		multiValueMap.add("contentDescription", "<h1>test</h1>");

		MockMultipartHttpServletRequestBuilder builder = MockMvcRequestBuilders.multipart(
			String.format("%s/%s", BASE_URL, "1000000002"));
		builder.with(request -> {
			request.setMethod(HttpMethod.PUT.name());
			return request;
		});

		mockMvc.perform(
				builder
					.file(image)
					.file(emailContentInlineImage)
					.params(multiValueMap)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			).andDo(print())
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("GET EventPopup 단일 조회 - 조회 성공 200 OK")
	void getEventPopup() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(String.format("%s/%s", BASE_URL, "1000000000"))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET EventPopup 단일 조회 - 존재 하지 않음 404 NOT_FOUND")
	void getEventPopupButNotFound() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(String.format("%s/%s", BASE_URL, "9999999999"))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_EVENT_POPUP.name()));
	}

	@Test
	@DisplayName("GET EventPopup revision 목록 조회 - 200 OK")
	void retrieveTheEventPopupRevisionList() throws Exception {

		mockMvc.perform(
				MockMvcRequestBuilders
					.get(String.format("%s/%s/revisions", BASE_URL, "1000000000"))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}
}
