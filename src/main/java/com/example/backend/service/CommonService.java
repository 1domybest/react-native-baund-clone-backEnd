package com.example.backend.service;

import com.example.backend.common.exception.CustomApiException;
import com.example.backend.domain.User;
import com.example.backend.dto.ResponseMap;
import com.example.backend.dto.common.request.RequestEmailAuthCodeDto;
import com.example.backend.dto.common.response.ResponseEmailAuthCodeDto;
import com.example.backend.repository.user.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.util.Random;

@Service
@RequiredArgsConstructor
public class CommonService {
    private final JavaMailSender emailSender;

    private final BCryptPasswordEncoder bCryptPasswordEncoder;

    private final UserRepository userRepository;

    public static String getRandomString() {
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        Random random = new Random();
        String generatedString = random.ints(leftLimit, rightLimit + 1)
                .filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97))
                .limit(targetStringLength)
                .collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append)
                .toString();
        return generatedString + "!";
    }

    public void sendEmail (String to, String title,String message, boolean html) {
        try {
            MimeMessage mimeMessage = emailSender.createMimeMessage();
            MimeMessageHelper mimeMessageHelper = new MimeMessageHelper(mimeMessage, false, "UTF-8");
            mimeMessageHelper.setTo(to); // 메일 수신자
            mimeMessageHelper.setSubject(title); // 메일 제목
            mimeMessageHelper.setText(message, html); // 메일 본문 내용, HTML 여부
            emailSender.send(mimeMessage);
        }  catch (MessagingException e) {
            throw new CustomApiException(title + "전송에 실패하였습니다.\n잠시후 다시시도해주세요", HttpStatus.BAD_REQUEST);
        }
    }

    public ResponseMap sendSimpleMessage(RequestEmailAuthCodeDto requestEmailAuthCodeDto) {
        int emailAuthCode = (int)(Math.random() * (99999 - 10000 + 1)) + 10000;
        String title = "보안인증";
        String message = "<html><head></head><body><h1>보안 인증 번호입니다.</h1><h3>[ "+ emailAuthCode + " ]</h3></body></html>";
        boolean html = true;

        sendEmail(requestEmailAuthCodeDto.getTo(),title , message, html); // 이메일 전송 모듈

        ResponseEmailAuthCodeDto responseEmailAuthCodeDto = new ResponseEmailAuthCodeDto();
        responseEmailAuthCodeDto.setEmailAuthCode(String.valueOf(emailAuthCode));

        ResponseMap responseMap = new ResponseMap();
        responseMap.setHttpStatus(HttpStatus.OK);
        responseMap.setMessage("인증번호가 전송되었습니다.");
        responseMap.setData(new ResponseEntity<>(responseEmailAuthCodeDto, null, HttpStatus.OK));

        return responseMap;
    }

    @Transactional(readOnly = false)
    public ResponseMap temporaryPassword(RequestEmailAuthCodeDto requestEmailAuthCodeDto) { // 비밀번호 암호화해서 교체해야함
        String getRandomString = getRandomString();
        User user = userRepository.findByEmail(requestEmailAuthCodeDto.getTo());

        if (user == null) { // 없는 게정일 경우
            throw new CustomApiException("존재하지않는 계정입니다.\n다시한번 확인해주세요", HttpStatus.BAD_REQUEST);
        }

        if (user.getPassword() == null && user.getProvider() != null) { // 비밀번호가 존재하지않고 sns로 가입하였다면.
            throw new CustomApiException( user.getProvider() + "로 가입한 이력이있습니다. 로그인을 시도해주세요", HttpStatus.BAD_REQUEST);
        }

        String title = "임시비밀번호 발급";
        String message = "<html><head></head><body><h1>보안 인증 번호입니다.</h1><h3>[ "+ getRandomString +" ]</h3></body></html>";
        boolean html = true;
        sendEmail(requestEmailAuthCodeDto.getTo(),title , message, html); // 이메일 전송 모듈

        ResponseEmailAuthCodeDto responseEmailAuthCodeDto = new ResponseEmailAuthCodeDto();
        responseEmailAuthCodeDto.setEmailAuthCode(getRandomString);

        ResponseMap responseMap = new ResponseMap();
        responseMap.setHttpStatus(HttpStatus.OK);
        responseMap.setMessage("임시 비밀번호가 발급되었습니다.");
        responseMap.setData(new ResponseEntity<>(responseEmailAuthCodeDto, null, HttpStatus.OK));

        String encPassword = bCryptPasswordEncoder.encode(getRandomString); // 비밀번호 암호화
        user.setPassword(encPassword); // 비밀번호 임시비밀번호로 변경

        return responseMap;
    }
}
