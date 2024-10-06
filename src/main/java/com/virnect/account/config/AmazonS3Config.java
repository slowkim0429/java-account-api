package com.virnect.account.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;

import com.amazonaws.auth.AWSCredentialsProvider;
import com.amazonaws.auth.AWSStaticCredentialsProvider;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3ClientBuilder;

@Profile({"staging", "production", "local"})
@Configuration
public class AmazonS3Config {

	@Value("${cloud.aws.credentials.access-key:none}")
	private String accessKey;

	@Value("${cloud.aws.credentials.secret-key:none}")
	private String secretKey;

	@Value("${cloud.aws.region:ap-northeast-2}")
	private String awsRegion;

	@Bean
	public BasicAWSCredentials basicAWSCredentials() {
		return new BasicAWSCredentials(accessKey, secretKey);
	}

	@Bean
	public AWSCredentialsProvider awsCredentialsProvider() {
		return new AWSStaticCredentialsProvider(basicAWSCredentials());
	}

	@Bean
	public AmazonS3 amazonS3Client(AWSCredentialsProvider awsCredentialsProvider) {
		return AmazonS3ClientBuilder.standard()
			.withCredentials(awsCredentialsProvider)
			.withRegion(awsRegion)
			.build();
	}
}