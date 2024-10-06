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

import com.virnect.account.adapter.inbound.dto.request.product.ProductCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductStatusChangeRequestDto;
import com.virnect.account.adapter.inbound.dto.request.product.ProductUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.ProductType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.port.outbound.ProductRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {"classpath:data/admin_users.sql", "classpath:data/products.sql", "classpath:data/users.sql"})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class ProductAdminControllerTest {
	@Autowired
	public MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private ProductRepository productRepository;

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
	@DisplayName("POST Product create")
	void create() throws Exception {
		productRepository.deleteAll();
		//given
		ProductCreateRequestDto productCreateRequestDto = new ProductCreateRequestDto();
		productCreateRequestDto.setName("스튜디오");
		productCreateRequestDto.setProductType(ProductType.SQUARS);

		// when
		String url = "/api/admin/products";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(productCreateRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("PUT Product update with new product type")
	void update_with_new_productType() throws Exception {
		productRepository.deleteAll();
		//given
		ProductUpdateRequestDto productUpdateRequestDto = new ProductUpdateRequestDto();
		productUpdateRequestDto.setId(1000000000L);
		productUpdateRequestDto.setName("버넥트파트너");
		productUpdateRequestDto.setProductType(ProductType.SQUARS);

		// when
		String url = "/api/admin/products";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(productUpdateRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("PUT update Product Status")
	void updateByStatus() throws Exception {
		//given
		ProductStatusChangeRequestDto productStatusChangeRequestDto = new ProductStatusChangeRequestDto();
		productStatusChangeRequestDto.setProductId(1000000000L);
		productStatusChangeRequestDto.setStatus(ApprovalStatus.APPROVED);

		// when
		String url = "/api/admin/products/status";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(productStatusChangeRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("PUT update Product Status isNotFound")
	void updateByStatus_with_not_found_product() throws Exception {
		//given
		ProductStatusChangeRequestDto productStatusChangeRequestDto = new ProductStatusChangeRequestDto();
		productStatusChangeRequestDto.setProductId(1000000004L);
		productStatusChangeRequestDto.setStatus(ApprovalStatus.APPROVED);

		// when
		String url = "/api/admin/products/status";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(productStatusChangeRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_PRODUCT.name()))
			.andReturn();
	}

	@Test
	@DisplayName("GET Product param productId")
	void getProductById() throws Exception {
		//given
		String productId = "1000000000";

		// when
		String url = String.format("/api/admin/products/%s", productId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.productType").value("SQUARS"))
			.andReturn();
	}

	@Test
	@DisplayName("GET Product param productId with not found")
	void getProductById_with_not_found() throws Exception {
		//given
		String productId = "1000000004";

		// when
		String url = String.format("/api/admin/products/%s", productId);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_PRODUCT.name()))
			.andReturn();
	}

	@Test
	@DisplayName("GET Product by product type")
	void getProducts() throws Exception {
		//given
		String productType = ProductType.SQUARS.name();

		// when
		String url = String.format("/api/admin/products?productType=%s", productType);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.pageMeta.totalElements").value(1))
			.andReturn();
	}

	@Test
	@DisplayName("GET Product by status")
	void getProducts_by_status() throws Exception {
		//given
		String status = ApprovalStatus.APPROVED.name();

		// when
		String url = String.format("/api/admin/products?status=%s", status);
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(adminTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.contents.[0].status").value(status))
			.andExpect(jsonPath("$.pageMeta.totalElements").value(2))
			.andReturn();
	}
}
