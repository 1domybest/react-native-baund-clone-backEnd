package com.example.backend.dto.aws.request;

import com.example.backend.domain.Aws;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class RequestAwsDto {

    private String bucket;

    private String tag;

    private String location;

    private String key;

    private String serverSideEncryption;

    private String fileType;

    public Aws toEntity () {
        return Aws.builder()
                .bucket(bucket)
                .ETag(tag)
                .Location(location)
                .awsKey(key)
                .ServerSideEncryption(serverSideEncryption)
                .fileType(fileType)
                .build();
    }
}
