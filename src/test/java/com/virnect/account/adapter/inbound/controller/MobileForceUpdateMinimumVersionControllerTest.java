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

import com.virnect.account.exception.ErrorCode;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/mobile_force_update_minimum_versions.sql",
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class MobileForceUpdateMinimumVersionControllerTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	@DisplayName("GET Mobile version 강제 업데이트 최소 버전 확인 - 성공 200 OK")
	void getForceUpdateVersion() throws Exception {
		// given
		String url = "/api/mobile-managements/force-update-minimum-versions";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@Sql(scripts = "classpath:data/mobile_force_update_minimum_versions_truncate.sql",
		executionPhase = Sql.ExecutionPhase.BEFORE_TEST_METHOD)
	@Sql(scripts = "classpath:data/mobile_force_update_minimum_versions_truncate.sql",
		executionPhase = Sql.ExecutionPhase.AFTER_TEST_METHOD)
	@DisplayName("GET Mobile version 강제 업데이트 최소 버전 확인 - 성공 200 OK")
	void getForceUpdateVersionButNotFound() throws Exception {
		// given
		String url = "/api/mobile-managements/force-update-minimum-versions";

		// when
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.contentType(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(ErrorCode.NOT_FOUND_MOBILE_FORCE_UPDATE_MINIMUM_VERSION.name()));
	}
}
