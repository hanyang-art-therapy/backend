package com.hanyang.arttherapy.common.aws.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.*;

import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Profile("!local")
@Configuration
public class AmazonConfig {

  @Value("${spring.cloud.aws.credentials.accessKey}")
  private String accessKey;

  @Value("${spring.cloud.aws.credentials.secretKey}")
  private String secretKey;

  @Value("${spring.cloud.aws.region.static}")
  private String region;

  @Bean
  public S3Client s3Client() {
    return S3Client.builder()
        .credentialsProvider(
            StaticCredentialsProvider.create(AwsBasicCredentials.create(accessKey, secretKey)))
        .region(Region.of(region))
        .build();
  }
}
