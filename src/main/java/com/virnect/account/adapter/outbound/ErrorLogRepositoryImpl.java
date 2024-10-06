package com.virnect.account.adapter.outbound;

import static com.virnect.account.domain.model.QErrorLog.*;

import java.util.Optional;

import org.apache.commons.lang.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Repository;

import com.querydsl.core.QueryResults;
import com.querydsl.core.types.dsl.BooleanExpression;
import com.querydsl.jpa.impl.JPAQueryFactory;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.error.ErrorLogSearchDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorLogDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorLogResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QErrorLogDetailResponseDto;
import com.virnect.account.adapter.inbound.dto.response.QErrorLogResponseDto;
import com.virnect.account.domain.enumclass.OriginType;
import com.virnect.account.port.outbound.ErrorLogRepositoryCustom;

@Repository
@RequiredArgsConstructor
public class ErrorLogRepositoryImpl implements ErrorLogRepositoryCustom {

	private final JPAQueryFactory queryFactory;

	@Override
	public Optional<ErrorLogDetailResponseDto> getErrorLogResponse(Long errorLogId) {
		ErrorLogDetailResponseDto responseDto = queryFactory
			.select(new QErrorLogDetailResponseDto(
				errorLog.id,
				errorLog.createdDate,
				errorLog.updatedDate,
				errorLog.createdBy,
				errorLog.lastModifiedBy,
				errorLog.authToken,
				errorLog.controller,
				errorLog.device,
				errorLog.elapsedTime,
				errorLog.header,
				errorLog.method,
				errorLog.methodName,
				errorLog.queryString,
				errorLog.requestBody,
				errorLog.responseBody,
				errorLog.responseStatus,
				errorLog.service,
				errorLog.clientServiceName,
				errorLog.stackTrace,
				errorLog.thirdPartyStackTrace,
				errorLog.url,
				errorLog.originType
			))
			.from(errorLog)
			.where(errorLog.id.eq(errorLogId))
			.fetchOne();
		return Optional.ofNullable(responseDto);
	}

	@Override
	public Page<ErrorLogResponseDto> getErrorLogResponses(ErrorLogSearchDto errorLogSearchDto, Pageable pageable) {
		QueryResults<ErrorLogResponseDto> results = queryFactory
			.select(new QErrorLogResponseDto(
				errorLog.id,
				errorLog.createdDate,
				errorLog.createdBy,
				errorLog.controller,
				errorLog.device,
				errorLog.elapsedTime,
				errorLog.method,
				errorLog.methodName,
				errorLog.queryString,
				errorLog.responseStatus,
				errorLog.service,
				errorLog.url,
				errorLog.clientServiceName,
				errorLog.originType
			))
			.from(errorLog)
			.where(
				eqResponseStatus(errorLogSearchDto.getResponseStatus()),
				eqMethod(errorLogSearchDto.getMethod()),
				eqCreatedBy(errorLogSearchDto.getCreatedBy()),
				eqUrl(errorLogSearchDto.getUrl()),
				eqMethodName(errorLogSearchDto.getMethodName()),
				eqOriginType(errorLogSearchDto.originTypeValueOf())
			)
			.orderBy(errorLog.id.desc())
			.offset(pageable.getOffset())
			.limit(pageable.getPageSize())
			.fetchResults();

		return new PageImpl<>(results.getResults(), pageable, results.getTotal());
	}

	private BooleanExpression eqResponseStatus(Integer responseStatus) {
		if (responseStatus == null) {
			return null;
		}

		return errorLog.responseStatus.eq(responseStatus);
	}

	private BooleanExpression eqUrl(String url) {
		if (StringUtils.isBlank(url)) {
			return null;
		}

		return errorLog.url.eq(url);
	}

	private BooleanExpression eqMethod(HttpMethod method) {
		if (method == null) {
			return null;
		}

		return errorLog.method.eq(method);
	}

	private BooleanExpression eqMethodName(String methodName) {
		if (StringUtils.isBlank(methodName)) {
			return null;
		}

		return errorLog.methodName.eq(methodName);
	}

	private BooleanExpression eqCreatedBy(Long createdBy) {
		if (createdBy == null) {
			return null;
		}

		return errorLog.createdBy.eq(createdBy);
	}

	private BooleanExpression eqOriginType(OriginType originType) {
		if (originType == null) {
			return null;
		}
		return errorLog.originType.eq(originType);
	}
}
