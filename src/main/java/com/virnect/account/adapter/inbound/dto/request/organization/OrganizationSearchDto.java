package com.virnect.account.adapter.inbound.dto.request.organization;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.Email;
import javax.validation.constraints.Size;

import org.apache.commons.lang3.StringUtils;
import org.springframework.format.annotation.DateTimeFormat;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.DateRangeType;
import com.virnect.account.domain.enumclass.OrganizationStatus;
import com.virnect.account.util.ZonedDateTimeUtil;

@Setter
@Getter
@NoArgsConstructor
public class OrganizationSearchDto {
	@ApiModelProperty(value = "organization status", example = "APPROVED")
	@CommonEnum(enumClass = OrganizationStatus.class)
	private String status;

	@ApiModelProperty(value = "organization id", example = "1000000000")
	private Long organizationId;

	@ApiModelProperty(value = "Email domain", example = "gmail.com")
	@Size(max = 50)
	private String emailDomain;

	@ApiModelProperty(value = "State name", example = "Massachusetts")
	@Size(max = 100)
	private String stateName;

	@ApiModelProperty(value = "Locale name", example = "United States of America")
	@Size(max = 50)
	private String localeName;

	@ApiModelProperty(value = "Province", example = "seoul")
	@Size(max = 50)
	private String province;

	@ApiModelProperty(value = "organization name", example = "squars")
	private String organizationName;

	@ApiModelProperty(value = "contract email", example = "squars@virnect.com")
	@Email
	private String contractEmail;

	@CommonEnum(enumClass = DateRangeType.class)
	@ApiModelProperty(value = "date range type", example = "WEEK")
	private String dateRangeType;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "start date", example = "2022-04-01")
	private LocalDate startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "end date", example = "2022-04-30")
	private LocalDate endDate;

	@AssertTrue
	@ApiModelProperty(hidden = true)
	public boolean isValidDateRangeType() {
		if (dateRangeTypeValueOf().isCustom()) {
			return Objects.nonNull(startDate) && Objects.nonNull(endDate);
		}
		return Objects.isNull(startDate) && Objects.isNull(endDate);
	}

	@ApiModelProperty(hidden = true)
	public DateRangeType dateRangeTypeValueOf() {
		if (StringUtils.isBlank(dateRangeType)) {
			return DateRangeType.ALL;
		}
		return DateRangeType.valueOf(dateRangeType);
	}

	@ApiModelProperty(hidden = true)
	public ZonedDateTime getStartDate() {
		if (Objects.isNull(startDate)) {
			return null;
		}
		return ZonedDateTimeUtil.convertToZoneDateTime(startDate, LocalTime.MIN);
	}

	@ApiModelProperty(hidden = true)
	public ZonedDateTime getEndDate() {
		if (Objects.isNull(endDate)) {
			return null;
		}
		return ZonedDateTimeUtil.convertToZoneDateTime(endDate, LocalTime.MAX);
	}
}
