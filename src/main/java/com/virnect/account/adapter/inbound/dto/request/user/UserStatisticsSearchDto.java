package com.virnect.account.adapter.inbound.dto.request.user;

import java.time.LocalDate;
import java.time.LocalTime;
import java.time.ZonedDateTime;
import java.util.Objects;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;

import org.springframework.format.annotation.DateTimeFormat;

import io.micrometer.core.instrument.util.StringUtils;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.DateRangeType;
import com.virnect.account.util.ZonedDateTimeUtil;

@Getter
@Setter
public class UserStatisticsSearchDto {
	@ApiModelProperty(value = "Date range type", example = "WEEK", required = true)
	@CommonEnum(enumClass = DateRangeType.class)
	@NotBlank
	private String dateRangeType;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "start date", example = "2023-04-01")
	private LocalDate startDate;

	@DateTimeFormat(pattern = "yyyy-MM-dd")
	@ApiModelProperty(value = "end date", example = "2023-04-01")
	private LocalDate endDate;

	@AssertTrue
	@ApiModelProperty(hidden = true)
	public boolean isValidDateRangeType() {
		if (StringUtils.isBlank(dateRangeType)) {
			return false;
		}

		if (dateRangeTypeValueOf().isCustom()) {
			return Objects.nonNull(startDate) && Objects.nonNull(endDate);
		}
		return Objects.isNull(startDate) && Objects.isNull(endDate);
	}

	@ApiModelProperty(hidden = true)
	public DateRangeType dateRangeTypeValueOf() {
		return DateRangeType.valueOf(this.dateRangeType);
	}

	@ApiModelProperty(hidden = true)
	public ZonedDateTime getStartDate() {
		if (Objects.isNull(this.startDate)) {
			return null;
		}
		return ZonedDateTimeUtil.convertToZoneDateTime(this.startDate, LocalTime.MIN);
	}

	@ApiModelProperty(hidden = true)
	public ZonedDateTime getEndDate() {
		if (Objects.isNull(this.endDate)) {
			return null;
		}
		return ZonedDateTimeUtil.convertToZoneDateTime(this.endDate, LocalTime.MAX);
	}
}
