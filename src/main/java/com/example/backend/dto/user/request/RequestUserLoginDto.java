package com.example.backend.dto.user.request;

import lombok.Data;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotNull;

@Data
public class RequestUserLoginDto {

    @NotNull(message = "이메일을 입력해주세요")
    @Email(message = "정확한 이메일을 입력해주세요")
    private String email;

    @NotNull(message = "비밀번호를 입력해주세요")
    private String password;
}
