package com.virnect.account.port.inbound;

import org.springframework.web.multipart.MultipartFile;

public interface UpdateGuideFileService {

	String mediaUpload(MultipartFile file);
}
