package com.virnect.account.adapter.inbound.dto.request.user;

import javax.validation.constraints.NotNull;

import org.springframework.web.multipart.MultipartFile;

import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class UserUpdateImageRequestDto {
	@ApiModelProperty(value = "프로필 이미지", required = true)
	@NotNull(message = "프로필 이미지는 반드시 입력되어야 합니다.")
	private MultipartFile profileImage;
}
