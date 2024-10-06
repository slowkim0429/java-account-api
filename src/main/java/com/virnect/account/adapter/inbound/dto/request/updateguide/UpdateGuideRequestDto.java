package com.virnect.account.adapter.inbound.dto.request.updateguide;

import java.text.SimpleDateFormat;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.hibernate.validator.constraints.Length;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.FileType;
import com.virnect.account.domain.enumclass.ServiceType;

@ApiModel
@Getter
@NoArgsConstructor
public class UpdateGuideRequestDto {

	@ApiModelProperty(name = "name", required = true)
	@NotBlank(message = "name 은 null 혹은 공백 일 수 없습니다.")
	@Size(max = 50, message = "name 의 길이는 50 을 넘을 수 없습니다")
	private String name;

	@ApiModelProperty(name = "제목", required = true)
	@NotBlank(message = "제목 은 null 혹은 공백 일 수 없습니다.")
	@Size(max = 50, message = "제목 의 길이는 50 을 넘을 수 없습니다")
	private String title;

	@ApiModelProperty(name = "보조제목")
	@Size(max = 50, message = "보조제목 의 길이는 50 을 넘을 수 없습니다")
	private String subTitle;

	@ApiModelProperty(name = "설명", required = true)
	@NotBlank(message = "설명 은 null 혹은 공백 일 수 없습니다.")
	private String description;

	@ApiModelProperty(name = "보조설명")
	private String subDescription;

	@ApiModelProperty(name = "서비스 타입", required = true)
	@CommonEnum(enumClass = ServiceType.class, message = "ServiceType 에 포함되어있지 않은 값입니다.")
	private String serviceType;

	@ApiModelProperty(name = "파일 타입(IMAGE, VIDEO)", required = true)
	@CommonEnum(enumClass = FileType.class, message = "ServiceType 에 포함되어있지 않은 값입니다.")
	private String fileType;

	@ApiModelProperty(name = "fileUrl", required = true)
	@Length(max = 250, message = "url 길이가 너무 깁니다.")
	private String fileUrl;

	@ApiModelProperty(name = "업데이트 날짜", example = "2023-08-11 05:44:13", required = true)
	@NotNull(message = "업데이트 날짜는 반드시 입력 되어야 합니다.")
	private String dateByUpdate;

	@AssertTrue(message = "날짜(yyyy-MM-dd) 형식으로 입력되어야 합니다")
	@ApiModelProperty(hidden = true)
	protected boolean isValidDateByUpdate() {
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("yyyy-MM-dd");
		simpleDateFormat.setLenient(false);
		try {
			simpleDateFormat.parse(this.dateByUpdate);
			return true;
		} catch (Exception e) {
			return false;
		}
	}

	@Builder
	public UpdateGuideRequestDto(
		String name, String title, String subTitle, String description, String subDescription, String serviceType,
		String fileType, String fileUrl, String dateByUpdate
	) {
		this.name = name;
		this.title = title;
		this.subTitle = subTitle;
		this.description = description;
		this.subDescription = subDescription;
		this.serviceType = serviceType;
		this.fileType = fileType;
		this.fileUrl = fileUrl;
		this.dateByUpdate = dateByUpdate;
	}

	public ServiceType valueOfServiceType() {
		return ServiceType.valueOf(this.serviceType);
	}

	public FileType valueOfFileType() {
		return FileType.valueOf(this.fileType);
	}
}
