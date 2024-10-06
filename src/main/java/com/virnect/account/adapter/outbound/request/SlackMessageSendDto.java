package com.virnect.account.adapter.outbound.request;

import java.util.List;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SlackMessageSendDto {
	private String text;
	private List<BlockMessage> blocks;

	@Getter
	@Setter
	public static class BlockMessage {
		private String type = "section";
		private SectionMessage text;

		public BlockMessage(SectionMessage text) {
			this.text = text;
		}
	}

	@Getter
	@Setter
	public static class SectionMessage {
		private String type = "mrkdwn";
		private String text;

		public SectionMessage(String text) {
			this.text = text;
		}
	}

}
