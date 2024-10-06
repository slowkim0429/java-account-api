package com.virnect.account.adapter.outbound.request;

import com.fasterxml.jackson.annotation.JsonProperty;

import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import com.virnect.account.domain.enumclass.AcceptOrReject;

@Getter
@NoArgsConstructor
public class ContactRequestDto {
	@JsonProperty(value = "email")
	private String email;

	@JsonProperty(value = "firstname")
	private String nickName;

	@JsonProperty(value = "country")
	private String localeCode;

	@JsonProperty(value = "lifecyclestage")
	private String lifeCycleStage = "customer";

	@JsonProperty(value = "hs_content_membership_status")
	private String membershipStatus = "active";

	@JsonProperty(value = "hs_legal_basis")
	private String legalBasis = "Legitimate interest – existing customer";

	@JsonProperty(value = "marketing_info_subscription")
	private String marketInfoReceive;

	@Builder
	public ContactRequestDto(String email, String nickname, String localeCode, AcceptOrReject marketInfoReceive) {
		this.email = email;
		this.nickName = nickname;
		this.localeCode = localeCode;
		this.marketInfoReceive = marketInfoReceive.name().toLowerCase();
	}

	@Override
	public String toString() {
		return "ContactRequestDto{" +
			"email='" + email + '\'' +
			", nickName='" + nickName + '\'' +
			", localeCode='" + localeCode + '\'' +
			", lifeCycleStage='" + lifeCycleStage + '\'' +
			", membershipStatus='" + membershipStatus + '\'' +
			", legalBasis='" + legalBasis + '\'' +
			", marketInfoReceive='" + marketInfoReceive + '\'' +
			'}';
	}
}
