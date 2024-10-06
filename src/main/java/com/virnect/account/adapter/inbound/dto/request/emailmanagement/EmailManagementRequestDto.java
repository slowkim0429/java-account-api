package com.virnect.account.adapter.inbound.dto.request.emailmanagement;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.FileExtensionType;
import com.virnect.account.adapter.inbound.dto.request.validate.MultipartFileExtension;
import com.virnect.account.adapter.inbound.dto.request.validate.MultipartFileSize;
import com.virnect.account.adapter.inbound.dto.request.validate.UnitType;
import com.virnect.account.domain.enumclass.Mail;

@Setter
@Getter
@ApiModel
public class EmailManagementRequestDto {
	@ApiModelProperty(value = "Email Type", example = "WELCOME")
	@NotNull
	private Mail emailType;

	@ApiModelProperty(value = "Contents Inline Image File")
	@NotNull
	@MultipartFileSize(value = 20, unit = UnitType.MB, message = "너무 큰 파일은 등록 할 수 없습니다.(제한: 20mb)")
	@MultipartFileExtension(
		value = {FileExtensionType.JPG, FileExtensionType.PNG, FileExtensionType.JPEG},
		message = "지정된 타입 이외의 파일을 등록 할 수 없습니다.(jpg, png, jpeg)"
	)
	private MultipartFile contentsInlineImage;

	@ApiModelProperty(value = "Description", example = "sign up")
	@Size(max = 255, message = "255자까지 입력가능합니다.")
	private String description;
}

