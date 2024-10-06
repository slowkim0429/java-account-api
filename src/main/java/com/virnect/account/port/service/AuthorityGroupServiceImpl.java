package com.virnect.account.port.service;

import static com.virnect.account.exception.ErrorCode.*;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;

import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupCreateRequestDto;
import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupModifyRequestDto;
import com.virnect.account.adapter.inbound.dto.request.authoritygroup.AuthorityGroupSearchDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.adapter.inbound.dto.response.authoritygroup.AuthorityGroupResponseDto;
import com.virnect.account.adapter.inbound.dto.response.authoritygroup.AuthorityGroupRevisionResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;
import com.virnect.account.domain.enumclass.MembershipStatus;
import com.virnect.account.domain.enumclass.UseStatus;
import com.virnect.account.domain.model.AuthorityGroup;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.AuthorityGroupService;
import com.virnect.account.port.outbound.AdminUserRepository;
import com.virnect.account.port.outbound.AuthorityGroupRepository;
import com.virnect.account.port.outbound.AuthorityGroupRevisionRepository;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthorityGroupServiceImpl implements AuthorityGroupService {
	private final AuthorityGroupRepository authorityGroupRepository;
	private final AuthorityGroupRevisionRepository authorityGroupRevisionRepository;
	private final AdminUserRepository adminUserRepository;

	@Override
	@Transactional(readOnly = true)
	public PageContentResponseDto<AuthorityGroupResponseDto> getAuthorityGroups(
		AuthorityGroupSearchDto authorityGroupSearchDto, Pageable pageable
	) {
		Page<AuthorityGroupResponseDto> authorityGroupResponseDtos = authorityGroupRepository.getAuthorityGroupResponses(
			authorityGroupSearchDto, pageable);
		return new PageContentResponseDto<>(authorityGroupResponseDtos, pageable);
	}

	@Override
	@Transactional(readOnly = true)
	public AuthorityGroupResponseDto getAuthorityGroupResponse(Long authorityGroupId) {
		return authorityGroupRepository.getAuthorityGroupResponse(authorityGroupId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_AUTHORITY_GROUP));
	}

	@Override
	public void create(AuthorityGroupCreateRequestDto authorityGroupCreateRequestDto) {
		authorityGroupRepository.save(AuthorityGroup.create(authorityGroupCreateRequestDto));
	}

	@Override
	public void modify(Long authorityGroupId, AuthorityGroupModifyRequestDto authorityGroupModifyRequestDto) {
		AuthorityGroup authorityGroup = authorityGroupRepository.getAuthorityGroup(authorityGroupId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_AUTHORITY_GROUP));

		authorityGroup.update(authorityGroupModifyRequestDto);
	}

	@Override
	public void validUseAuthorityGroup(Long authorityGroupId) {
		AuthorityGroup authorityGroup = authorityGroupRepository.getAuthorityGroup(authorityGroupId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_AUTHORITY_GROUP));
		if (authorityGroup.getStatus().isNotUse()) {
			throw new CustomException(INVALID_AUTHORITY_GROUP_NOT_USED);
		}
	}

	@Transactional(readOnly = true)
	@Override
	public PageContentResponseDto<AuthorityGroupRevisionResponseDto> getRevisions(
		Long authorityGroupId, Pageable pageable
	) {
		Page<AuthorityGroupRevisionResponseDto> authorityGroupRevisionResponses = authorityGroupRevisionRepository.getAuthorityGroupRevisionResponses(
			authorityGroupId, pageable);
		return new PageContentResponseDto(authorityGroupRevisionResponses, pageable);
	}

	@Override
	public void updateStatus(Long authorityGroupId, UseStatus status) {
		AuthorityGroup authorityGroup = authorityGroupRepository.getAuthorityGroup(authorityGroupId)
			.orElseThrow(() -> new CustomException(NOT_FOUND_AUTHORITY_GROUP));

		if (status.isUnuse() && isAuthorityGroupInUseByActiveAdminUser(authorityGroupId)) {
			throw new CustomException(ErrorCode.INVALID_UPDATE_USING_AUTHORITY_GROUP);
		}

		authorityGroup.updateStatus(status);
	}

	private boolean isAuthorityGroupInUseByActiveAdminUser(Long authorityGroupId) {
		return adminUserRepository.getAdminUser(MembershipStatus.JOIN, ApprovalStatus.APPROVED, authorityGroupId)
			.isPresent();
	}
}
