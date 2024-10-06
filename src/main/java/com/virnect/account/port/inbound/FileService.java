package com.virnect.account.port.inbound;

import org.springframework.web.multipart.MultipartFile;

import com.virnect.account.domain.enumclass.FileDirectory;

public interface FileService {

	String profileUpload(MultipartFile file, Long userId);

	String upload(MultipartFile file, FileDirectory directory);

	void deleteByUrl(String fullUrl);

	String replaceFileUrl(String deleteFileUrl, MultipartFile uploadFile, FileDirectory directory);
}
