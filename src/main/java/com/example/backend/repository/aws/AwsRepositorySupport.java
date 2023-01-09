package com.example.backend.repository.aws;

import com.example.backend.domain.Aws;
import com.example.backend.domain.User;
import com.querydsl.jpa.impl.JPAQueryFactory;
import org.springframework.data.jpa.repository.support.QuerydslRepositorySupport;
import org.springframework.stereotype.Repository;

import static com.example.backend.domain.QAws.aws;

@Repository
public class AwsRepositorySupport extends QuerydslRepositorySupport {

    private final JPAQueryFactory jpaQueryFactory;

    /**
     * Creates a new {@link QuerydslRepositorySupport} instance for the given domain type.
     *
     * @param jpaQueryFactory
     */
    public AwsRepositorySupport(JPAQueryFactory jpaQueryFactory) {
        super(Aws.class);
        this.jpaQueryFactory = jpaQueryFactory;
    }


    public Aws findByNo (Long no) {
        return jpaQueryFactory.select(aws)
                .from(aws)
                .where(aws.no.eq(no))
                .fetchOne();
    }
}
