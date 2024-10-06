package com.virnect.account.adapter.inbound.dto.request.user;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModelProperty;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import com.virnect.account.adapter.inbound.dto.request.validate.AcceptOrRejectSubset;
import com.virnect.account.adapter.inbound.dto.request.validate.CommonEnum;
import com.virnect.account.domain.enumclass.AcceptOrReject;
import com.virnect.account.log.NoLogging;

@Getter
@Setter
@NoLogging
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class UserCreateRequestDto {
	@ApiModelProperty(value = "이메일", required = true, example = "test@virnect.com")
	@NotBlank(message = "이메일은 반드시 입력되어야 합니다.")
	@Email
	private String email;

	@ApiModelProperty(value = "비밀번호", required = true, example = "!2dfksdklWa")
	@NotBlank(message = "비밀번호는 반드시 입력되어야 합니다.")
	@Pattern(regexp = "^(?=.*[A-Za-z])(?=.*\\d)(?=.*[,./!@#$%])[A-Za-z\\d,./!@#$%]{8,20}$")
	private String password;

	@ApiModelProperty(value = "사용 지역 코드", required = true, example = "US")
	@NotBlank(message = "사용 지역은 반드시 입력되어야 합니다.")
	private String localeCode;

	@ApiModelProperty(value = "마케팅 수집 정보 동의", required = true, example = "REJECT")
	@NotBlank(message = "마케팅 수집 정보 여부는 반드시 입력되어야 합니다.")
	@CommonEnum(enumClass = AcceptOrReject.class)
	private String marketInfoReceive;

	@ApiModelProperty(value = "개인 정보 처리 방침", required = true, example = "ACCEPT")
	@NotBlank(message = "개인 정보 처리 방침에 대한 여부는 반드시 입력되어야 합니다.")
	@AcceptOrRejectSubset(anyOf = {AcceptOrReject.ACCEPT})
	private String privacyPolicy;

	@ApiModelProperty(value = "서비스 이용 약관", required = true, example = "ACCEPT")
	@NotBlank(message = "서비스 이용 약관에 대한 여부는 반드시 입력되어야 합니다.")
	@AcceptOrRejectSubset(anyOf = {AcceptOrReject.ACCEPT})
	private String termsOfService;

	@ApiModelProperty(value = "사용자 유입 경로", example = "https://www.naver.com/")
	@Size(max = 255)
	private String referrerUrl;

	@ApiModelProperty(value = "인증 코드", required = true, example = "108631")
	@NotBlank
	@Pattern(regexp = "[0-9]{6}")
	private String authCode;

	@ApiModelProperty(value = "초대 토큰")
	private String inviteToken;

	private UserCreateRequestDto(
		String email, String password, String localeCode, String marketInfoReceive,
		String privacyPolicy, String termsOfService, String authCode, String inviteToken
	) {
		this.email = email;
		this.password = password;
		this.localeCode = localeCode;
		this.marketInfoReceive = marketInfoReceive;
		this.privacyPolicy = privacyPolicy;
		this.termsOfService = termsOfService;
		this.authCode = authCode;
		this.inviteToken = inviteToken;
	}

	public static UserCreateRequestDto of(
		String email, String password, String localeCode, String marketInfoReceive,
		String privacyPolicy, String termsOfService, String authCode, String inviteToken
	) {
		return new UserCreateRequestDto(
			email, password, localeCode, marketInfoReceive, privacyPolicy, termsOfService,
			authCode, inviteToken
		);
	}

	@ApiModelProperty(hidden = true)
	public AcceptOrReject marketInfoReceiveValueOf() {
		return AcceptOrReject.valueOf(this.marketInfoReceive);
	}

	@ApiModelProperty(hidden = true)
	public AcceptOrReject privacyPolicyValueOf() {
		return AcceptOrReject.valueOf(this.privacyPolicy);
	}

	@ApiModelProperty(hidden = true)
	public AcceptOrReject termsOfServiceValueOf() {
		return AcceptOrReject.valueOf(this.termsOfService);
	}

	public String getLowerCaseEmail() {
		return email.toLowerCase();
	}
}
