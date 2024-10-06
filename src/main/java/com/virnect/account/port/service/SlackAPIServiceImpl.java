package com.virnect.account.port.service;

import java.util.List;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Service;
import org.thymeleaf.util.StringUtils;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.outbound.request.SlackMessageSendDto;
import com.virnect.account.domain.enumclass.OriginType;
import com.virnect.account.domain.model.ErrorLog;
import com.virnect.account.port.inbound.SlackAPIService;
import com.virnect.account.port.outbound.SlackAPIRepository;

@Service
@RequiredArgsConstructor
@Profile(value = {"production"})
public class SlackAPIServiceImpl implements SlackAPIService {
	private final SlackAPIRepository slackAPIRepository;

	@Value("${spring.profiles.active}")
	private String environment;

	@Value("${slack.monitoring-url}")
	private String monitoringUrl;

	@Override
	public void create(ErrorLog errorLog) {
		if (!isRequiredErrorNotification(errorLog.getOriginType(), errorLog.getResponseStatus())) {
			return;
		}

		String headMessage = String.format("*[%s] Error log : %s*", environment, errorLog.getService());
		SlackMessageSendDto.SectionMessage headSectionMessage = new SlackMessageSendDto.SectionMessage(headMessage);
		SlackMessageSendDto.BlockMessage headBlockMessage = new SlackMessageSendDto.BlockMessage(headSectionMessage);

		String errorMonitoringUrl = monitoringUrl + "/" + errorLog.getId();

		String bodyMessage = String.format(
			"ID : <%s|%s>" + System.lineSeparator()
				+ "Origin type : %s" + System.lineSeparator()
				+ "Client service name : %s" + System.lineSeparator()
				+ "Method name : %s" + System.lineSeparator()
				+ "Method : %s" + System.lineSeparator()
				+ "URL : %s" + System.lineSeparator()
				+ "Response status : %s" + System.lineSeparator()
				+ "Created by (User ID) : %s" + System.lineSeparator()
				+ "Created date : %s",
			errorMonitoringUrl, errorLog.getId(), errorLog.getOriginType().name(), errorLog.getClientServiceName(),
			errorLog.getMethodName(),
			errorLog.getMethod(), errorLog.getUrl(), errorLog.getResponseStatus(), errorLog.getCreatedBy(),
			errorLog.getCreatedDate()
		);

		SlackMessageSendDto.SectionMessage bodySectionMessage = new SlackMessageSendDto.SectionMessage(bodyMessage);
		SlackMessageSendDto.BlockMessage bodyBlockMessage = new SlackMessageSendDto.BlockMessage(bodySectionMessage);

		SlackMessageSendDto slackMessageSendDto = new SlackMessageSendDto();
		slackMessageSendDto.setText(headMessage);
		slackMessageSendDto.setBlocks(List.of(headBlockMessage, bodyBlockMessage));

		slackAPIRepository.create(slackMessageSendDto);
	}

	private boolean isRequiredErrorNotification(OriginType errorOriginType, int responseStatus) {
		if (OriginType.SERVER.equals(errorOriginType)) {
			if (is5xxServerError(responseStatus) || isInternalFeignRequestError(responseStatus))
				return true;
		}

		if (OriginType.CLIENT.equals(errorOriginType) && responseStatus == 0) {
			return true;
		}

		return false;
	}

	private boolean isInternalFeignRequestError(int responseStatus) {
		return responseStatus == 0;
	}

	private boolean is5xxServerError(int responseStatus) {
		return StringUtils.startsWith(responseStatus, "5");
	}

}
