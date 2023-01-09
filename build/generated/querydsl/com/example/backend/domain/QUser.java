package com.example.backend.domain;

import static com.querydsl.core.types.PathMetadataFactory.*;

import com.querydsl.core.types.dsl.*;

import com.querydsl.core.types.PathMetadata;
import javax.annotation.processing.Generated;
import com.querydsl.core.types.Path;
import com.querydsl.core.types.dsl.PathInits;


/**
 * QUser is a Querydsl query type for User
 */
@Generated("com.querydsl.codegen.DefaultEntitySerializer")
public class QUser extends EntityPathBase<User> {

    private static final long serialVersionUID = -1435773686L;

    private static final PathInits INITS = PathInits.DIRECT2;

    public static final QUser user = new QUser("user");

    public final QAws backgroundImageFile;

    public final StringPath email = createString("email");

    public final StringPath nickName = createString("nickName");

    public final NumberPath<Long> no = createNumber("no", Long.class);

    public final StringPath phone = createString("phone");

    public final QAws profileImageFile;

    public final StringPath provider = createString("provider");

    public final StringPath providerId = createString("providerId");

    public final StringPath refreshToken = createString("refreshToken");

    public final DateTimePath<java.time.LocalDateTime> regDate = createDateTime("regDate", java.time.LocalDateTime.class);

    public final StringPath role = createString("role");

    public final NumberPath<Integer> state = createNumber("state", Integer.class);

    public final StringPath userName = createString("userName");

    public QUser(String variable) {
        this(User.class, forVariable(variable), INITS);
    }

    public QUser(Path<? extends User> path) {
        this(path.getType(), path.getMetadata(), PathInits.getFor(path.getMetadata(), INITS));
    }

    public QUser(PathMetadata metadata) {
        this(metadata, PathInits.getFor(metadata, INITS));
    }

    public QUser(PathMetadata metadata, PathInits inits) {
        this(User.class, metadata, inits);
    }

    public QUser(Class<? extends User> type, PathMetadata metadata, PathInits inits) {
        super(type, metadata, inits);
        this.backgroundImageFile = inits.isInitialized("backgroundImageFile") ? new QAws(forProperty("backgroundImageFile")) : null;
        this.profileImageFile = inits.isInitialized("profileImageFile") ? new QAws(forProperty("profileImageFile")) : null;
    }

}

