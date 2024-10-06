package com.virnect.account.adapter.inbound.controller;

import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import com.virnect.account.adapter.inbound.dto.response.HubSpotAccessTokenResponseDto;
import com.virnect.account.adapter.outbound.response.HubSpotTokenResponse;
import com.virnect.account.port.inbound.HubspotService;

@ActiveProfiles("test")
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class HubSpotControllerTest {
	@Autowired
	MockMvc mockMvc;

	@MockBean
	private HubspotService hubSpotService;

	@BeforeEach
	void mockUp() {
		HubSpotTokenResponse hubSpotTokenResponse = new HubSpotTokenResponse();
		hubSpotTokenResponse.setAccessToken("accessToken");
		hubSpotTokenResponse.setTokenType("bearer");
		hubSpotTokenResponse.setExpiresIn(1800L);
		HubSpotAccessTokenResponseDto hubSpotAccessTokenResponseDto = HubSpotAccessTokenResponseDto.from(
			hubSpotTokenResponse);
		when(hubSpotService.getHubSpotToken()).thenReturn(hubSpotAccessTokenResponseDto);
	}

	@Test
	@DisplayName("POST hubspot token")
	void getHubSpotAccessToken() throws Exception {
		String url = "/api/hubspot/token";

		mockMvc.perform(MockMvcRequestBuilders
				.post(url)
				.contentType(MediaType.APPLICATION_JSON_VALUE)
			).andDo(print())
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.accessToken").isNotEmpty())
			.andExpect(jsonPath("$.accessToken").isString())
			.andExpect(jsonPath("$.grantType").isNotEmpty())
			.andExpect(jsonPath("$.grantType").isString())
			.andExpect(jsonPath("$.expireIn").isNotEmpty())
			.andExpect(jsonPath("$.expireIn").isNumber())
		;
	}
}
