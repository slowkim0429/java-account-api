package com.virnect.account.adapter.outbound;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import com.amazonaws.SdkClientException;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.CannedAccessControlList;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.amazonaws.services.s3.model.PutObjectRequest;

import lombok.RequiredArgsConstructor;

import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.FileRepository;

@Profile({"staging", "production"})
@Component
@RequiredArgsConstructor
public class AmazonS3FileRepositoryImpl implements FileRepository {
	private final AmazonS3 amazonS3Client;
	@Value("${cloud.aws.s3.bucket.name:virnect-platform}")
	private String bucketName;

	@Value("${cloud.aws.s3.prefix}")
	private String cdnUrl;

	@Override
	public String upload(String objectKey, MultipartFile uploadFile) {

		ObjectMetadata objectMetadata = new ObjectMetadata();
		objectMetadata.setContentType(uploadFile.getContentType());
		objectMetadata.setContentLength(uploadFile.getSize());
		objectMetadata.setContentDisposition("inline");

		try (InputStream inputStream = uploadFile.getInputStream()) {
			PutObjectRequest putObjectRequest = new PutObjectRequest(
				bucketName, objectKey, inputStream, objectMetadata);

			amazonS3Client.putObject(putObjectRequest.withCannedAcl(CannedAccessControlList.Private));
			return cdnUrl + objectKey;
		} catch (SdkClientException | IOException e) {
			CustomException customException = new CustomException(
				ErrorCode.INTERNAL_SERVER_ERROR, "s3 file upload error");
			customException.initCause(e);
			throw customException;
		}
	}

	@Override
	public void deleteByUrl(String fullUrl) {
		try {
			URL url = new URL(fullUrl);
			String objectPath = url.getPath();
			objectPath = URLDecoder.decode(objectPath, StandardCharsets.UTF_8);
			if (objectPath.startsWith("/"))
				objectPath = url.getPath().substring(1);
			amazonS3Client.deleteObject(bucketName, objectPath);
		} catch (MalformedURLException e) {
			throw new CustomException(ErrorCode.MALFORMED_FILE_URL_ERROR);
		}
	}
}
