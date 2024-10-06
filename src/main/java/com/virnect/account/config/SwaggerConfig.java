package com.virnect.account.config;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;

import com.fasterxml.jackson.databind.ObjectMapper;

import lombok.RequiredArgsConstructor;
import springfox.documentation.builders.ApiInfoBuilder;
import springfox.documentation.builders.PathSelectors;
import springfox.documentation.builders.RequestHandlerSelectors;
import springfox.documentation.schema.Example;
import springfox.documentation.service.ApiInfo;
import springfox.documentation.service.ApiKey;
import springfox.documentation.service.AuthorizationScope;
import springfox.documentation.service.Response;
import springfox.documentation.service.SecurityReference;
import springfox.documentation.spi.DocumentationType;
import springfox.documentation.spi.service.contexts.SecurityContext;
import springfox.documentation.spring.web.plugins.Docket;

import com.virnect.account.adapter.inbound.dto.response.ErrorResponse;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;

@Configuration
@RequiredArgsConstructor
public class SwaggerConfig {
	private static final String VERSION = "v1.0.0";
	private static final String PATH_URI = "/api/**";
	private static final String TITLE = "Account API";
	private static final String DESCRIPTION = "Account, User, Product, License";
	private static final String BASE_PACKAGE = "com.virnect.account.adapter.inbound.controller";
	private final ObjectMapper objectMapper;

	@Bean
	public Docket api() {
		return new Docket(DocumentationType.OAS_30).useDefaultResponseMessages(false)
			.select()
			.apis(RequestHandlerSelectors.basePackage(BASE_PACKAGE))
			.paths(PathSelectors.ant(PATH_URI))
			.build()

			.globalResponses(HttpMethod.GET, getGlobalErrorMessages())
			.globalResponses(HttpMethod.POST, getGlobalErrorMessages())
			.globalResponses(HttpMethod.PUT, getGlobalErrorMessages())
			.globalResponses(HttpMethod.DELETE, getGlobalErrorMessages())
			.consumes(getConsumeContentTypes())
			.produces(getProduceContentTypes())
			.apiInfo(apiInfo())
			.directModelSubstitute(Locale.class, String.class)
			.securityContexts(Collections.singletonList(securityContext()))
			.securitySchemes(Collections.singletonList(apiKey()));
	}

	private List<Response> getGlobalErrorMessages() {
		List<Response> responses = new ArrayList<>();
		Map<Integer, List<Example>> responseExamplesMap = new HashMap<>();
		for (ErrorCode errorCode : ErrorCode.values()) {
			try {
				Integer statusCode = errorCode.getHttpStatus().value();
				String body = objectMapper.writeValueAsString(ErrorResponse.toResponseEntity(errorCode).getBody());
				if (!responseExamplesMap.containsKey(statusCode)) {
					List<Example> examples = new ArrayList<>();
					Example example = getErrorResponseExample(errorCode, body);
					examples.add(example);
					responseExamplesMap.put(statusCode, examples);
					continue;
				}
				responseExamplesMap.get(statusCode).add(getErrorResponseExample(errorCode, body));
			} catch (Exception e) {
				throw new CustomException(ErrorCode.INTERNAL_SERVER_ERROR, e.getMessage());
			}
		}

		for (ErrorCode errorCode : ErrorCode.values()) {
			responses.add(
				new Response(String.valueOf(errorCode.getHttpStatus().value()), errorCode.getHttpStatus().name(), true,
					Collections.emptyList(), Collections.emptyList(),
					responseExamplesMap.get(errorCode.getHttpStatus().value()), Collections.emptyList()
				));
		}
		return responses;
	}

	private Example getErrorResponseExample(ErrorCode errorCode, String body) {
		return new Example(errorCode.name(), "", errorCode.name(), body, "", MediaType.APPLICATION_JSON_VALUE);
	}

	private Set<String> getConsumeContentTypes() {
		Set<String> consumes = new HashSet<>();
		consumes.add(MediaType.APPLICATION_JSON_VALUE);
		consumes.add(MediaType.APPLICATION_FORM_URLENCODED_VALUE);
		return consumes;
	}

	private Set<String> getProduceContentTypes() {
		Set<String> produces = new HashSet<>();
		produces.add(MediaType.APPLICATION_JSON_VALUE);
		return produces;
	}

	private SecurityContext securityContext() {
		return SecurityContext.builder()
			.securityReferences(defaultAuth())
			.build();
	}

	private List<SecurityReference> defaultAuth() {
		AuthorizationScope authorizationScope = new AuthorizationScope("global", "accessEverything");
		AuthorizationScope[] authorizationScopes = new AuthorizationScope[1];
		authorizationScopes[0] = authorizationScope;
		return Collections.singletonList(new SecurityReference(HttpHeaders.AUTHORIZATION, authorizationScopes));
	}

	private ApiKey apiKey() {
		return new ApiKey(HttpHeaders.AUTHORIZATION, HttpHeaders.AUTHORIZATION, "header");
	}

	private ApiInfo apiInfo() {
		return new ApiInfoBuilder()
			.title(TITLE)
			.description(DESCRIPTION)
			.version(VERSION)
			.build();
	}
}