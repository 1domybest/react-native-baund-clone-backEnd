package com.example.backend.dto.aws.response;

import com.example.backend.domain.Aws;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class ResponseAwsDto {

    private Long awsNo; // pk

    private String Bucket;

    private String ETag;

    private String Location;

    private String Key;

    private String ServerSideEncryption;

    private String fileType;

    private LocalDateTime regDate;

    public ResponseAwsDto(Aws aws) {
        this.awsNo = aws.getNo();
        this.Bucket = aws.getBucket();
        this.ETag= aws.getETag();
        this.Location = aws.getLocation();
        this.Key = aws.getAwsKey();
        this.ServerSideEncryption = aws.getServerSideEncryption();
        this.fileType = aws.getFileType();
        this.regDate = aws.getRegDate();
    }
}
