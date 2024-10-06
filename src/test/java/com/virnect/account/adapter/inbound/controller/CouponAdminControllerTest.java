package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.exception.ErrorCode.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZonedDateTime;
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

import com.virnect.account.adapter.inbound.dto.request.ApprovalStatusRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UseStatusRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.coupon.CouponModifyRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.CouponBenefitOption;
import com.virnect.account.domain.enumclass.CouponLicenseGradeMatchingType;
import com.virnect.account.domain.enumclass.CouponRecurringIntervalMatchingType;
import com.virnect.account.domain.enumclass.CouponType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/admin_users.sql",
	"classpath:data/coupons.sql",
	"classpath:data/items.sql",
	"classpath:data/licenses.sql",
	"classpath:data/license_grades.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class CouponAdminControllerTest {
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
	@DisplayName("GET Coupon 목록 조회 - 조회 성공 200 OK")
	void getCoupons() throws Exception {
		// when
		String url = "/api/admin/coupons";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(5));
	}

	@Test
	@DisplayName("GET Coupon 목록 조회 - 쿼리스트링(couponType)으로 조회 성공 200 OK")
	void getCouponsByCouponType() throws Exception {
		// when
		String url = "/api/admin/coupons?couponType=UPGRADE_LICENSE_ATTRIBUTE";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(5));
	}

	@Test
	@DisplayName("GET Coupon 목록 조회 - 쿼리스트링(status)으로 조회 성공 200 OK")
	void getCouponsByStatus() throws Exception {
		// when
		String url = "/api/admin/coupons?status=REGISTER";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(2));
	}

	@Test
	@DisplayName("GET Coupon 목록 조회 - 쿼리스트링(useStatus)으로 조회 성공 200 OK")
	void getCouponsByUseStatus() throws Exception {
		// when
		String url = "/api/admin/coupons?useStatus=USE";
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
	@DisplayName("GET Coupon 목록 조회 - 쿼리스트링(status + useStatus)으로 조회 성공 200 OK")
	void getCouponsByCouponTypeAndIsExposed() throws Exception {
		// when
		String url = "/api/admin/coupons?status=APPROVED&useStatus=USE";
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
	@DisplayName("POST Coupon 생성 - 생성 성공 200 OK")
	void create() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.MAXIMUM_GROUP.name();
		Long benefitValue = 3L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.MONTH.name();
		CouponCreateRequestDto requestDto = CouponCreateRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST Coupon 생성 - 기간이 10년 이상인 경우 400 BAD_REQUEST")
	void createByInvalidYearValue() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_PERIOD.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.YEAR.name();
		Long benefitValue = 10L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.MONTH.name();
		CouponCreateRequestDto requestDto = CouponCreateRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_PERIOD_BENEFIT_VALUE.name()));
	}

	@Test
	@DisplayName("POST Coupon 생성 - 기간이 120개월(10년) 이상인 경우 400 BAD_REQUEST")
	void createByInvalidMonthValue() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_PERIOD.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.MONTH.name();
		Long benefitValue = 120L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.MONTH.name();
		CouponCreateRequestDto requestDto = CouponCreateRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_PERIOD_BENEFIT_VALUE.name()));
	}

	@Test
	@DisplayName("POST Coupon 생성 - 이미 존재하는 coupon code일 경우 409 CONFLICT")
	void createByAlreadyExistsCode() throws Exception {
		// given
		String name = "coupon name";
		String code = "ABCDEFGHIJKLMN1";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.MAXIMUM_GROUP.name();
		Long benefitValue = 3L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.MONTH.name();
		CouponCreateRequestDto requestDto = CouponCreateRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_COUPON_CODE.name()));
	}

	@Test
	@DisplayName("POST Coupon 생성 - expiredDate가 과거인 경우 400 BAD_REQUEST")
	void createByInvalidExpiredDate() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().minusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.MAXIMUM_GROUP.name();
		Long benefitValue = 3L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.MONTH.name();
		CouponCreateRequestDto requestDto = CouponCreateRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_EXPIRED_DATE.name()));
	}

	@Test
	@DisplayName("POST Coupon 생성 - benefit option이 coupon type과 일치하지 않는 경우 400 BAD_REQUEST")
	void createByInvalidBenefitOption() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.YEAR.name();
		Long benefitValue = 1L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.MONTH.name();
		CouponCreateRequestDto requestDto = CouponCreateRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_BENEFIT_OPTION.name()));
	}

	@Test
	@DisplayName("POST Coupon 생성 - benefit value가 양수가 아닐 경우 400 BAD_REQUEST")
	void createByInvalidBenefitValue() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.YEAR.name();
		Long benefitValue = -1L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.MONTH.name();
		CouponCreateRequestDto requestDto = CouponCreateRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Coupon 승인 상태 변경 - 변경 성공 200 OK")
	void updateApprovalStatus() throws Exception {
		// given
		String status = ApprovalStatus.APPROVED.name();
		ApprovalStatusRequestDto requestDto = ApprovalStatusRequestDto.from(status);

		String url = "/api/admin/coupons/1000000000/approval-status";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Coupon 승인 상태 변경 - 해당 쿠폰이 존재하지 않는 경우 404 NOT_FOUND")
	void updateApprovalStatusByNonExistentCouponId() throws Exception {
		// given
		String status = ApprovalStatus.APPROVED.name();
		ApprovalStatusRequestDto requestDto = ApprovalStatusRequestDto.from(status);

		String url = "/api/admin/coupons/99999999999/approval-status";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_COUPON.name()));
	}

	@Test
	@DisplayName("PUT Coupon 승인 상태 변경 - 변경할 수 없는 상태의 쿠폰인 경우 400 BAD_REQUEST")
	void updateApprovalStatusInImmutableStatus() throws Exception {
		// given
		String status = ApprovalStatus.REJECT.name();
		ApprovalStatusRequestDto requestDto = ApprovalStatusRequestDto.from(status);

		String url = "/api/admin/coupons/1000000001/approval-status";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("PUT Coupon 사용 상태 변경 - 변경 성공 200 OK")
	void updateUseStatus() throws Exception {
		// given
		String status = UseStatus.USE.name();
		UseStatusRequestDto requestDto = UseStatusRequestDto.from(status);

		String url = "/api/admin/coupons/1000000004/use-status";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Coupon 사용 상태 변경 - 해당 쿠폰이 존재하지 않는 경우 404 NOT_FOUND")
	void updateUseStatusByNonExistentCouponId() throws Exception {
		// given
		String status = UseStatus.USE.name();
		UseStatusRequestDto requestDto = UseStatusRequestDto.from(status);

		String url = "/api/admin/coupons/99999999999/use-status";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_COUPON.name()));
	}

	@Test
	@DisplayName("PUT Coupon 사용 상태 변경 - 승인 상태가 아닌 쿠폰인 경우 400 BAD_REQUEST")
	void updateUseStatusByNotApprovedCouponId() throws Exception {
		// given
		String status = UseStatus.USE.name();
		UseStatusRequestDto requestDto = UseStatusRequestDto.from(status);

		String url = "/api/admin/coupons/1000000001/use-status";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("PUT Coupon 데이터 수정 - 수정 성공 200 OK")
	void modify() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.MAXIMUM_GROUP.name();
		Long benefitValue = 3L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.YEAR.name();

		CouponModifyRequestDto requestDto = CouponModifyRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons/1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Coupon 데이터 수정 - 기간이 120개월(10년) 이상인 경우 400 BAD_REQUEST")
	void modifyByInvalidMonthValue() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_PERIOD.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.MONTH.name();
		Long benefitValue = 120L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.YEAR.name();

		CouponModifyRequestDto requestDto = CouponModifyRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons/1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_PERIOD_BENEFIT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Coupon 데이터 수정 - 기간이 10년 이상인 경우 400 BAD_REQUEST")
	void modifyByInvalidYearValue() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_PERIOD.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.YEAR.name();
		Long benefitValue = 10L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.YEAR.name();

		CouponModifyRequestDto requestDto = CouponModifyRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons/1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_PERIOD_BENEFIT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Coupon 데이터 수정 - 존재하지 않는 쿠폰일 경우 404 NOT_FOUND")
	void modifyByNonExistentCouponId() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.MAXIMUM_GROUP.name();
		Long benefitValue = 3L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.YEAR.name();

		CouponModifyRequestDto requestDto = CouponModifyRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons/9999999999";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_COUPON.name()));
	}

	@Test
	@DisplayName("PUT Coupon 데이터 수정 - 등록 상태의 쿠폰이 아닌 경우 400 BAD_REQUEST")
	void modifyCouponOfNotRegisterStatus() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.MAXIMUM_GROUP.name();
		Long benefitValue = 3L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.YEAR.name();

		CouponModifyRequestDto requestDto = CouponModifyRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons/1000000002";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("PUT Coupon 데이터 수정 - 현재 쿠폰을 제외한 데이터 중 이미 존재하는 coupon code일 경우 409 CONFLICT")
	void modifyByAlreadyExistsCode() throws Exception {
		// given
		String name = "coupon name";
		String code = "ABCDEFGHIJKLMN2";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.MAXIMUM_GROUP.name();
		Long benefitValue = 3L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.YEAR.name();

		CouponModifyRequestDto requestDto = CouponModifyRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons/1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isConflict())
			.andExpect(jsonPath("$.customError").value(DUPLICATE_COUPON_CODE.name()));
	}

	@Test
	@DisplayName("PUT Coupon 데이터 수정 - expiredDate가 과거인 경우 400 BAD_REQUEST")
	void modifyByInvalidExpiredDate() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().minusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.MAXIMUM_GROUP.name();
		Long benefitValue = 3L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.YEAR.name();

		CouponModifyRequestDto requestDto = CouponModifyRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons/1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_EXPIRED_DATE.name()));
	}

	@Test
	@DisplayName("PUT Coupon 데이터 수정 - benefit option이 coupon type과 일치하지 않는 경우 400 BAD_REQUEST")
	void modifyByInvalidBenefitOption() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.YEAR.name();
		Long benefitValue = 1L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.MONTH.name();
		CouponModifyRequestDto requestDto = CouponModifyRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons/1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_BENEFIT_OPTION.name()));
	}

	@Test
	@DisplayName("PUT Coupon 데이터 수정 - benefit value가 양수가 아닐 경우 400 BAD_REQUEST")
	void modifyByInvalidBenefitValue() throws Exception {
		// given
		String name = "coupon name";
		String code = "code";
		Long maxCount = 100L;
		String couponType = CouponType.UPGRADE_LICENSE_ATTRIBUTE.name();
		ZonedDateTime expiredDate = ZonedDateTime.now().plusDays(1);
		String description = "coupon description";
		String benefitOption = CouponBenefitOption.YEAR.name();
		Long benefitValue = -1L;
		String couponLicenseGradeMatchingType = CouponLicenseGradeMatchingType.STANDARD.name();
		String couponRecurringIntervalMatchingType = CouponRecurringIntervalMatchingType.MONTH.name();
		CouponCreateRequestDto requestDto = CouponCreateRequestDto.of(
			name, code, maxCount, couponType, expiredDate, description, benefitOption, benefitValue,
			couponLicenseGradeMatchingType, couponRecurringIntervalMatchingType
		);

		String url = "/api/admin/coupons/1000000000";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.contentType(MediaType.APPLICATION_JSON)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.content(objectMapper.writeValueAsString(requestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("GET Coupon 상세 조회 - 조회 성공 200 OK")
	void getCoupon() throws Exception {
		// when
		String url = "/api/admin/coupons/1000000000";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.id").value("1000000000"))
			.andExpect(jsonPath("$.code").value("ABCDEFGHIJKLMN1"))
			.andExpect(jsonPath("$.name").value("name 1"))
			.andExpect(jsonPath("$.description").value("description 1"))
			.andExpect(jsonPath("$.couponType").value("UPGRADE_LICENSE_ATTRIBUTE"))
			.andExpect(jsonPath("$.maxCount").value("100"))
			.andExpect(jsonPath("$.status").value("REGISTER"))
			.andExpect(jsonPath("$.useStatus").value("UNUSE"));
	}

	@Test
	@DisplayName("GET Coupon 상세 조회 - 존재하지 않는 쿠폰일 경우 404 NOT_FOUND")
	void getCouponByNonExistentCouponId() throws Exception {
		// when
		String url = "/api/admin/coupons/9999999999";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_COUPON.name()));
	}

	@Test
	@DisplayName("POST Synchronize coupon data - 동기화 성공 200 OK")
	void synchronizeCoupon() throws Exception {
		String url = "/api/admin/coupons/1000000004/synchronize";

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
	@DisplayName("POST Synchronize coupon data - 존재하지 않는 쿠폰일 경우 404 NOT_FOUND")
	void synchronizeItemButNotFound() throws Exception {
		String url = "/api/admin/coupons/99999999999/synchronize";

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
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_COUPON.name()));
	}

	@Test
	@DisplayName("POST Synchronize coupon data - 승인된 쿠폰이 아닐 경우 400 BAD_REQUEST")
	void synchronizeItemButNotApproved() throws Exception {
		String url = "/api/admin/coupons/1000000000/synchronize";

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
			.andExpect(jsonPath("$.customError").value(INVALID_STATUS.name()));
	}

	@Test
	@DisplayName("GET Coupon 변경 이력 조회 - 조회 성공 OK 200")
	void getCouponRevisions() throws Exception {
		// given
		String url = "/api/admin/coupons/1000000000/revisions";

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
