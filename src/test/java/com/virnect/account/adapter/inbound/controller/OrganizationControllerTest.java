package com.virnect.account.adapter.inbound.controller;

import static com.virnect.account.exception.ErrorCode.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.time.ZonedDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

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

import com.fasterxml.jackson.databind.ObjectMapper;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationContractRequestDto;
import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationUpdateRequestDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.domain.enumclass.ContractStatus;
import com.virnect.account.domain.enumclass.CustomerType;
import com.virnect.account.domain.enumclass.ItemType;
import com.virnect.account.domain.enumclass.PaymentType;
import com.virnect.account.domain.enumclass.RecurringIntervalType;
import com.virnect.account.domain.enumclass.Role;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.ContractAPIRepository;
import com.virnect.account.port.outbound.RedisRepository;
import com.virnect.account.port.outbound.UserRepository;
import com.virnect.account.port.outbound.UserRoleRepository;
import com.virnect.account.port.outbound.WorkspaceAPIRepository;
import com.virnect.account.security.jwt.TokenProvider;

@ActiveProfiles("test")
@Sql(scripts = {
	"classpath:data/users.sql", "classpath:data/organizations.sql", "classpath:data/products.sql",
	"classpath:data/licenses.sql", "classpath:data/organization_licenses.sql",
	"classpath:data/organization_contracts.sql", "classpath:data/invite.sql",
	"classpath:data/license_attributes.sql", "classpath:data/license_grades.sql",
	"classpath:data/service_regions.sql", "classpath:data/service_region_locale_mappings.sql"
	, "classpath:data/items.sql",
	"classpath:data/organization_license_attributes.sql",
	"classpath:data/organization_license_additional_attributes.sql",
	"classpath:data/service_locale_states.sql"
})
@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.MOCK)
@AutoConfigureMockMvc
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
class OrganizationControllerTest {
	@Autowired
	public MockMvc mockMvc;
	@Autowired
	private ObjectMapper objectMapper;

	@Autowired
	private UserRepository userRepository;

	@Autowired
	private UserRoleRepository userRoleRepository;

	@Autowired
	private TokenProvider tokenProvider;

	@Autowired
	private RedisRepository redisRepository;

	@MockBean
	private WorkspaceAPIRepository workspaceAPIRepository;

	@MockBean
	private ContractAPIRepository contractAPIRepository;

	private TokenResponseDto userTokenResponseDto;

	private TokenResponseDto organizationOwnerTokenResponseDto;

	private TokenResponseDto organizationRegisterTokenResponseDto;

	private TokenResponseDto organizationOwnerSingleTokenResponseDto;

	private String getAuthorizationBearerToken(TokenResponseDto tokenResponseDto) {
		return tokenResponseDto.getGrantType() + " " + tokenResponseDto.getAccessToken();
	}

	@BeforeAll
	void setUp() throws Exception {
		userSetup();
		organizationOwnerSetup();
		organizationRegisterSetup();
		organizationOwnerSingleSetup();
	}

	void userSetup() {
		Long userId = 1000000005L;
		Long organizationId = 0L;
		String nickname = "user1";
		String email = "user1@guest.com";
		String language = "ko_KR";
		List<Role> roles = Arrays.stream("ROLE_USER".split(","))
			.map(Role::valueOf)
			.collect(Collectors.toList());

		userTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, nickname, email, language, roles);

		redisRepository.setObjectValue(
			userId.toString(), userTokenResponseDto.getRefreshToken(),
			userTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	void organizationRegisterSetup() {
		Long userId = 1000000009L;
		Long organizationId = 1000000009L;
		String nickname = "organizationRegister nickname";
		String email = "organiRegister@virnect.com";
		String language = "ko_KR";
		List<Role> roles = Arrays.stream("ROLE_USER,ROLE_ORGANIZATION_OWNER".split(","))
			.map(Role::valueOf)
			.collect(Collectors.toList());

		organizationRegisterTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, nickname, email, language, roles);

		redisRepository.setObjectValue(
			userId.toString(), organizationRegisterTokenResponseDto.getRefreshToken(),
			organizationRegisterTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	void organizationOwnerSetup() {
		Long userId = 1000000001L;
		Long organizationId = 1000000000L;
		String nickname = "organization nickname";
		String email = "organiowner@virnect.com";
		String language = "ko_KR";
		List<Role> roles = Arrays.stream("ROLE_USER,ROLE_ORGANIZATION_OWNER".split(","))
			.map(Role::valueOf)
			.collect(Collectors.toList());

		organizationOwnerTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, nickname, email, language, roles);

		redisRepository.setObjectValue(
			userId.toString(), organizationOwnerTokenResponseDto.getRefreshToken(),
			organizationOwnerTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	void organizationOwnerSingleSetup() {
		Long userId = 1000000014L;
		Long organizationId = 1000000004L;
		String nickname = "organization nickname";
		String email = "organiowner-one@virnect.com";
		String language = "ko_KR";
		List<Role> roles = Arrays.stream("ROLE_USER,ROLE_ORGANIZATION_OWNER".split(","))
			.map(Role::valueOf)
			.collect(Collectors.toList());

		organizationOwnerSingleTokenResponseDto = tokenProvider.createToken(
			userId, organizationId, nickname, email, language, roles);

		redisRepository.setObjectValue(
			userId.toString(), organizationOwnerSingleTokenResponseDto.getRefreshToken(),
			organizationOwnerSingleTokenResponseDto.getAccessTokenExpiresIn()
		);
	}

	@AfterAll
	void tearDown() {
		redisRepository.deleteObjectValue(tokenProvider.getUserNameFromJwtToken(userTokenResponseDto.getAccessToken()));
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(organizationOwnerTokenResponseDto.getAccessToken()));
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(organizationRegisterTokenResponseDto.getAccessToken()));
		redisRepository.deleteObjectValue(
			tokenProvider.getUserNameFromJwtToken(organizationOwnerSingleTokenResponseDto.getAccessToken()));
	}

	@Test
	@DisplayName("PUT Organization update")
	void update() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect1");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Organization 수정  - name에 다국어 모두 허용 200 OK")
	void updateByNameWithMultilingual() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("かわいがり 父母 Âme Love");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Organization 수정  - first name과 last name에 다국어 모두 허용 200 OK")
	void updateByFistNameAndLastNameWithMultilingual() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("かわいがり 父母 Âme Love");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("かわいがり 父母 Âme Love");
		organizationUpdateRequestDto.setLastName("かわいがり 父母 Âme Love");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Organization update - 403 Error - 본인의 organization 이 아닌데 수정 요청한 경우")
	void updateOrganization_isForbidden_withInvalidOrganizationOwner() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setProvince("1012345678");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000000";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_AUTHORIZATION.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - CustomerType을 법인으로 선택하였지만 CorporateNumber를 입력하지 않은 경우 400 BAD_REQUEST")
	void updateByLegalPersonAndBlankCorporateNumber() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setProvince("1012345678");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - CustomerType을 법인으로 선택하였지만 vatIdentificationNumber 값을 입력하지 않은 경우 400 BAD_REQUEST")
	void updateByLegalPersonAndBlankVatIdentificationNumber() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setProvince("1012345678");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - CustomerType을 개인으로 선택하였지만 CorporateNumber를 입력한 경우 400 BAD_REQUEST")
	void updateByNaturalPersonAndCorporateNumber() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.NATURAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setProvince("1012345678");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - 전화번호가 0으로 시작하는 경우 400 BAD_REQUEST")
	void updateByInvalidPhoneNumber() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.NATURAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setProvince("1012345678");
		organizationUpdateRequestDto.setPhoneNumber("01012345678");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - vatIdentificationNumber에 하이픈(-) 외 기호가 입력된 경우 400 BAD_REQUEST")
	void updateByInvalidOrganization() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B194810,0571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - name에 이모지가 입력된 경우 400 BAD_REQUEST")
	void updateByNameWithEmoji() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect🌸");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(ErrorCode.INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - corporateNumber에 공백이 입력된 경우 200 OK")
	void updateByCorporateNumberIsContainsBlank() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF 234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(
						HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Organization 수정  - VatIdentificationNumber에 공백이 입력된 경우 200 OK")
	void updateByVatIdIsContainsBlank() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B 1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF 234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Organization 수정  - postalCode에 공백이 입력된 경우 200 OK")
	void updateByPostalCodeIsContainsBlank() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000003L);
		organizationUpdateRequestDto.setName("virnect");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("N12 8QR");
		organizationUpdateRequestDto.setVatIdentificationNumber("B 1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF 234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("PUT Organization 수정  - 존재하지 않는 State Code로 요청한 경우 404 NotFound")
	void updateWithNotFoundStateCode() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000234L);
		organizationUpdateRequestDto.setStateCode("USA");
		organizationUpdateRequestDto.setName("virnect1");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_STATE.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - State code의 Locale과 인풋의 Locale이 일치하지 않는 경우 400 BadRequest")
	void updateWithInvalidLocaleId() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000234L);
		organizationUpdateRequestDto.setStateCode("CA-BC");
		organizationUpdateRequestDto.setName("virnect1");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(
						HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_LOCALE.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - Locale이 US인데 State를 빈 값으로 요청한 경우 400 BadRequest")
	void updateWithEmptyStateCode() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000234L);
		organizationUpdateRequestDto.setName("virnect1");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - Locale이 US/CA가 아닌데 State에 값을 입력한 경우 400 BadRequest")
	void updateWithNotEmptyStateCode() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000000L);
		organizationUpdateRequestDto.setStateCode("CA-BC");
		organizationUpdateRequestDto.setName("virnect1");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("PUT Organization 수정  - Locale이 US인데 Province에 값을 입력한 경우 400 BadRequest")
	void updateWithNotEmptyProvince() throws Exception {
		//given
		OrganizationUpdateRequestDto organizationUpdateRequestDto = new OrganizationUpdateRequestDto();
		organizationUpdateRequestDto.setEmail("virnect@virnect.com");
		organizationUpdateRequestDto.setLocaleId(1000000234L);
		organizationUpdateRequestDto.setName("virnect1");
		organizationUpdateRequestDto.setAddress("10-15, Hangang-daero 7-gil");
		organizationUpdateRequestDto.setCity("Seoul");
		organizationUpdateRequestDto.setProvince("Yongsan-gu");
		organizationUpdateRequestDto.setPostalCode("04379");
		organizationUpdateRequestDto.setVatIdentificationNumber("B1948100571");
		organizationUpdateRequestDto.setCustomerType(CustomerType.LEGAL_PERSON.name());
		organizationUpdateRequestDto.setFirstName("Justin");
		organizationUpdateRequestDto.setLastName("Bieber");
		organizationUpdateRequestDto.setPhoneNumber("1012345678");
		organizationUpdateRequestDto.setCorporateNumber("SDLF234");

		// when
		String url = "/api/organizations/1000000009";
		mockMvc.perform(
				MockMvcRequestBuilders
					.put(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationRegisterTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationUpdateRequestDto))
			)
			.andDo(print())
			// then
			.andExpect(status().isBadRequest())
			.andExpect(jsonPath("$.customError").value(INVALID_INPUT_VALUE.name()));
	}

	@Test
	@DisplayName("GET my organization")
	void getMyOrganization() throws Exception {
		// when
		String url = "/api/organizations/me";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.email").value("support1@virnect.com"))
			.andExpect(jsonPath("$.vatIdentificationNumber").value("1948100551"))
			.andExpect(jsonPath("$.localeName").value("Korea, Republic of"))
			.andReturn();
	}

	@Test
	@DisplayName("GET Organization User - 200 OK")
	void getOrganizationUser() throws Exception {
		// when
		String url = "/api/organizations/1000000004/users";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("GET My Organization License - 200 OK")
	void getMyOrganizationLicense() throws Exception {
		// when
		String url = "/api/organizations/licenses/my";
		mockMvc.perform(
				MockMvcRequestBuilders
					.get(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.accept(MediaType.APPLICATION_JSON)
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("POST OrganizationContract Create - 200 OK - 서비스라이선스 상품 구매한 경우")
	void syncOrganizationContract() throws Exception {
		OrganizationContractRequestDto organizationContractRequestDto = new OrganizationContractRequestDto();
		organizationContractRequestDto.setOrganizationId(1000000002L);
		organizationContractRequestDto.setContractId(1000000005L);
		organizationContractRequestDto.setItemId(1000000007L);
		organizationContractRequestDto.setItemName("item name");
		organizationContractRequestDto.setItemType(ItemType.LICENSE);
		organizationContractRequestDto.setPaymentType(PaymentType.SUBSCRIPTION);
		organizationContractRequestDto.setRecurringInterval(RecurringIntervalType.MONTH);
		organizationContractRequestDto.setStatus(ContractStatus.PROCESSING);
		organizationContractRequestDto.setStartDate(ZonedDateTime.now());
		organizationContractRequestDto.setEndDate(ZonedDateTime.now().plusMonths(1L));

		// when
		String url = "/api/organizations/contracts";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationContractRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk())
			.andReturn();
	}

	@Test
	@DisplayName("POST OrganizationLicense Create - 200 OK - 부가라이선스 상품 구매한 경우")
	void syncOrganizationLicense_withAttributeItem() throws Exception {
		OrganizationContractRequestDto organizationContractRequestDto = new OrganizationContractRequestDto();
		organizationContractRequestDto.setOrganizationId(1000000000L);
		organizationContractRequestDto.setContractId(1000000006L);
		organizationContractRequestDto.setItemId(1000000008L);
		organizationContractRequestDto.setItemName("item name");
		organizationContractRequestDto.setItemType(ItemType.ATTRIBUTE);
		organizationContractRequestDto.setPaymentType(PaymentType.NONRECURRING);
		organizationContractRequestDto.setRecurringInterval(RecurringIntervalType.NONE);
		organizationContractRequestDto.setStatus(ContractStatus.PROCESSING);
		organizationContractRequestDto.setStartDate(ZonedDateTime.now());
		organizationContractRequestDto.setEndDate(ZonedDateTime.now());

		// when
		String url = "/api/organizations/contracts";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationContractRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST OrganizationLicense Create - 404 Error - 라이선스 이용중이지 않은 Organization 에서 부가라이선스를 구매한 경우")
	void syncOrganizationLicense_isNotFound() throws Exception {
		OrganizationContractRequestDto organizationContractRequestDto = new OrganizationContractRequestDto();
		organizationContractRequestDto.setOrganizationId(1000000002L);
		organizationContractRequestDto.setContractId(1000000006L);
		organizationContractRequestDto.setItemId(1000000008L);
		organizationContractRequestDto.setItemName("item name");
		organizationContractRequestDto.setItemType(ItemType.ATTRIBUTE);
		organizationContractRequestDto.setPaymentType(PaymentType.NONRECURRING);
		organizationContractRequestDto.setRecurringInterval(RecurringIntervalType.NONE);
		organizationContractRequestDto.setStatus(ContractStatus.PROCESSING);
		organizationContractRequestDto.setStartDate(ZonedDateTime.now());
		organizationContractRequestDto.setEndDate(ZonedDateTime.now());
		// when
		String url = "/api/organizations/contracts";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationContractRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isNotFound())
			.andExpect(jsonPath("$.customError").value(NOT_FOUND_ORGANIZATION_LICENSE.name()));
	}

	@Test
	@DisplayName("POST OrganizationLicense Create - 200 OK - 부가라이선스 상품 환불 요청의 경우")
	void syncOrganizationLicense_withAttributeCanceled() throws Exception {
		OrganizationContractRequestDto organizationContractRequestDto = new OrganizationContractRequestDto();
		organizationContractRequestDto.setOrganizationId(1000000000L);
		organizationContractRequestDto.setContractId(1000000003L);
		organizationContractRequestDto.setItemId(1000000008L);
		organizationContractRequestDto.setItemName("item name");
		organizationContractRequestDto.setItemType(ItemType.ATTRIBUTE);
		organizationContractRequestDto.setPaymentType(PaymentType.NONRECURRING);
		organizationContractRequestDto.setRecurringInterval(RecurringIntervalType.NONE);
		organizationContractRequestDto.setStatus(ContractStatus.TERMINATION);
		organizationContractRequestDto.setStartDate(ZonedDateTime.now());
		organizationContractRequestDto.setEndDate(ZonedDateTime.now());
		organizationContractRequestDto.setUsedMonth(0L);

		// when
		String url = "/api/organizations/contracts";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationContractRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST OrganizationLicense Create - 200 OK - 월구독 상품 환불 요청의 경우 ")
	void syncOrganizationLicense_withMonthlyContractTermination() throws Exception {
		OrganizationContractRequestDto organizationContractRequestDto = new OrganizationContractRequestDto();
		organizationContractRequestDto.setOrganizationId(1000000000L);
		organizationContractRequestDto.setContractId(1000000000L);
		organizationContractRequestDto.setItemId(1000000000L);
		organizationContractRequestDto.setItemName("item name");
		organizationContractRequestDto.setItemType(ItemType.LICENSE);
		organizationContractRequestDto.setPaymentType(PaymentType.SUBSCRIPTION);
		organizationContractRequestDto.setRecurringInterval(RecurringIntervalType.MONTH);
		organizationContractRequestDto.setStatus(ContractStatus.TERMINATION);
		organizationContractRequestDto.setStartDate(ZonedDateTime.now());
		organizationContractRequestDto.setEndDate(ZonedDateTime.now().plusMonths(1L));
		organizationContractRequestDto.setUsedMonth(0L);
		// when
		String url = "/api/organizations/contracts";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationContractRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}

	@Test
	@DisplayName("POST OrganizationLicense Create - 200 OK - 연구독 상품 환불 요청의 경우")
	void syncOrganizationLicense_withAnnuallyContractTermination() throws Exception {
		OrganizationContractRequestDto organizationContractRequestDto = new OrganizationContractRequestDto();
		organizationContractRequestDto.setOrganizationId(1000000000L);
		organizationContractRequestDto.setContractId(1000000000L);
		organizationContractRequestDto.setItemId(1000000000L);
		organizationContractRequestDto.setItemName("item name");
		organizationContractRequestDto.setItemType(ItemType.LICENSE);
		organizationContractRequestDto.setPaymentType(PaymentType.SUBSCRIPTION);
		organizationContractRequestDto.setRecurringInterval(RecurringIntervalType.YEAR);
		organizationContractRequestDto.setStatus(ContractStatus.TERMINATION);
		organizationContractRequestDto.setStartDate(ZonedDateTime.now().minusMonths(1));
		organizationContractRequestDto.setEndDate(ZonedDateTime.now().minusMonths(1).plusYears(1));
		organizationContractRequestDto.setUsedMonth(2L);
		// when
		String url = "/api/organizations/contracts";
		mockMvc.perform(
				MockMvcRequestBuilders
					.post(url)
					.header(HttpHeaders.AUTHORIZATION, getAuthorizationBearerToken(organizationOwnerTokenResponseDto))
					.contentType(MediaType.APPLICATION_JSON)
					.content(objectMapper.writeValueAsString(organizationContractRequestDto))
			)
			.andDo(print())

			// then
			.andExpect(status().isOk());
	}
}
