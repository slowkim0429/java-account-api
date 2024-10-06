package com.virnect.account.port.outbound;

import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import com.virnect.account.adapter.outbound.request.SlackMessageSendDto;

@FeignClient(name = "slack-api", url = "${slack.webhook-url:https://hooks.slack.com}", primary = false)
public interface SlackAPIRepository {
	@RequestMapping(method = RequestMethod.POST)
	void create(
		@RequestBody SlackMessageSendDto slackMessageSendDto
	);

}
