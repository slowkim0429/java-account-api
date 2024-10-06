package com.virnect.account.adapter.inbound.dto.request.user;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.validation.constraints.AssertTrue;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.organization.OrganizationSearchDto;
import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.AcceptOrReject;
import com.virnect.account.domain.enumclass.DateRangeType;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@Setter
@Getter
@NoArgsConstructor
public class UserSearchDto {

	@ApiModelProperty(value = "유저 id")
	private Long id;

	@ApiModelProperty(value = "검색 대상 사용자 이메일")
	private String email;

	@ApiModelProperty(value = "검색 대상 사용자 이메일 도메인")
	private String emailDomain;

	@ApiModelProperty(value = "광고 수신 동의 여부")
	@CommonEnum(enumClass = AcceptOrReject.class)
	private String marketInfoReceive;

	@ApiModelProperty(value = "허브스팟 id")
	private Long hubspotContactId;

	@ApiModelProperty(value = "검색 대상 사용자 닉네임")
	private String nickname;

	@ApiModelProperty(value = "organization Id")
	private Long organizationId;

	@ApiModelProperty(value = "organization Status")
	private UseStatus organizationStatus = UseStatus.NONE;

	@ApiModelProperty(value = "검색 대상 지역 id")
	private Long localeId;

	@ApiModelProperty(value = "Locale name")
	private String localeName;

	@ApiModelProperty(value = "검색 대상 리전 id")
	private Long regionId;

	@ApiModelProperty(value = "검색 대상 사용자 유입 경로")
	private String referrerUrl;

	@ApiModelProperty(value = "Region code")
	private String regionCode;

	@ApiModelProperty(value = "membership Status")
	private MembershipStatus status;

	@ApiModelProperty(value = "Date range type", example = "WEEK", required = true)
	@CommonEnum(enumClass = DateRangeType.class)
	private String dateRangeType;

	@ApiModelProperty(value = "start date", example = "2022-04-01")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate startDateOfCreatedDate;

	@ApiModelProperty(value = "end date", example = "2022-04-30")
	@DateTimeFormat(pattern = "yyyy-MM-dd")
	private LocalDate endDateOfCreatedDate;

	@Builder(builderClassName = "OrganizationUserSearchBuilder", builderMethodName = "organizationUserSearchBuilder")
	public UserSearchDto(Long organizationId, OrganizationSearchDto organizationSearchDto) {
		this.organizationId = organizationId;
	}

	public static UserSearchDto of(OrganizationUserSearchDto organizationUserSearchDto) {
		UserSearchDto userSearchDto = new UserSearchDto();
		userSearchDto.setOrganizationId(organizationUserSearchDto.getOrganizationId());
		userSearchDto.setEmail(organizationUserSearchDto.getEmail());
		userSearchDto.setNickname(organizationUserSearchDto.getNickname());
		return userSearchDto;
	}

	@AssertTrue
	@ApiModelProperty(hidden = true)
	public boolean isValidDateRangeType() {
		if (dateRangeTypeValueOf().isCustom()) {
			return Objects.nonNull(startDateOfCreatedDate) && Objects.nonNull(endDateOfCreatedDate);
		}
		return Objects.isNull(startDateOfCreatedDate) && Objects.isNull(endDateOfCreatedDate);
	}

	@ApiModelProperty(hidden = true)
	public DateRangeType dateRangeTypeValueOf() {
		if (StringUtils.isBlank(dateRangeType)) {
			return DateRangeType.ALL;
		}
		return DateRangeType.valueOf(this.dateRangeType);
	}

	@ApiModelProperty(hidden = true)
	public ZonedDateTime getStartDateOfCreatedDate() {
		if (Objects.isNull(this.startDateOfCreatedDate)) {
			return null;
		}
		return ZonedDateTimeUtil.convertToZoneDateTime(this.startDateOfCreatedDate, LocalTime.MIN);
	}

	@ApiModelProperty(hidden = true)
	public ZonedDateTime getEndDateOfCreatedDate() {
		if (Objects.isNull(this.endDateOfCreatedDate)) {
			return null;
		}
		return ZonedDateTimeUtil.convertToZoneDateTime(this.endDateOfCreatedDate, LocalTime.MAX);
	}

	public boolean isValid() {
		return StringUtils.isNotEmpty(email)
			|| StringUtils.isNotEmpty(nickname)
			|| (organizationId != null && organizationId > 0);
	}

	public String isValidMessage() {
		return "email, nickname, organizationId 중 한개 이상을 입력해 주세요";
	}

	public boolean isValidOfOrganizationUser() {
		return (organizationId != null && organizationId > 0);
	}

	public String isValidMessageOfOrganizationUser() {
		return "organizationId를 입력해 주세요";
	}
}
