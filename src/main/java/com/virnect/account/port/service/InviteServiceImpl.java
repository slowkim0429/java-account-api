package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import java.nio.ByteBuffer;
import java.time.ZonedDateTime;
import java.util.List;

import org.apache.commons.lang.StringUtils;
import org.slf4j.MDC;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import feign.FeignException;
import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.InviteUserAssignRequestDto;
import com.virnect.account.adapter.inbound.dto.request.invite.InviteRequestDto;
import com.virnect.account.adapter.inbound.dto.request.invite.InviteSearchDto;
import com.virnect.account.adapter.inbound.dto.request.invite.WorkspaceInviteRequestDto;
import com.virnect.account.adapter.inbound.dto.response.ErrorResponse;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.TokenResponseDto;
import com.virnect.account.adapter.inbound.dto.response.invite.InviteResponseDto;
import com.virnect.account.adapter.inbound.dto.response.invite.InviteRevisionResponseDto;
import com.virnect.account.adapter.outbound.request.GroupUserByInviteRequestDto;
import com.virnect.account.adapter.outbound.request.WorkspaceUserCreateSendDto;
import com.virnect.account.domain.converter.ValueConverter;
import com.virnect.account.domain.enumclass.InviteRole;
import com.virnect.account.domain.enumclass.InviteStatus;
import com.virnect.account.domain.enumclass.InviteType;
import com.virnect.account.domain.model.Invite;
import com.virnect.account.domain.model.User;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.EmailAuthService;
import com.virnect.account.port.inbound.InviteService;
import com.virnect.account.port.inbound.SquarsApiService;
import com.virnect.account.port.inbound.UserService;
import com.virnect.account.port.inbound.WorkspaceAPIService;
import com.virnect.account.port.outbound.InviteRepository;
import com.virnect.account.port.outbound.InviteRevisionRepository;
import com.virnect.account.port.outbound.UserRepository;
import com.virnect.account.security.SecurityUtil;
import com.virnect.account.security.jwt.TokenProvider;
import com.virnect.account.util.ZonedDateTimeUtil;

@Service
@RequiredArgsConstructor
@Transactional
public class InviteServiceImpl implements InviteService {

	private final TokenProvider tokenProvider;

	private final InviteRepository inviteRepository;
	private final InviteRevisionRepository inviteRevisionRepository;

	private final EmailAuthService emailAuthService;
	private final UserRepository userRepository;
	private final WorkspaceAPIService workspaceAPIService;
	private final SquarsApiService squarsApiService;
	private final UserService userService;

	@Override
	public void createInvite(InviteRequestDto inviteRequestDto) {
		Long currentUserId = SecurityUtil.getCurrentUserId();
		String currentUserEmail = SecurityUtil.getCurrentUserEmail();
		String currentUserNickname = SecurityUtil.getCurrentUserNickname();
		String currentUserLocaleCode = SecurityUtil.getCurrentUserLocaleCode();

		Long workspaceId = inviteRequestDto.getWorkspaceId();
		Long groupId = inviteRequestDto.getGroupId();
		InviteType inviteType = inviteRequestDto.inviteTypeValueOf();
		InviteRole inviteRole = inviteRequestDto.inviteRoleValueOf();

		for (String receiverEmail : inviteRequestDto.getEmailList()) {
			String inviteToken = tokenProvider.createInviteTicket(
				receiverEmail, currentUserId, workspaceId, groupId, inviteType, inviteRole
			);

			inviteRepository.getInvite(workspaceId, groupId, receiverEmail, InviteStatus.PENDING, inviteType)
				.ifPresent(Invite::cancel);

			Invite invite = Invite.create(
				receiverEmail, tokenProvider.getExpireDateFromJwtToken(inviteToken),
				inviteRequestDto.getOrganizationId(), workspaceId, groupId, inviteRole, inviteType,
				null
			);

			inviteRepository.save(invite);

			if (InviteType.GROUP.equals(inviteType)) {
				emailAuthService.sendGroupUserInviteEmail(
					currentUserEmail, currentUserNickname, receiverEmail, inviteRequestDto.getGroupName(),
					inviteRequestDto.getProfileColor(), workspaceId, groupId, inviteToken, currentUserLocaleCode
				);
			}
		}
	}

	@Transactional
	public void assignUser(InviteUserAssignRequestDto inviteUserAssignRequestDto) {
		String inviteToken = inviteUserAssignRequestDto.getInviteToken();

		String inviteSub = tokenProvider.getUserNameFromJwtToken(inviteToken);

		if (!tokenProvider.validateToken(inviteToken)) {
			throw new CustomException(INVALID_INVITE_TOKEN);
		}

		if (InviteType.WORKSPACE.name().equals(inviteSub)) {
			assignToWorkspaceUser(inviteToken);
		} else if (InviteType.GROUP.name().equals(inviteSub)) {
			assignToWorkspaceUserByGroupInvite(inviteToken);
			assignToGroupUser(inviteToken);
		} else {
			throw new CustomException(INVALID_INVITE_TOKEN);
		}
	}

	private void assignToWorkspaceUserByGroupInvite(String inviteToken) {
		String invitedUserEmail = tokenProvider.getUserEmailFromJwtToken(inviteToken);

		User invitedUser = userRepository.getUser(invitedUserEmail)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		Long workspaceId = tokenProvider.getWorkspaceIdFromToken(inviteToken);

		createWorkspaceUserByInvite(workspaceId, invitedUser);
	}

	private void createWorkspaceUserByInvite(Long workspaceId, User invitedUser) {
		WorkspaceUserCreateSendDto workspaceUserCreateSendDto = WorkspaceUserCreateSendDto.of(
			workspaceId,
			invitedUser.getId(),
			InviteRole.ROLE_WORKSPACE_USER
		);

		TokenResponseDto tokenResponseDto = tokenProvider.createToken(invitedUser.getEmail());
		String authorizationHeaderValue = tokenProvider.createAuthorizationHeaderValue(tokenResponseDto);

		try {
			workspaceAPIService.createWorkspaceUserByInvite(
				workspaceUserCreateSendDto,
				authorizationHeaderValue
			);
		} catch (FeignException e) {
			if (HttpStatus.BAD_REQUEST.value() == e.status()) {
				MDC.put("thirdPartyStackTrace", ValueConverter.getStackTraceString(e));
				ByteBuffer responseBody = e.responseBody().orElseThrow(() -> new CustomException(FEIGN_SERVER_ERROR));
				this.wrappingWorkspaceCustomError(new String(responseBody.array()));
			}
		}
	}

	public void assignToGroupUser(String inviteToken) {
		String inviteSubRole = tokenProvider.getInviteRoleFromJwtToken(inviteToken);

		if (!InviteRole.ROLE_GROUP_USER.name().equals(inviteSubRole)) {
			throw new CustomException(INVALID_INVITE_TOKEN);
		}

		String receiverEmail = tokenProvider.getUserEmailFromJwtToken(inviteToken);

		User receiver = userRepository.getUser(receiverEmail)
			.orElseThrow(() -> new CustomException(NOT_FOUND_USER));

		Long workspaceId = tokenProvider.getWorkspaceIdFromToken(inviteToken);
		Long groupId = tokenProvider.getGroupIdFromJwtToken(inviteToken);

		Invite currentInvite = inviteRepository.getInvite(
				workspaceId, groupId, receiverEmail, InviteStatus.PENDING, InviteType.GROUP)
			.orElseThrow(() -> new CustomException(INVALID_INVITE_TOKEN));

		if (currentInvite.isExpired()) {
			throw new CustomException(INVALID_INVITE_TOKEN);
		}

		Long sendUserId = currentInvite.getCreatedBy();

		this.createGroupUser(
			workspaceId, groupId, receiver, sendUserId, InviteRole.valueOf(inviteSubRole), receiverEmail);

		currentInvite.done();
	}

	private void createGroupUser(
		Long workspaceId, Long groupId, User inviteUser, Long sendUserId, InviteRole role, String receiverEmail
	) {
		GroupUserByInviteRequestDto groupUserByInviteRequestDto = GroupUserByInviteRequestDto.of(
			workspaceId, groupId, inviteUser, sendUserId, role
		);

		TokenResponseDto tokenResponseDto = tokenProvider.createToken(receiverEmail);
		String authorizationHeaderValue = tokenProvider.createAuthorizationHeaderValue(tokenResponseDto);

		try {
			squarsApiService.createGroupUser(groupUserByInviteRequestDto, authorizationHeaderValue);
		} catch (FeignException e) {
			if (HttpStatus.BAD_REQUEST.value() == e.status()) {
				MDC.put("thirdPartyStackTrace", ValueConverter.getStackTraceString(e));
				ByteBuffer responseBody = e.responseBody().orElseThrow(() -> new CustomException(FEIGN_SERVER_ERROR));
				this.wrappingWorkspaceCustomError(new String(responseBody.array()));
			}
		}
	}

	private void wrappingWorkspaceCustomError(String responseBody) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			ErrorResponse errorResponse = mapper.readValue(responseBody, ErrorResponse.class);

			if (StringUtils.isNotBlank(errorResponse.getCustomError())) {
				ErrorCode errorCode = valueOf(errorResponse.getCustomError());
				throw new CustomException(errorCode);
			}
		} catch (JsonProcessingException e) {
			CustomException customException = new CustomException(INTERNAL_SERVER_ERROR);
			customException.initCause(e);
			throw customException;
		} catch (IllegalArgumentException e) {
			CustomException customException = new CustomException(FEIGN_SERVER_ERROR);
			customException.initCause(e);
			throw customException;
		}
	}

	private void assignToWorkspaceUser(String inviteToken) {
		String inviteSubRole = tokenProvider.getInviteRoleFromJwtToken(inviteToken);

		if (!InviteRole.ROLE_WORKSPACE_USER.name().equals(inviteSubRole)) {
			throw new CustomException(INVALID_INVITE_TOKEN);
		}

		String receiverEmail = tokenProvider.getUserEmailFromJwtToken(inviteToken);
		User receiver = userService.getUserByEmail(receiverEmail);
		Long workspaceId = tokenProvider.getWorkspaceIdFromToken(inviteToken);

		TokenResponseDto tokenResponseDto = tokenProvider.createToken(receiverEmail);
		String authorizationHeaderValue = tokenProvider.createAuthorizationHeaderValue(tokenResponseDto);

		Invite currentInvite = inviteRepository.getInvite(
				workspaceId, null, receiverEmail, InviteStatus.PENDING, InviteType.WORKSPACE)
			.orElseThrow(() -> new CustomException(INVALID_INVITE_TOKEN));

		if (currentInvite.isExpired()) {
			throw new CustomException(INVALID_INVITE_TOKEN);
		}

		this.createWorkspaceUser(workspaceId, receiver, inviteSubRole, authorizationHeaderValue);

		currentInvite.done();
	}

	private void createWorkspaceUser(
		Long workspaceId, User receiver, String inviteSubRole, String authorizationToken
	) {
		WorkspaceUserCreateSendDto requestDto = WorkspaceUserCreateSendDto.of(
			workspaceId, receiver.getId(), InviteRole.valueOf(inviteSubRole));

		try {
			workspaceAPIService.createWorkspaceUser(requestDto, authorizationToken);
		} catch (FeignException e) {
			if (HttpStatus.BAD_REQUEST.value() == e.status()) {
				ByteBuffer responseBody = e.responseBody().orElseThrow(() -> new CustomException(FEIGN_SERVER_ERROR));
				this.wrappingWorkspaceCustomError(new String(responseBody.array()));
			}
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<InviteResponseDto> getInviteOfWorkspaceType(Long workspaceId, Pageable pageable) {
		Long currentUserOrganizationId = SecurityUtil.getCurrentUserOrganizationId();

		Page<InviteResponseDto> inviteResponseDtos = inviteRepository.getInviteResponses(
			currentUserOrganizationId, workspaceId, InviteType.WORKSPACE, pageable);

		for (InviteResponseDto inviteResponseDto : inviteResponseDtos) {
			if (isExpired(inviteResponseDto.getInviteStatus(), inviteResponseDto.getExpiredDate())) {
				inviteResponseDto.expired();
			}
		}

		return new PageContentResponseDto<>(inviteResponseDtos, pageable);
	}

	@Override
	public void cancelInvite(Long inviteId) {
		Long currentUserOrganizationId = SecurityUtil.getCurrentUserOrganizationId();

		Invite invite = inviteRepository.getInvite(inviteId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_INVITATION));

		if (!invite.getOrganizationId().equals(currentUserOrganizationId)) {
			throw new CustomException(INVALID_AUTHORIZATION);
		}

		if (!InviteStatus.PENDING.equals(invite.getInviteStatus()) ||
			ZonedDateTimeUtil.zoneOffsetOfUTC().isAfter(invite.getExpireDate())) {
			throw new CustomException(NOT_FOUND_INVITATION);
		}

		invite.cancel();
	}

	@Override
	public void createWorkspaceInvite(WorkspaceInviteRequestDto workspaceInviteRequestDto) {
		inviteRepository.getWorkspaceInvite(workspaceInviteRequestDto.getWorkspaceInviteId())
			.ifPresentOrElse(invite -> {
				invite.setInviteStatus(workspaceInviteRequestDto.getInviteStatus());
			}, () -> {
				Invite newInvite = Invite.from(workspaceInviteRequestDto);
				newInvite.setInviteStatus(workspaceInviteRequestDto.getInviteStatus());
				inviteRepository.save(newInvite);
			});

	}

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<InviteResponseDto> getInvites(InviteSearchDto inviteSearchDto, Pageable pageable) {
		Page<InviteResponseDto> inviteResponses = inviteRepository.getInviteResponses(inviteSearchDto, pageable);

		for (InviteResponseDto inviteResponseDto : inviteResponses) {
			if (isExpired(inviteResponseDto.getInviteStatus(), inviteResponseDto.getExpiredDate())) {
				inviteResponseDto.expired();
			}
		}
		return new PageContentResponseDto<>(inviteResponses, pageable);
	}

	private boolean isExpired(InviteStatus inviteStatus, String expiredDateString) {
		ZonedDateTime expiredDate = ZonedDateTimeUtil.convertZonedDateTime(expiredDateString);
		return InviteStatus.PENDING.equals(inviteStatus) && ZonedDateTimeUtil.zoneOffsetOfUTC().isAfter(expiredDate);
	}

	@Override
	@Transactional(readOnly = true)
	public InviteResponseDto getInvite(Long inviteId) {
		InviteResponseDto inviteResponseDto = inviteRepository.getInviteResponse(inviteId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_INVITATION));

		if (isExpired(inviteResponseDto.getInviteStatus(), inviteResponseDto.getExpiredDate())) {
			inviteResponseDto.expired();
		}

		return inviteResponseDto;
	}

	@Override
	public List<InviteRevisionResponseDto> getInviteRevisions(Long inviteId) {
		return inviteRevisionRepository.getInviteRevisions(inviteId);
	}
}
