package com.example.backend.dto.user.request;

import com.example.backend.domain.User;
import com.example.backend.dto.aws.request.RequestAwsDto;
import lombok.Data;
import org.springframework.lang.Nullable;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class RequestUserRegisterDto {

    @NotBlank(message = "이름을 입력해주세요")
    private String userName; // 이름

    @NotBlank(message = "닉네임 입력해주세요")
    private String nickName; // 이름

    @NotBlank(message = "이메일을 입력해주세요.")
    @Email(message = "정확한 이메일을 입력해주세요")
    private String email; // 이메일

    @Nullable
    private String provider; // 로그인한 sns 브랜드명 예) google

    @NotNull(message = "비밀번호를 입력해주세요.")
    private String password;

    @Nullable
    private String providerId; // 로그인한 sns의 회원 고유번호 예)asdAKSDJjwndjicIAI2314

    @Nullable
    private String phone;

    @Nullable
    private RequestAwsDto profileImageFile;

    @Nullable
    private RequestAwsDto backgroundImageFile;

    public User toEntity () {
        return User.builder()
                .userName(userName)
                .email(email)
                .nickName(nickName)
                .provider(provider)
                .providerId(providerId)
                .phone(phone)
                .build();
    }
}

