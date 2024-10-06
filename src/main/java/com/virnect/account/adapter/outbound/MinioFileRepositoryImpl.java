package com.virnect.account.adapter.outbound;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidBucketNameException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.MinioException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;

import com.virnect.account.exception.CustomException;
import com.virnect.account.exception.ErrorCode;
import com.virnect.account.port.outbound.FileRepository;

@Component
@Profile(value = {"docker", "local", "develop", "test", "qa"})
@RequiredArgsConstructor
public class MinioFileRepositoryImpl implements FileRepository {
	private final MinioClient minioClient;

	@Value("${minio.bucket:virnect-platform}")
	private String bucket;

	@Value("${minio.prefix:}")
	private String prefixUrl;

	@Override
	public String upload(String objectKey, MultipartFile uploadFile) {
		try (InputStream inputStream = uploadFile.getInputStream()) {
			PutObjectArgs putObjectArgs = PutObjectArgs.builder()
				.contentType(uploadFile.getContentType())
				.bucket(bucket)
				.object(objectKey)
				.stream(inputStream, uploadFile.getSize(), -1)
				.build();
			minioClient.putObject(putObjectArgs);
			return addPrefixUrl(bucket, objectKey);
		} catch (MinioException | InvalidKeyException | NoSuchAlgorithmException | IOException e) {
			CustomException customException = new CustomException(
				ErrorCode.INTERNAL_SERVER_ERROR, "minio file upload error");
			customException.initCause(e);
			throw customException;
		}
	}

	private String addPrefixUrl(String bucket, String fileName) throws
		ServerException,
		InvalidBucketNameException,
		InsufficientDataException,
		ErrorResponseException,
		IOException,
		NoSuchAlgorithmException,
		InvalidKeyException,
		InvalidResponseException,
		XmlParserException,
		InternalException {
		if (StringUtils.isBlank(prefixUrl)) {
			return minioClient.getObjectUrl(bucket, fileName);
		}
		return prefixUrl + bucket + "/" + fileName;
	}

	@Override
	public void deleteByUrl(String fullUrl) {
		try {
			String objectPath = getMinioObjectPathFromFileUrl(fullUrl);
			minioClient.removeObject(RemoveObjectArgs.builder()
				.bucket(bucket)
				.object(objectPath)
				.build());
		} catch (ErrorResponseException | InsufficientDataException | InternalException | InvalidBucketNameException |
				 InvalidKeyException | InvalidResponseException | IOException | NoSuchAlgorithmException |
				 ServerException | XmlParserException e) {
			CustomException customException = new CustomException(ErrorCode.FILE_SERVER_ERROR);
			customException.initCause(e);
			throw customException;
		}
	}

	private String getMinioObjectPathFromFileUrl(String fileUrl) {
		try {
			URL url = new URL(fileUrl);
			String objectPathWithBucket = url.getPath();
			objectPathWithBucket = URLDecoder.decode(objectPathWithBucket, StandardCharsets.UTF_8);
			return objectPathWithBucket.substring(StringUtils.ordinalIndexOf(objectPathWithBucket, "/", 2) + 1);
		} catch (MalformedURLException e) {
			CustomException customException = new CustomException(ErrorCode.MALFORMED_FILE_URL_ERROR);
			customException.initCause(e);
			throw customException;
		}
	}
}
