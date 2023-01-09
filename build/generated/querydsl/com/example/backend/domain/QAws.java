package com.example.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;


/**
 * QAws is a Querydsl query type for Aws
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QAws extends EntityPathBase<Aws> {

    private static final long serialVersionUID = -184881698L;

    public static final QAws aws = new QAws("aws");

    public final StringPath awsKey = createString("awsKey");

    public final StringPath bucket = createString("bucket");

    public final StringPath ETag = createString("ETag");

    public final StringPath fileName = createString("fileName");

    public final StringPath fileType = createString("fileType");

    public final StringPath Location = createString("Location");

    public final NumberPath<Long> no = createNumber("no", Long.class);

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public final StringPath ServerSideEncryption = createString("ServerSideEncryption");

    public QAws(String variable) {
        super(Aws.class, forVariable(variable));
    }

    public QAws(Path<? extends Aws> path) {
        super(path.getType(), path.getMetadata());
    }

    public QAws(PathMetadata metadata) {
        super(Aws.class, metadata);
    }

}

