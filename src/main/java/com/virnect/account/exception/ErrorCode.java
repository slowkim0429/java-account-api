package com.virnect.account.exception;

import org.springframework.http.HttpStatus;

import lombok.Getter;

@Getter
public enum ErrorCode {
	/* 400 BAD_REQUEST : 잘못된 요청 */
	INVALID_INPUT_VALUE(HttpStatus.BAD_REQUEST),
	INVALID_STATUS(HttpStatus.BAD_REQUEST),
	INVALID_DISALLOWED_EMAIL(HttpStatus.BAD_REQUEST),
	INVALID_EMAIL_AUTH_CODE(HttpStatus.BAD_REQUEST),
	INVALID_IMAGE(HttpStatus.BAD_REQUEST),
	INVALID_MEDIA(HttpStatus.BAD_REQUEST),
	INVALID_IMAGE_SIZE(HttpStatus.BAD_REQUEST),
	INVALID_MEDIA_SIZE(HttpStatus.BAD_REQUEST),
	INVALID_ORGANIZATION(HttpStatus.BAD_REQUEST),
	INVALID_PRODUCT(HttpStatus.BAD_REQUEST),
	INVALID_LICENSE(HttpStatus.BAD_REQUEST),
	INVALID_LICENSE_GRADE(HttpStatus.BAD_REQUEST),
	INVALID_ITEM_TYPE(HttpStatus.BAD_REQUEST),
	INVALID_RECURRING_INTERVAL_TYPE(HttpStatus.BAD_REQUEST),
	INVALID_OTHER_ORGANIZATION_USER(HttpStatus.BAD_REQUEST),
	INVALID_ORGANIZATION_OWNER_COUNT(HttpStatus.BAD_REQUEST),
	INVALID_LOCALE(HttpStatus.BAD_REQUEST),
	INVALID_MONTHLY_USED_AMOUNT(HttpStatus.BAD_REQUEST),
	INVALID_LICENSE_ATTRIBUTE(HttpStatus.BAD_REQUEST),
	INVALID_INVITE_TOKEN(HttpStatus.BAD_REQUEST),
	INVALID_PASSWORD_MATCHING(HttpStatus.BAD_REQUEST),
	INVALID_FREE_LICENSE_EXPOSED(HttpStatus.BAD_REQUEST),
	INVALID_ENTERPRISE_IS_EXPOSE(HttpStatus.BAD_REQUEST),
	INVALID_ITEM_STATUS(HttpStatus.BAD_REQUEST),
	INVALID_ITEM_EXPOSED(HttpStatus.BAD_REQUEST),
	INVALID_ITEM(HttpStatus.BAD_REQUEST),
	INVALID_EVENT_POPUP_COUPON_NOT_APPROVED(HttpStatus.BAD_REQUEST),
	INVALID_EVENT_POPUP_COUPON_NOT_USED(HttpStatus.BAD_REQUEST),
	INVALID_EVENT_POPUP_MODIFY(HttpStatus.BAD_REQUEST),
	INVALID_MOBILE_MANAGEMENT_NOT_MATCHED_PASSWORD(HttpStatus.BAD_REQUEST),
	INVALID_AUTHORITY_GROUP_NOT_USED(HttpStatus.BAD_REQUEST),
	INVALID_ADMIN_USER_STATUS(HttpStatus.BAD_REQUEST),

	INVALID_COUNT_OF_GROUP_USER(HttpStatus.BAD_REQUEST),
	INVALID_AMOUNT_IS_LESS_THAN_OR_EQUAL_TO_MONTHLY_USED_AMOUNT(HttpStatus.BAD_REQUEST),
	INVALID_LICENSE_GRADE_TYPE(HttpStatus.BAD_REQUEST),
	INVALID_EXPIRED_DATE(HttpStatus.BAD_REQUEST),
	INVALID_BENEFIT_OPTION(HttpStatus.BAD_REQUEST),
	INVALID_PERIOD_BENEFIT_VALUE(HttpStatus.BAD_REQUEST),
	INVALID_TARGET_OF_SYNCHRONIZATION(HttpStatus.BAD_REQUEST),
	INVALID_SYNCABLE(HttpStatus.BAD_REQUEST),
	INVALID_UPDATE_USING_AUTHORITY_GROUP(HttpStatus.BAD_REQUEST),

	/* 401 UNAUTHORIZED : 인증되지 않은 사용자 */
	INVALID_USER(HttpStatus.UNAUTHORIZED),
	INVALID_AUTHENTICATION(HttpStatus.UNAUTHORIZED),
	INVALID_TOKEN(HttpStatus.UNAUTHORIZED),

	/* 403 FORBIDDEN : 인가되지 않은 사용자 */
	INVALID_AUTHORIZATION(HttpStatus.FORBIDDEN),
	INVALID_ROLE(HttpStatus.FORBIDDEN),

	/* 404 NOT_FOUND : Resource 를 찾을 수 없음 */
	NOT_FOUND(HttpStatus.NOT_FOUND),
	NOT_FOUND_USER(HttpStatus.NOT_FOUND),
	NOT_FOUND_ORGANIZATION(HttpStatus.NOT_FOUND),
	NOT_FOUND_PRODUCT(HttpStatus.NOT_FOUND),
	NOT_FOUND_LICENSE_GRADE(HttpStatus.NOT_FOUND),
	NOT_FOUND_LICENSE(HttpStatus.NOT_FOUND),
	NOT_FOUND_STATE(HttpStatus.NOT_FOUND),
	NOT_FOUND_LOCALE(HttpStatus.NOT_FOUND),
	NOT_FOUND_REGION(HttpStatus.NOT_FOUND),
	NOT_FOUND_DOMAIN(HttpStatus.NOT_FOUND),
	NOT_FOUND_ORGANIZATION_LICENSE(HttpStatus.NOT_FOUND),
	NOT_FOUND_ORGANIZATION_ADDITIONAL_LICENSE(HttpStatus.NOT_FOUND),
	NOT_FOUND_ITEM(HttpStatus.NOT_FOUND),
	NOT_FOUND_LICENSE_ATTRIBUTE(HttpStatus.NOT_FOUND),
	NOT_FOUND_ITEM_PAYMENT_LINK(HttpStatus.NOT_FOUND),
	NOT_FOUND_ATTRIBUTE_ITEM(HttpStatus.NOT_FOUND),
	NOT_FOUND_ADMIN_USER(HttpStatus.NOT_FOUND),
	NOT_FOUND_ORGANIZATION_CONTRACT(HttpStatus.NOT_FOUND),
	NOT_FOUND_INVITATION(HttpStatus.NOT_FOUND),
	NOT_FOUND_UPDATE_GUIDE(HttpStatus.NOT_FOUND),
	NOT_FOUND_COUPON(HttpStatus.NOT_FOUND),
	NOT_FOUND_EVENT_POPUP(HttpStatus.NOT_FOUND),
	NOT_FOUND_EVENT_POPUP_COUPON(HttpStatus.NOT_FOUND),
	NOT_FOUND_MOBILE_MANAGEMENT(HttpStatus.NOT_FOUND),
	NOT_FOUND_MOBILE_FORCE_UPDATE_MINIMUM_VERSION(HttpStatus.NOT_FOUND),
	NOT_FOUND_EMAIL_CUSTOMIZING_MANAGEMENT(HttpStatus.NOT_FOUND),
	NOT_FOUND_HUBSPOT_COMPANY(HttpStatus.NOT_FOUND),
	NOT_FOUND_ORGANIZATION_LICENSE_KEY(HttpStatus.NOT_FOUND),
	NOT_FOUND_EXTERNAL_SERVICE_MAPPING(HttpStatus.NOT_FOUND),
	NOT_FOUND_SERVICE_TIME_ZONE(HttpStatus.NOT_FOUND),
	NOT_FOUND_AUTHORITY_GROUP(HttpStatus.NOT_FOUND),

	/* 406 METHOD_NOT_ALLOWED: method not allowed */
	METHOD_NOT_ALLOWED(HttpStatus.METHOD_NOT_ALLOWED),

	/* 409 CONFLICT : Resource 의 현재 상태와 충돌. 보통 중복된 데이터 존재 */
	DUPLICATE_USER(HttpStatus.CONFLICT),
	DUPLICATE_ADMIN_USER(HttpStatus.CONFLICT),
	DUPLICATE_ORGANIZATION(HttpStatus.CONFLICT),
	DUPLICATE_ORGANIZATION_LICENSE(HttpStatus.CONFLICT),
	DUPLICATE_PRODUCT(HttpStatus.CONFLICT),
	DUPLICATE_LICENSE(HttpStatus.CONFLICT),
	DUPLICATE_LICENSE_GRADE_NAME(HttpStatus.CONFLICT),
	DUPLICATE_LICENSE_ATTRIBUTE(HttpStatus.CONFLICT),
	DUPLICATE_LOGIN(HttpStatus.CONFLICT),
	DUPLICATE_LOGIN_SESSION(HttpStatus.CONFLICT),
	DUPLICATE_PASSWORD(HttpStatus.CONFLICT),
	DUPLICATE_ORGANIZATION_BUSINESS_REGISTRATION_NUMBER(HttpStatus.CONFLICT),
	DUPLICATE_ATTRIBUTE_ITEM(HttpStatus.CONFLICT),
	DUPLICATE_APPLIED_USER(HttpStatus.CONFLICT),
	DUPLICATE_COUPON_FOR_ITEM(HttpStatus.CONFLICT),
	DUPLICATE_COUPON_CODE(HttpStatus.CONFLICT),

	/* 410 GONE : 서버는 요청한 리소스가 영구적으로 삭제 */
	EXPIRED_EMAIL_SESSION_CODE(HttpStatus.GONE),

	/* 500 INTERNAL_SERVER_ERROR */
	INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

	MALFORMED_FILE_URL_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

	FILE_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

	FEIGN_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR),

	MAIL_SEND_ERROR(HttpStatus.INTERNAL_SERVER_ERROR);

	private final HttpStatus httpStatus;

	ErrorCode(HttpStatus httpStatus) {
		this.httpStatus = httpStatus;
	}
}
