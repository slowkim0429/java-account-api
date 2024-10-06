package com.virnect.account.adapter.inbound.dto.request.eventpopup;

import javax.validation.constraints.AssertTrue;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.adapter.inbound.dto.request.validate.FileExtensionType;
import com.virnect.account.adapter.inbound.dto.request.validate.MultipartFileExtension;
import com.virnect.account.adapter.inbound.dto.request.validate.MultipartFileSize;
import com.virnect.account.adapter.inbound.dto.request.validate.UnitType;
import com.virnect.account.domain.enumclass.DataType;
import com.virnect.account.domain.enumclass.ExposureOptionType;

@ApiModel
@Getter
@Setter
public class EventPopupUpdateRequestDto {

	@ApiModelProperty(value = "이름", required = true)
	@NotBlank
	private String name;

	@ApiModelProperty(value = "이미지 파일")
	@MultipartFileSize(value = 20, unit = UnitType.MB, message = "너무 큰 파일은 등록 할 수 없습니다.(제한: 20mb)")
	@MultipartFileExtension(
		value = {FileExtensionType.GIF, FileExtensionType.JPG, FileExtensionType.PNG, FileExtensionType.JPEG},
		message = "지정된 타입 이외의 파일을 등록 할 수 없습니다.(gif, jpg, png, jpeg)"
	)
	private MultipartFile image;

	@ApiModelProperty(value = "이미지 클릭 링크 url")
	private String imageLinkUrl;

	@ApiModelProperty(value = "컨텐츠 설명")
	private String contentDescription;

	@ApiModelProperty(value = "버튼 라벨")
	private String buttonLabel;

	@ApiModelProperty(value = "버튼 클릭 링크 url")
	private String buttonUrl;

	@ApiModelProperty(value = "노출 옵션 타입", required = true)
	@CommonEnum(enumClass = ExposureOptionType.class)
	@NotBlank
	private String exposureOptionType;

	@ApiModelProperty(value = "노출 옵션 데이터 타입")
	@CommonEnum(enumClass = DataType.class)
	private String exposureOptionDataType;

	@ApiModelProperty(value = "노출 옵션 값")
	@Size(max = 2)
	private String exposureOptionValue;

	@ApiModelProperty(value = "입력 가이드(email placeholder)")
	private String inputGuide;

	@ApiModelProperty(value = "이메일 타이틀")
	private String emailTitle;

	@ApiModelProperty(value = "이메일 내부 컨텐츠 이미지")
	@MultipartFileSize(value = 20, unit = UnitType.MB, message = "너무 큰 파일은 등록 할 수 없습니다.(제한: 20mb)")
	@MultipartFileExtension(
		value = {FileExtensionType.GIF, FileExtensionType.JPG, FileExtensionType.PNG, FileExtensionType.JPEG},
		message = "지정된 타입 이외의 파일을 등록 할 수 없습니다.(gif, jpg, png, jpeg)"
	)
	private MultipartFile emailContentInlineImage;

	@AssertTrue(message = "ExposureOption 이 지정 된 경우, 값이 반드시 입력 되어야 합니다.")
	@ApiModelProperty(hidden = true)
	public boolean isValidExposureOptionValue() {
		if (getExposureOptionType().isSelectiveDeactivationDay()) {
			if (getExposureOptionDataType() == null) {
				return false;
			}
			if (this.exposureOptionValue == null || this.exposureOptionValue.isBlank()) {
				return false;
			}
		}
		return true;
	}

	@AssertTrue(message = "ExposureOption 의 지정된 데이터 타입 이외의 타입은 사용할 수 없습니다")
	@ApiModelProperty(hidden = true)
	public boolean isValidExposureOptionType() {
		if (getExposureOptionType().isSelectiveDeactivationDay()) {
			if (getExposureOptionDataType() == null || !getExposureOptionDataType().isNumber()) {
				return false;
			}
		}
		return true;
	}

	public boolean hasEmailContentInlineImage() {
		return this.emailContentInlineImage != null;
	}

	public DataType getExposureOptionDataType() {
		if (null == this.exposureOptionDataType || this.exposureOptionDataType.isBlank()) {
			return null;
		}
		return DataType.valueOf(this.exposureOptionDataType);
	}

	public ExposureOptionType getExposureOptionType() {
		if (null == this.exposureOptionType || this.exposureOptionType.isBlank()) {
			return null;
		}
		return ExposureOptionType.valueOf(this.exposureOptionType);
	}
}
