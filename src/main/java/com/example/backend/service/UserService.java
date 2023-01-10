package com.example.backend.service;

import com.example.backend.common.exception.CustomApiException;
import com.example.backend.common.jwt.JWTUserMap;
import com.example.backend.common.jwt.JwtService;
import com.example.backend.domain.Aws;
import com.example.backend.domain.User;
import com.example.backend.dto.ResponseMap;
import com.example.backend.dto.user.request.RequestUserEmailDoubleCheckDto;
import com.example.backend.dto.user.request.RequestUserLoginDto;
import com.example.backend.dto.user.request.RequestUserRegisterDto;
import com.example.backend.dto.user.request.RequestUserSnsRegisterDto;
import com.example.backend.repository.aws.AwsRepository;
import com.example.backend.repository.aws.AwsRepositorySupport;
import com.example.backend.repository.user.UserRepository;
import com.example.backend.repository.user.UserRepositorySupport;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Service
@RequiredArgsConstructor
public class UserService {

    private final BCryptPasswordEncoder bCryptPasswordEncoder;
    private final JwtService jwtService;
    private static final String ACCESS_TOKEN = "accessToken";
    private static final String REFRESH_TOKEN = "refreshToken";
    private static final int TEN_MINUTE = 1000 * 60 * 10;

    private static final int DAY = 1000 * 60 * 10 * 60 * 24;
    private final UserRepository userRepository;
    private final UserRepositorySupport userRepositorySupport;

    private final AwsRepository awsRepository;

    private final AwsRepositorySupport awsRepositorySupport;


    public void initToken (User user, HttpServletRequest request, HttpServletResponse response) {
        // 엑세스 토큰 생성 (유효시간은 최대한 짧게)
        System.out.println(user.getUserName());
        JWTUserMap jwtUserMap = new JWTUserMap(user);
        String accessToken = jwtService.createAccessToken(user.getNo()+"", TEN_MINUTE, jwtUserMap.getJWTMap());
        response.setHeader(ACCESS_TOKEN, accessToken); // 헤더에 넣어준다

        //리프레쉬 토큰 생성 (유효기간은 최대한 길게)
        String refreshToken  = jwtService.createRefreshToken(DAY); // 리프레쉬 토큰은 아무런 정보가없는 입장권
        user.setRefreshToken(refreshToken); // 리프레쉬 토큰은 db에 저장해야함
        response.setHeader(REFRESH_TOKEN, refreshToken); // 헤더에 넣어준다
    }

    /**
     * 일반 로그인
     * @param requestUserRegisterDto
     * @return
     */
    @Transactional(readOnly = false)
    public ResponseMap login (RequestUserLoginDto requestUserRegisterDto, HttpServletRequest request, HttpServletResponse response) {
        ResponseMap responseMap = new ResponseMap();

        User user = userRepository.findByEmail(requestUserRegisterDto.getEmail());

        if (user == null) {
            throw new CustomApiException("존재하지 않는 이메일입니다.", HttpStatus.BAD_REQUEST);
        } else {
            if (user.getProvider() != null) {
                throw new CustomApiException( user.getProvider() + "로 가입한 계정입니다 \n" + user.getProvider() + "로 로그인해주세요.", HttpStatus.SEE_OTHER); // 일반회원으로 가입한 경로가 존재함
            } else {
                if (!bCryptPasswordEncoder.matches(requestUserRegisterDto.getPassword(), user.getPassword())) {
                    throw new CustomApiException("비밀번호가 일치하지 않습니다.", HttpStatus.BAD_REQUEST);
                } else {
                    initToken(user, request, response); // 토큰 생성 함수
                    responseMap.setMessage("로그인 되었습니다.");
                    responseMap.setHttpStatus(HttpStatus.OK);
                }
            }
        }

        return responseMap;
    }

    /**
     * 일반 회원가입
     * @param requestUserRegisterDto
     * @return
     */
    @Transactional(readOnly = false)
    public ResponseMap register (RequestUserRegisterDto requestUserRegisterDto, HttpServletRequest request, HttpServletResponse response) {
        ResponseMap responseMap = new ResponseMap();

        User user = userRepository.save(requestUserRegisterDto.toEntity());

        String rawPassword = requestUserRegisterDto.getPassword();
        String encPassword = bCryptPasswordEncoder.encode(rawPassword); // 비밀번호 암호화

        user.setPassword(encPassword);

        if (requestUserRegisterDto.getProfileImageFile() != null) {
            Aws profileImageFile = awsRepository.save(requestUserRegisterDto.getProfileImageFile().toEntity());
            user.setProfileImageFile(profileImageFile);
        }

        if (requestUserRegisterDto.getBackgroundImageFile() != null) {
            Aws backgroundImageFile = awsRepository.save(requestUserRegisterDto.getBackgroundImageFile().toEntity());
            user.setBackgroundImageFile(backgroundImageFile);
        }


        if (user != null) {
            initToken(user, request, response); // 토큰 생성 함수
            responseMap.setMessage("회원가입이 완료되었습니다.");
            responseMap.setHttpStatus(HttpStatus.OK);
            return responseMap;
        } else {
            throw new CustomApiException("회원가입에 실패하였습니다.\n고객 센터에 문의해주세요.", HttpStatus.BAD_REQUEST);
        }
    }

    /**
     * 아이디 중복체크
     * @param requestUserRegisterDto
     * @return
     */
    @Transactional(readOnly = true)
    public ResponseMap userEmailDoubleCheck (RequestUserEmailDoubleCheckDto requestUserRegisterDto) {
        ResponseMap responseMap = new ResponseMap();
        User user = userRepositorySupport.findByEmail(requestUserRegisterDto.getEmail());

        if (user != null) {
            throw new CustomApiException("이미 사용중인 이메일 입니다.", HttpStatus.BAD_REQUEST);
        } else {
            responseMap.setHttpStatus(HttpStatus.OK);
            responseMap.setMessage("사용가능한 이메일 입니다.");
            responseMap.setData(new ResponseEntity<>(true, null, HttpStatus.OK));
            return responseMap;
        }
    }

    /**
     * 소셜 로그인
     * @param requestUserSnsRegisterDto
     * @return
     */
    @Transactional(readOnly = false) // 로그인뿐만 아니라 회원가입일수도 있기때문 false
    public ResponseMap snsLogin (RequestUserSnsRegisterDto requestUserSnsRegisterDto, HttpServletRequest request, HttpServletResponse response) {
        ResponseMap responseMap = new ResponseMap();
        User user = userRepositorySupport.findByEmail(requestUserSnsRegisterDto.getEmail());
        if (user == null) { // 같은 이메일로 가입한 회원이 없다면 (= sns로 첫 로그인 이라는 뜻)
            user = userRepository.save(requestUserSnsRegisterDto.toEntity()); // 가입 진행
            responseMap.setHttpStatus(HttpStatus.OK);
            responseMap.setMessage("회원가입이 완료되었습니다");
            initToken(user, request, response); // 토큰 생성 함수
            return responseMap;
        } else {
            if (user.getProvider() == null) { // sns 로 가입한 이력이 x
                throw new CustomApiException("일반 이메일회원입니다.\n연동하시겠습니까?.", HttpStatus.SEE_OTHER); // 일반회원으로 가입한 경로가 존재함
            } else {
                user.setProvider(requestUserSnsRegisterDto.getProvider()); // sns의 가입경로를 바꿔준다
                user.setProviderId(requestUserSnsRegisterDto.getProviderId()); // sns의 고유 번호를 바꿔준다.
                initToken(user, request, response); // 토큰 생성 함수
                responseMap.setHttpStatus(HttpStatus.OK);
                responseMap.setMessage("로그인 완료");
                return responseMap;

            }
        }
    }

    /**
     * 소셜 로그인 연동
     * @param requestUserSnsRegisterDto
     * @return
     */
    @Transactional(readOnly = false) // 로그인뿐만 아니라 회원가입일수도 있기때문 false
    public ResponseMap updateProvider (RequestUserSnsRegisterDto requestUserSnsRegisterDto) {
        User user = userRepositorySupport.findByEmail(requestUserSnsRegisterDto.getEmail());
        user.setProvider(requestUserSnsRegisterDto.getProvider());
        user.setProviderId(requestUserSnsRegisterDto.getProviderId());
        ResponseMap responseMap = new ResponseMap();
        responseMap.setHttpStatus(HttpStatus.OK);
        responseMap.setMessage("연동되었습니다.");
        return responseMap;
    }

}
