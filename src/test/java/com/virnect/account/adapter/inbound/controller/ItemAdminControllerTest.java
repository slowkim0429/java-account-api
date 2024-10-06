package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.exception.ErrorCode.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.math.BigDecimal;
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
import org.springframework.test.web.servlet.result.MockMvcResultHandlers;
import org.springframework.test.web.servlet.result.MockMvcResultMatchers;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.item.ItemExposeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemPaymentLinkRequestDto;
import com.virnect.account.adapter.inbound.dto.request.item.ItemRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.ContractAPIRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/admin_users.sql",
	"classpath:data/licenses.sql",
	"classpath:data/license_grades.sql",
	"classpath:data/products.sql",
	"classpath:data/license_attributes.sql",
	"classpath:data/items.sql",
	"classpath:data/domains.sql",
	"classpath:data/users.sql",
	"classpath:data/item_payment_links.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ItemAdminControllerTest {
	@Autowired
	public MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

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
	@DisplayName("PUT item 수정 - 성공 200")
	void update() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"Track item",
			"LICENSE",
			1000000001L,
			null,
			RecurringIntervalType.NONE.name(),
			new BigDecimal("0"),
			new BigDecimal("0")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT item 수정 - Track 제품이지만 Free 타입 외 등급으로 수정하는 경우 400 BAD_REQUEST")
	void updateTrackItemByNoFreeLicenseGrade() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"Track item",
			"LICENSE",
			1000000012L,
			null,
			RecurringIntervalType.NONE.name(),
			new BigDecimal("0"),
			new BigDecimal("0")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - Track 제품이지만 가격이 0을 초과하는 경우 400 BAD_REQUEST")
	void updateTrackItemByExceedingZeroAmount() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"Track item",
			"LICENSE",
			1000000001L,
			null,
			RecurringIntervalType.NONE.name(),
			new BigDecimal("1"),
			new BigDecimal("0")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - Track 제품이지만 RecurringIntervalType이 NONE이 아닌 경우 400 BAD_REQUEST")
	void updateTrackItemByMonthRecurringIntervalType() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"Track item",
			"LICENSE",
			1000000001L,
			null,
			RecurringIntervalType.MONTH.name(),
			new BigDecimal("0"),
			new BigDecimal("0")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_RECURRING_INTERVAL_TYPE.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - Free 로 변경 할 때 ItemType 이 ATTRIBUTE 일 경우 BAD_REQUEST 400")
	void updateByFreeItemButItemTypeIsAttribute() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"ATTRIBUTE",
			1000000013L,
			null,
			"NONE",
			new BigDecimal("0"),
			new BigDecimal("0")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_ITEM_TYPE.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - Free 로 변경 할 때 RecurringInterval 이 MONTH 일 경우 BAD_REQUEST 400")
	void updateByFreeItemButRecurringIntervalIsMonth() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			1000000013L,
			null,
			"MONTH",
			new BigDecimal("0"),
			new BigDecimal("0")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_RECURRING_INTERVAL_TYPE.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - Free 로 변경 할 때 RecurringInterval 이 YEAR 일 경우 BAD_REQUEST 400")
	void updateByFreeItemButRecurringIntervalIsYear() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			1000000013L,
			null,
			"YEAR",
			new BigDecimal("0"),
			new BigDecimal("0")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_RECURRING_INTERVAL_TYPE.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - Free 로 변경 할 때 amount 가 0이 아닐 경우 BAD_REQUEST 400")
	void updateByFreeItemButAmountIsNotZero() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			1000000013L,
			null,
			"NONE",
			new BigDecimal("10"),
			new BigDecimal("10")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - item을 찾을 수 없는 경우 404")
	void updateNotFoundItem() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			1000000001L,
			null,
			"MONTH",
			new BigDecimal("200"),
			new BigDecimal("200")
		);
		Long itemId = 9000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_ITEM.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - item useStatus가 delete인 경우 404")
	void updateItemUseStatusDelete() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			1000000001L,
			null,
			"MONTH",
			new BigDecimal("200"),
			new BigDecimal("200")
		);
		Long itemId = 1000000003L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_ITEM.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - item status가 register가 아닐경우 400")
	void updateItemStatusNotRegister() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			1000000001L,
			null,
			"MONTH",
			new BigDecimal("200"),
			new BigDecimal("200")
		);
		Long itemId = 1000000002L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - 수정하는 license를 찾을 수 없는 경우 404")
	void updateNotFoundLicense() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			9000000000L,
			null,
			"MONTH",
			new BigDecimal("200"),
			new BigDecimal("200")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_LICENSE.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - 수정하는 license useStatus가 delete인 경우 404")
	void updateLicenseUseStatusDelete() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			1000000002L,
			null,
			"MONTH",
			new BigDecimal("200"),
			new BigDecimal("200")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_LICENSE.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - 수정하는 license status가 approve 아닌경우 400")
	void updateLicenseNotApprove() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			1000000010L,
			null,
			"MONTH",
			new BigDecimal("200"),
			new BigDecimal("200")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()))
			.andReturn();
	}

	@Test
	@DisplayName("PUT item 수정 - 수정하는 license grade가 enterprise인데 recurringIntervalType이 NONE인 경우 400")
	void updateEnterpriseRecurringIntervalTypeNone() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			1000000011L,
			null,
			"NONE",
			new BigDecimal("200"),
			new BigDecimal("200")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - 수정하는 license grade가 enterprise일때 amount가 monthlyUsedAmount * 12 보다 클 경우 400")
	void updateMonthlyUsedAmountError() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"LICENSE",
			1000000011L,
			null,
			"YEAR",
			new BigDecimal("130"),
			new BigDecimal("10")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_MONTHLY_USED_AMOUNT.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - 수정하는 license itemType이 attribute일때 licenseAttributeId에 부가서비스가 없는 경우 400")
	void updateAttributeNotDataType() throws Exception {
		// given
		ItemRequestDto itemRequestDto = ItemRequestDto.of(
			"squars item",
			"ATTRIBUTE",
			1000000011L,
			1000000000L,
			"NONE",
			new BigDecimal("130"),
			new BigDecimal("10")
		);
		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(itemRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Item 승인 상태 변경 (APPROVED)")
	void updateByStatus() throws Exception {
		// given
		String itemId = "1000000006";
		String status = ApprovalStatus.APPROVED.name();
		String url = String.format("/api/admin/items/%s/status/%s", itemId, status);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("PUT Item 승인 상태 변경 (REJECT)")
	void updateByStatusIsReject() throws Exception {
		// given
		String itemId = "1000000006";
		String status = ApprovalStatus.REJECT.name();
		String url = String.format("/api/admin/items/%s/status/%s", itemId, status);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("PUT Item 승인 상태 변경 - 400 에러 - 인풋 status 값이 올바르지 않은 경우")
	void updateByStatus_isBadRequest_withInvalidInput() throws Exception {
		// given
		String itemId = "1000000006";
		String status = " ";
		String url = String.format("/api/admin/items/%s/status/%s", itemId, status);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()))
			.andReturn();
	}

	@Test
	@DisplayName("PUT Item 승인 상태 변경 - 400 에러 - 이미 승인된 아이템의 승인 상태를 변경하려는 경우")
	void updateByStatus_isBadRequest_withInvalidStatus() throws Exception {
		// given
		String itemId = "1000000007";
		String status = ApprovalStatus.APPROVED.name();
		String url = String.format("/api/admin/items/%s/status/%s", itemId, status);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()))
			.andReturn();
	}

	@Test
	@DisplayName("POST item 등록 - Track 제품으로 아이템 등록 성공 OK 200")
	void createTrackProductItem() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"Track SDK",
			ItemType.LICENSE.name(),
			1000000001L,
			null,
			RecurringIntervalType.NONE.name(),
			new BigDecimal("0"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST item 등록 - 무료 라이센스 등급이 아닌 타입으로 Track 아이템 등록 BAD_REQUEST 400")
	void createTrackProductItemByNoFreeLicenseGrade() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"Track SDK",
			ItemType.LICENSE.name(),
			1000000003L,
			null,
			RecurringIntervalType.NONE.name(),
			new BigDecimal("0"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - FREE Grade 아닌 1년 구독형 아이템 등록 성공 OK 200")
	void createNoFreeGradeItemForYear() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS PRO ITEM - YEAR",
			"LICENSE",
			1000000004L,
			null,
			"YEAR",
			new BigDecimal("1100"),
			new BigDecimal("100")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - 1개월 구독형 FREE_PLUS 아이템 등록 실패 BAD_REQUEST 400")
	void createFreeGradeItemForMonth() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS FREE_PLUS - MONTH",
			"LICENSE",
			1000000013L,
			null,
			"MONTH",
			new BigDecimal("0"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_RECURRING_INTERVAL_TYPE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - 1년 구독형 FREE_PLUS 아이템 등록 실패 BAD_REQUEST 400")
	void createFreeGradeItemForYear() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS FREE_PLUS - YEAR",
			"LICENSE",
			1000000013L,
			null,
			"YEAR",
			new BigDecimal("0"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_RECURRING_INTERVAL_TYPE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - 가격이 있는 FREE_PLUS 아이템 등록 실패 BAD_REQUEST 400")
	void createFreeGradeItemForNone() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS FREE_PLUS - NONE",
			"LICENSE",
			1000000013L,
			null,
			"NONE",
			new BigDecimal("1100"),
			new BigDecimal("100")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - 가격이 있는 FREE_PLUS 아이템 등록 실패 BAD_REQUEST 400")
	void createFreeGradeItemForAttributeType() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS FREE_PLUS - NONE",
			"ATTRIBUTE",
			1000000013L,
			null,
			"NONE",
			new BigDecimal("0"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_ITEM_TYPE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - FREE Grade 아닌 1달 구독형 아이템 등록 성공 OK 200")
	void createNoFreeGradeItemForMonth() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS PRO ITEM - MONTH",
			"LICENSE",
			1000000004L,
			null,
			"MONTH",
			new BigDecimal("100"),
			new BigDecimal("100")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - ENTERPRISE 이며 0원인 아이템 등록 실패 BAD_REQUEST 400")
	void createEnterpriseGradeItemForMonth() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS ENTERPRISE ITEM - MONTH",
			"LICENSE",
			1000000007L,
			null,
			"MONTH",
			new BigDecimal("0"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST 부가서비스 item 등록 - FREE Grade 아닌 부가 서비스 아이템 등록 성공 OK 200")
	void createAttributeItem() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS PRO ATTRIBUTE ITEM",
			"ATTRIBUTE",
			1000000007L,
			1000000036L,
			"NONE",
			new BigDecimal("100"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - Free Grade 라이센스일 경우 등록 성공 OK 200")
	void createFreeGradeItem() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS FREE ITEM",
			"LICENSE",
			1000000000L,
			null,
			"NONE",
			new BigDecimal("0"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST Free 라이센스 item 등록 - amount 가 1 이상 일 경우 BAD_REQUEST 400")
	void createFreeGradeItemButFailBecauseAmountIsOneThenHigher() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS FREE ITEM",
			"LICENSE",
			1000000000L,
			null,
			"NONE",
			new BigDecimal("1"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST Free 라이센스 item 등록 - monthlyUsedAmount 가 1 이상 일 경우 BAD_REQUEST 400")
	void createFreeGradeItemButFailBecauseMonthlyUsedAmountIsOneThenHigher() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS FREE ITEM",
			"LICENSE",
			1000000000L,
			null,
			"NONE",
			new BigDecimal("0"),
			new BigDecimal("1")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("POST 부가서비스 item 등록 - 이미 해당 라이센스에 동일한 부가서비스가 존재하는 경우 CONFLICT 409")
	void createAlreadyInExistenceAttributeItem() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS PRO ATTRIBUTE ITEM",
			"ATTRIBUTE",
			1000000004L,
			1000000018L,
			"NONE",
			new BigDecimal("100"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_ATTRIBUTE_ITEM.name()));
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - 부가 속성이 아닌 경우 BAD_REQUEST 400")
	void createItem_withInvalidLicenseAttributeId() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS PRO ITEM",
			"ATTRIBUTE",
			1000000004L,
			1000000014L,
			"NONE",
			new BigDecimal("10"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_LICENSE_ATTRIBUTE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - 요청하는 license에 속한 attribute가 아닌 경우 BAD_REQUEST 400")
	void createItem_withAnotherLicenseAttributeId() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS PRO ITEM",
			"ATTRIBUTE",
			1000000004L,
			1000000009L,
			"NONE",
			new BigDecimal("10"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_LICENSE_ATTRIBUTE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - 존재하지 않는 라이센스일 경우 NOT_FOUND 404")
	void createByNonExistentLicenseId() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS PRO ITEM - MONTH",
			"LICENSE",
			99999999999L,
			null,
			"MONTH",
			new BigDecimal("100"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_LICENSE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - 삭제된 라이센스일 경우 NOT_FOUND 404")
	void createByDeletedLicenseId() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS PRO ITEM - MONTH",
			"LICENSE",
			1000000002L,
			null,
			"MONTH",
			new BigDecimal("100"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_LICENSE.name()));
	}

	@Test
	@DisplayName("POST 라이센스 item 등록 - 환불 기준 가격이 1년 구독 금액을 12로 나눈 수보다 작거나 같을 경우 BAD_REQUEST 400")
	void createByInvalidMonthlyUsedAmount() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS PRO ITEM - MONTH",
			"LICENSE",
			1000000004L,
			null,
			"YEAR",
			new BigDecimal("100"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_MONTHLY_USED_AMOUNT.name()));
	}

	@Test
	@DisplayName("POST 부가서비스 item 등록 - recurringInterval이 NONE이 아닌 경우 400 BAD_REQUEST")
	void createAttributeItemWithInvalidValue() throws Exception {
		// given
		ItemRequestDto requestDto = ItemRequestDto.of(
			"SQUARS PRO ATTRIBUTE ITEM",
			"ATTRIBUTE",
			1000000004L,
			1000000017L,
			"YEAR",
			new BigDecimal("100"),
			new BigDecimal("0")
		);

		String url = "/api/admin/items";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("GET Item Revision 목록 조회")
	void getLicenseRevisions() throws Exception {
		String itemId = "1000000000";
		// when
		String url = String.format("/api/admin/items/%s/revisions", itemId);
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
	@DisplayName("PUT item useStatus 삭제 - 성공 200")
	void updateUseStatus() throws Exception {

		Long itemId = 1000000000L;
		String url = String.format("/api/admin/items/%s/useStatus/delete", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))

			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT item useStatus 상태 변경 - item이 register 상태가 아닐경우 400")
	void updateUseStatusNotApprove() throws Exception {

		Long itemId = 1000000002L;
		String url = String.format("/api/admin/items/%s/useStatus/delete", itemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))

			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("PUT 부가서비스 item 상태 변경 - 이미 해당 라이센스에 동일한 부가서비스가 존재하는 경우 CONFLICT 409")
	void updateAlreadyInExistenceAttributeItem() throws Exception {
		// given
		String url = "/api/admin/items/1000000012/status/APPROVED";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_ATTRIBUTE_ITEM.name()));
	}

	@Test
	@DisplayName("GET item 상세 조회 - 조회 성공 200 OK")
	void getItemDetailById() throws Exception {
		// given
		String url = "/api/admin/items/1000000000";

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
	@DisplayName("GET item 상세 조회 - 존재하지 않는 item일 경우 404 NOT_FOUND")
	void getItemDetailByNonExistentId() throws Exception {
		// given
		String url = "/api/admin/items/99999999999";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto)))
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ITEM.name()));
	}

	@Test
	@DisplayName("GET Item List - 성공 200")
	void getItemList() throws Exception {
		// when
		String url = "/api/admin/items";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isOk())
			.andExpect(MockMvcResultMatchers.jsonPath("$.pageMeta.totalElements").value(20));
	}

	@Test
	@DisplayName("GET Item List - itemName 조회 성공 200")
	void getItemListByItemName() throws Exception {
		// when
		String url = "/api/admin/items?itemName=Studio";
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
	@DisplayName("PUT Item switching free license - free is exposed 성공 200")
	void putItemSwitchingOnlyOneFreeLicenseItem() throws Exception {
		// when
		ItemExposeRequestDto requestDto = new ItemExposeRequestDto(true);
		final int freeLicenseItemId = 1000000014;
		String url = "/api/admin/items/" + freeLicenseItemId + "/expose";

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT item 수정 - 이미 수정된 아이템일 경우(request: false / persist: false) BAD_REQUEST 400")
	void putInvalidFreeItemExposedButHasFalse() throws Exception {
		// given
		ItemExposeRequestDto requestDto = new ItemExposeRequestDto(false);
		final int freeLicenseItemId = 1000000014;
		String url = String.format("/api/admin/items/%s/expose", freeLicenseItemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_ITEM_EXPOSED.name()));
	}

	@Test
	@DisplayName("PUT item 수정 - 이미 수정된 아이템일 경우(request: true / persist: true) BAD_REQUEST 400")
	void putInvalidFreeItemExposedButHasTrue() throws Exception {
		// given
		ItemExposeRequestDto requestDto = new ItemExposeRequestDto(true);
		final int freeLicenseItemId = 1000000013;
		String url = String.format("/api/admin/items/%s/expose", freeLicenseItemId);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.accept(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_ITEM_EXPOSED.name()));
	}

	@Test
	@DisplayName("PUT free license 아이템 수정 - 하나뿐인 아이템을 비노출 처리 실패 400")
	void putItemSwitchingFreeLicenseIsNotExposeAll() throws Exception {
		// when
		ItemExposeRequestDto requestDto = new ItemExposeRequestDto(false);
		final int freeLicenseItemId = 1000000013;
		String url = String.format("/api/admin/items/%s/expose", freeLicenseItemId);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_FREE_LICENSE_EXPOSED.name()));
	}

	@Test
	@DisplayName("PUT Professional license 아이템의 노출상태 수정 - 비노출->노출 변경 성공 200")
	void putItemSwitchingProfessionalLicenseArNotExposedProfessionalIsChangeToBeExpose() throws Exception {
		// when
		ItemExposeRequestDto requestDto = new ItemExposeRequestDto(true);
		final int professionalId = 1000000008;
		String url = "/api/admin/items/" + professionalId + "/expose";

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Professional license 아이템의 노출상태 수정 - 노출->비노출 변경 성공 200")
	void putItemSwitchingProfessionalLicenseExposedProfessionalIsChangeToBeNotExpose() throws Exception {
		// when
		ItemExposeRequestDto requestDto = new ItemExposeRequestDto(false);
		final int professionalId = 1000000015;
		String url = "/api/admin/items/" + professionalId + "/expose";

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Enterprise license Item 의 expose 변경 - (비노출->노출) 실패 400")
	void putEnterpriseItemIsExposed() throws Exception {
		// when
		ItemExposeRequestDto requestDto = new ItemExposeRequestDto(true);
		final int enterpriseLicenseItemId = 1000000009;
		String url = String.format("/api/admin/items/%s/expose", enterpriseLicenseItemId);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_ENTERPRISE_IS_EXPOSE.name()));
	}

	@Test
	@DisplayName("PUT license Item 의 expose 변경 - (비노출->비노출) 실패 400")
	void putEnterpriseItemIsExposedChangeNotWorking() throws Exception {
		// when
		ItemExposeRequestDto requestDto = new ItemExposeRequestDto(false);
		final int licenseItemId = 1000000009;
		String url = String.format("/api/admin/items/%s/expose", licenseItemId);

		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(requestDto))
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(MockMvcResultHandlers.print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_ITEM_EXPOSED.name()));
	}

	@Test
	@DisplayName("POST Item Payment Link Send - 성공 200")
	void createItemPaymentLink() throws Exception {
		// when
		String url = "/api/admin/items/1000000009/payment-link";

		ItemPaymentLinkRequestDto itemPaymentLinkRequestDto = new ItemPaymentLinkRequestDto();
		itemPaymentLinkRequestDto.setEmail("organiowner-one@virnect.com");

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(itemPaymentLinkRequestDto)))

			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST Item Payment Link Send - 401 Error - 요청한 아이템이 엔터프라이즈 아이템이 아닌 경우")
	void createItemPaymentLinkWithInvalidItem() throws Exception {
		// when
		String url = "/api/admin/items/1000000007/payment-link";
		ItemPaymentLinkRequestDto itemPaymentLinkRequestDto = new ItemPaymentLinkRequestDto();
		itemPaymentLinkRequestDto.setEmail("organiowner-one@virnect.com");

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(itemPaymentLinkRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));

	}

	@Test
	@DisplayName("POST Item Payment Link Send - 404 Error - 부가서비스 구매 요청을 보내려는 사용자가 제품 라이선스를 구독중이지 않을 경우")
	void createItemPaymentLinkWithNotFoundOrganizationLicense() throws Exception {
		// when
		String url = "/api/admin/items/1000000010/payment-link";
		ItemPaymentLinkRequestDto itemPaymentLinkRequestDto = new ItemPaymentLinkRequestDto();
		itemPaymentLinkRequestDto.setEmail("organiowner-one@virnect.com");

		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(itemPaymentLinkRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ORGANIZATION_LICENSE.name()));
	}

	@Test
	@DisplayName("POST synchronize item - item 정보 전파 OK 200")
	void synchronizeItem() throws Exception {
		String url = String.format("/api/admin/items/%s/synchronize", 1000000002);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST synchronize item - item 정보 전파 NOT_FOUND 404")
	void synchronizeItemButNotFound() throws Exception {
		String url = String.format("/api/admin/items/%s/synchronize", 9999999999L);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ITEM.name()));
	}

	@Test
	@DisplayName("POST APPROVED 상태가 아닌 synchronize item - item 정보 전파 BAD_REQUEST 400")
	void synchronizeItemButNotApproved() throws Exception {
		String url = String.format("/api/admin/items/%s/synchronize", 1000000012);

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_ITEM_STATUS.name()));
	}

	@Test
	@DisplayName("GET Item Payment Link Send Histories - 200 OK")
	void getItemPaymentLinks() throws Exception {
		// given
		String url = "/api/admin/items/payment-links";

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
}
