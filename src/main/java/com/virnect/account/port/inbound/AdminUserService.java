package com.virnect.account.port.inbound;

import org.springframework.data.domain.Pageable;

import com.virnect.account.adapter.inbound.dto.request.PasswordVerificationRequestDto;
import com.virnect.account.adapter.inbound.dto.request.UpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.request.adminuser.AdminUserAuthorityGroupRequestDto;
import com.virnect.account.adapter.inbound.dto.request.user.AdminUserSearchDto;
import com.virnect.account.adapter.inbound.dto.request.user.UserUpdatePasswordRequestDto;
import com.virnect.account.adapter.inbound.dto.response.AdminUserResponseDto;
import com.virnect.account.adapter.inbound.dto.response.AdminUserRevisionResponseDto;
import com.virnect.account.adapter.inbound.dto.response.PageContentResponseDto;
import com.virnect.account.domain.enumclass.ApprovalStatus;

public interface AdminUserService {
	PageContentResponseDto<AdminUserResponseDto> getAdminUsers(
		AdminUserSearchDto adminUserSearchDto, Pageable pageable
	);

	void updateApprovalStatus(Long adminUserId, ApprovalStatus status);

	AdminUserResponseDto getCurrentAdminUser();

	AdminUserResponseDto getAdminUserResponse(Long adminUserId);

	void updatePasswordWithoutAuthentication(UpdatePasswordRequestDto updatePasswordRequestDto);

	void updatePassword(UserUpdatePasswordRequestDto updatePasswordRequestDto);

	void resign(PasswordVerificationRequestDto passwordVerificationRequestDto);

	void resignByAdminMaster(Long adminUserId);

	PageContentResponseDto<AdminUserRevisionResponseDto> getAdminUserRevisions(Long adminUserId, Pageable pageable);

	void updateAuthorityGroup(Long adminUserid, AdminUserAuthorityGroupRequestDto adminUserAuthorityGroupRequestDto);
}
