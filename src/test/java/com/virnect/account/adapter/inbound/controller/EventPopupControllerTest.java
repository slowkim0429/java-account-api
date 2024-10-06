package com.virnect.account.adapter.inbound.controller;

import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.eventpopup.SendCouponRequestDto;
import com.virnect.account.exception.ErrorCode;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/event_popups.sql",
	"classpath:data/coupons.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class EventPopupControllerTest {

	@Autowired
	public MockMvc mockMvc;

	@Autowired
	private ObjectMapper objectMapper;

	@Test
	@DisplayName("GET 최신 노출 event popup 조회 - 200 OK")
	void getLatestEventPopup() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
					.get("/api/event-popups/event-types/SUBMISSION/service-types/SQUARS/latest")
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("GET 최신 노출 event popup 조회(데이터없음) - 200 OK")
	void getLatestEventPopupButNoData() throws Exception {
		mockMvc.perform(
				MockMvcRequestBuilders
					.get("/api/event-popups/event-types/MARKETING/service-types/SQUARS/latest")
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 쿠폰 메일 발송 - 200 OK")
	void sendCouponEmail() throws Exception {
		SendCouponRequestDto sendCouponRequestDto = new SendCouponRequestDto("test@ruu.kr");
		mockMvc.perform(
				MockMvcRequestBuilders
					.post("/api/event-popups/1000000004/coupons/email")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(sendCouponRequestDto))
			)
			.andDo(print())
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST 쿠폰 메일 발송 - 404 NOT FOUND eventPopup")
	void sendCouponEmailWithNotFoundEventPopup() throws Exception {
		SendCouponRequestDto sendCouponRequestDto = new SendCouponRequestDto("test@ruu.kr");
		mockMvc.perform(
				MockMvcRequestBuilders
					.post("/api/event-popups/1000000099/coupons/email")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(sendCouponRequestDto))
			)
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_EVENT_POPUP.name()));
	}

	@Test
	@Sql(scripts = {
		"classpath:data/event_popups_not_found_coupon.sql"}, executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = {"classpath:data/event_popups.sql"}, executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("POST 쿠폰 메일 발송 - 404 NOT FOUND coupon")
	void sendCouponEmailWithNotFoundCoupon() throws Exception {
		SendCouponRequestDto sendCouponRequestDto = new SendCouponRequestDto("test@ruu.kr");
		mockMvc.perform(
				MockMvcRequestBuilders
					.post("/api/event-popups/1000000000/coupons/email")
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(sendCouponRequestDto))
			)
			.andDo(print())
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_COUPON.name()));
	}
}
