package com.virnect.account.port.outbound;

import org.springframework.web.multipart.MultipartFile;

public interface FileRepository {
	String upload(String objectKey, MultipartFile uploadFile);

	void deleteByUrl(String url);
}
