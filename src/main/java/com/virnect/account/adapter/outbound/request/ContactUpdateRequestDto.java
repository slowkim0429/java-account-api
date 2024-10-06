package com.virnect.account.adapter.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.AcceptOrReject;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.model.User;

@Getter
@NoArgsConstructor
public class ContactUpdateRequestDto {
	@JsonProperty(value = "firstname")
	private String nickName;

	@JsonProperty(value = "country")
	private String localeCode;

	@JsonProperty(value = "lifecyclestage")
	private String lifeCycleStage;

	@JsonProperty(value = "hs_content_membership_status")
	private String membershipStatus;

	@JsonProperty(value = "hs_legal_basis")
	private String legalBasis;

	@JsonProperty(value = "marketing_info_subscription")
	private String marketInfoReceive;

	@JsonProperty(value = "last_login")
	private Long loginDate;

	private ContactUpdateRequestDto(String nickName, String membershipStatus, String marketInfoReceive, String localeCode) {
		this.nickName = nickName;
		this.membershipStatus = membershipStatus;
		this.marketInfoReceive = marketInfoReceive;
		this.localeCode = localeCode;
	}

	public static ContactUpdateRequestDto of(
		String nickname, MembershipStatus membershipStatus, AcceptOrReject marketInfoReceive, String localeCode
	) {
		return new ContactUpdateRequestDto(
			nickname,
			MembershipStatus.JOIN.equals(membershipStatus) ? "active" : "inactive",
			marketInfoReceive.name().toLowerCase(),
			localeCode
		);
	}

	public static ContactUpdateRequestDto from(long loginDate) {
		ContactUpdateRequestDto contactUpdateRequestDto = new ContactUpdateRequestDto();
		contactUpdateRequestDto.loginDate = loginDate;
		return contactUpdateRequestDto;
	}

	public static ContactUpdateRequestDto from(User user) {
		ContactUpdateRequestDto contactUpdateRequestDto = new ContactUpdateRequestDto();
		contactUpdateRequestDto.nickName = user.getNickname();
		contactUpdateRequestDto.localeCode = user.getLocaleCode();
		contactUpdateRequestDto.lifeCycleStage = "customer";
		contactUpdateRequestDto.membershipStatus = "active";
		contactUpdateRequestDto.legalBasis = "Legitimate interest – existing customer";
		contactUpdateRequestDto.marketInfoReceive = user.getMarketInfoReceive().name().toLowerCase();
		return contactUpdateRequestDto;
	}

	@Override
	public String toString() {
		return "ContactUpdateRequestDto{" +
			"nickName='" + nickName + '\'' +
			", localeCode='" + localeCode + '\'' +
			", lifeCycleStage='" + lifeCycleStage + '\'' +
			", membershipStatus='" + membershipStatus + '\'' +
			", legalBasis='" + legalBasis + '\'' +
			", marketInfoReceive='" + marketInfoReceive + '\'' +
			", loginDate=" + loginDate +
			'}';
	}
}
