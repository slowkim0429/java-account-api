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
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/users.sql",
	"classpath:data/licenses.sql",
	"classpath:data/license_grades.sql",
	"classpath:data/products.sql",
	"classpath:data/license_attributes.sql",
	"classpath:data/items.sql",
	"classpath:data/item_payment_links.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemControllerTest {
	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private RedisRepository redisRepository;

	private TokenResponseDto organizationOwnerTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeAll
	void setUp() {
		organizationOwnerSetup();
	}

	void organizationOwnerSetup() {
		Long userId = 1000000001L;
		Long organizationId = 1000000000L;
		String email = "organiowner@virnect.com";
		organizationOwnerTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, null, email, null, List.of());

		redisRepository.setObjectValue(
			userId.toString(), organizationOwnerTokenResponseDto.getRefreshToken(),
			organizationOwnerTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(organizationOwnerTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("GET item 상세 조회 - 조회 성공 200 OK")
	void getItemAndLicenseAttributesByItemId() throws Exception {
		// given
		String url = "/api/items/1000000009";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.name").value("Enterpriese Squars License"));
	}

	@Test
	@DisplayName("GET item 상세 조회 - 부가 서비스 아이템일 경우 부가 서비스에 대한 데이터도 함께 조회 성공 200 OK")
	void getItemAndLicenseAttributesWithAdditionalItemDataByItemId() throws Exception {
		// given
		String url = "/api/items/1000000007";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.additionalItems").isArray())
			.andExpect(jsonPath("$.additionalItems").isNotEmpty());
	}

	@Test
	@DisplayName("GET item 상세 조회 - 존재하지 않는 item일 경우 404 NOT_FOUND")
	void getItemAndLicenseAttributesByNonExistentItemId() throws Exception {
		// given
		String url = "/api/items/99999999999";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ITEM.name()));
	}

	@Test
	@DisplayName("GET 엔터프라이즈 Item 구매 요청 데이터 검증 - 검증 성공 200 OK")
	void verify() throws Exception {
		// given
		String url = "/api/items/1000000009/payment-link/verification";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET 엔터프라이즈 Item 구매 요청 데이터 검증 - item Id가 일치하지 않는 경우 404 NOT_FOUND")
	void verifyByNonExistentItemId() throws Exception {
		// given
		String url = "/api/items/99999999999/payment-link/verification";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ITEM_PAYMENT_LINK.name()));
	}

	@Test
	@DisplayName("GET 부가 서비스 item id 조회 - 조회 성공 200 OK")
	void getAttributeItemIdByLicenseItemId() throws Exception {
		// given
		String url = "/api/items/1000000009/attributeItemId";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value("1000000010"));
	}

	@Test
	@DisplayName("GET 부가 서비스 item id 조회 - 존재하지 않는 item id일 경우 404 NOT_FOUND")
	void getAttributeItemIdByNonExistentItemId() throws Exception {
		// given
		String url = "/api/items/99999999999/attributeItemId";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ITEM.name()));
	}

	@Test
	@DisplayName("GET 부가 서비스 item id 조회 - 해당 라이센스에 등록되어 있는 부가서비스 아이템이 없는 경우 404 NOT_FOUND")
	void getAttributeItemIdByNonExistentAttributeItemId() throws Exception {
		// given
		String url = "/api/items/1000000011/attributeItemId";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ATTRIBUTE_ITEM.name()));
	}

	@Test
	@DisplayName("GET 구매 가능한 item List 조회 - 검색 시 정상 200 OK")
	void getPurchaseListByAll() throws Exception {
		// given
		String url = "/api/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(4));
	}

	@Test
	@DisplayName("GET 구매 가능한 item List 조회 - YEAR 검색 시 정상 200 OK")
	void getPurchaseListByYear() throws Exception {
		// given
		String url = "/api/items?recurringInterval=" + RecurringIntervalType.YEAR.name();

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1));
	}

	@Test
	@DisplayName("GET 구매 가능한 item List 조회 - NONE 검색 시 정상 200 OK")
	void getPurchaseListByNone() throws Exception {
		// given
		String url = "/api/items?recurringInterval=" + RecurringIntervalType.NONE.name();

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	@DisplayName("GET 구매 가능한 item List 조회 - MONTH 검색 시 정상 200 OK")
	void getPurchaseListByMonth() throws Exception {
		// given
		String url = "/api/items?recurringInterval=" + RecurringIntervalType.MONTH.name();

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1));
	}
}
