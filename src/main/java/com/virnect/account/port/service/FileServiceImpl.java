package com.virnect.account.port.service;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang.StringUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.virnect.account.domain.enumclass.FileDirectory;
import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.FileService;
import com.virnect.account.port.outbound.FileRepository;

@Component
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {
	private static final List<String> PROFILE_IMAGE_ALLOW_EXTENSION = Arrays.asList("JPG", "JPEG", "PNG");
	private static final long MAX_USER_PROFILE_IMAGE_BYTE_SIZE = 20971520;

	private final FileRepository fileRepository;

	@Override
	public String upload(MultipartFile uploadFile, FileDirectory flag) {
		String fileExtension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());
		String fileName = getUniqueFileName(fileExtension);
		String objectKey = getObjectKey(flag, null, fileName);
		return fileRepository.upload(objectKey, uploadFile);
	}

	@Override
	public String profileUpload(MultipartFile uploadFile, Long userId) {
		String fileExtension = FilenameUtils.getExtension(uploadFile.getOriginalFilename());

		if (fileExtension == null || uploadFile.getSize() == 0) {
			throw new CustomException(ErrorCode.INVALID_IMAGE);
		}

		if (!PROFILE_IMAGE_ALLOW_EXTENSION.contains(fileExtension.toUpperCase())) {
			throw new CustomException(ErrorCode.INVALID_IMAGE, "not allowed fileExtension : " + fileExtension);
		}

		if (uploadFile.getSize() >= MAX_USER_PROFILE_IMAGE_BYTE_SIZE) {
			throw new CustomException(
				ErrorCode.INVALID_IMAGE_SIZE,
				"profile image size(" + uploadFile.getSize() + ") is bigger than " + MAX_USER_PROFILE_IMAGE_BYTE_SIZE
			);
		}

		String fileName = getUniqueFileName(fileExtension);
		String objectKey = getObjectKey(FileDirectory.USER_PROFILE, userId, fileName);

		return fileRepository.upload(objectKey, uploadFile);
	}

	@Override
	public void deleteByUrl(String fullUrl) {
		fileRepository.deleteByUrl(fullUrl);
	}

	private String getObjectKey(FileDirectory fileDirectory, Long id, String fileName) {
		return String.format("%s/%s", fileDirectory.getDirectory(id), fileName);
	}

	private String getUniqueFileName(String fileExtension) {
		return String.format("%s_%s.%s", LocalDate.now(), RandomStringUtils.randomAlphabetic(20), fileExtension);
	}

	@Override
	public String replaceFileUrl(String deleteFileUrl, MultipartFile uploadFile, FileDirectory fileDirectory) {
		String uploadFileUrl = upload(uploadFile, fileDirectory);

		if (StringUtils.isNotBlank(deleteFileUrl)) {
			deleteByUrl(deleteFileUrl);
		}
		return uploadFileUrl;
	}
}
