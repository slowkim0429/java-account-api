package com.virnect.account.port.service;

import static org.hibernate.type.descriptor.java.JdbcTimestampTypeDescriptor.*;

import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;

import javax.validation.constraints.Size;

import org.apache.commons.io.FilenameUtils;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import lombok.RequiredArgsConstructor;

import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.inbound.UpdateGuideFileService;
import com.virnect.account.port.outbound.FileRepository;
import com.virnect.account.util.ZonedDateTimeUtil;

@Component
@RequiredArgsConstructor
public class UpdateGuideFileServiceImpl implements UpdateGuideFileService {
	private static final List<String> ALLOWED_MEDIA_EXTENSION = Arrays.asList(
		"JPG", "JPEG", "PNG", "GIF", "AVI", "MP4", "MOV");
	private static final long MAX_MEDIA_SIZE = 20 * 1024 * 1024; // 20mb

	@Size
	private final FileRepository fileRepository;

	@Override
	public String mediaUpload(MultipartFile file) {
		String fileExtension = FilenameUtils.getExtension(file.getOriginalFilename());

		if (fileExtension == null || file.getSize() == 0) {
			throw new CustomException(ErrorCode.INVALID_MEDIA);
		}

		if (!ALLOWED_MEDIA_EXTENSION.contains(fileExtension.toUpperCase())) {
			throw new CustomException(ErrorCode.INVALID_MEDIA, "not allowed fileExtension : " + fileExtension);
		}

		if (file.getSize() > MAX_MEDIA_SIZE) {
			throw new CustomException(
				ErrorCode.INVALID_MEDIA_SIZE,
				"Update guide media(image or video) size(" + file.getSize() + ") is bigger than " + MAX_MEDIA_SIZE
			);
		}

		String fileName = createUploadFileName(fileExtension);
		String objectPath = getObjectPath(fileName);

		return fileRepository.upload(objectPath, file);
	}

	private String getObjectPath(String fileName) {
		return String.join("/", "service", "update-guide-media", fileName);
	}

	private String createUploadFileName(String originFilename) {
		return ZonedDateTimeUtil.zoneOffsetOfUTC().format(DateTimeFormatter.ofPattern(TIMESTAMP_FORMAT)) + "_" +
			RandomStringUtils.randomAlphanumeric(10) + "." + FilenameUtils.getExtension(
			originFilename);
	}
}
