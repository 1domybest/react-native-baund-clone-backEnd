package com.example.backend.repository.aws;

import com.example.backend.domain.Aws;
import com.example.backend.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AwsRepository extends JpaRepository<Aws, Long> {
}
