package com.example.backend.domain;

import lombok.*;
import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@AllArgsConstructor
@Getter
@Setter
@Builder
@Table(name = "aws")
public class Aws {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "awsNo")
    private Long no; // pk

    private String bucket;

    private String ETag;

    @Column(length = 1000)
    private String Location;

    private String awsKey;

    private String fileName;

    private String fileType;

    private String ServerSideEncryption;

    private LocalDateTime regDate;

    @PrePersist
    public void regDate() {
        this.regDate = LocalDateTime.now();
    }

}

