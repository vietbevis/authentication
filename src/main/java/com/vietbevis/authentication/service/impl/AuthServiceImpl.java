package com.vietbevis.authentication.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vietbevis.authentication.common.RoleBase;
import com.vietbevis.authentication.common.UserStatus;
import com.vietbevis.authentication.common.Utils;
import com.vietbevis.authentication.common.VerificationCodeType;
import com.vietbevis.authentication.dto.request.LoginRequest;
import com.vietbevis.authentication.dto.request.RegisterRequest;
import com.vietbevis.authentication.dto.request.SendOtpRequest;
import com.vietbevis.authentication.dto.response.AuthResponse;
import com.vietbevis.authentication.entity.RoleEntity;
import com.vietbevis.authentication.entity.UserEntity;
import com.vietbevis.authentication.entity.VerificationCodeEntity;
import com.vietbevis.authentication.exception.AlreadyExistsException;
import com.vietbevis.authentication.exception.BadRequestException;
import com.vietbevis.authentication.exception.NotFoundException;
import com.vietbevis.authentication.repository.RoleRepository;
import com.vietbevis.authentication.repository.UserRepository;
import com.vietbevis.authentication.repository.VerificationCodeRepository;
import com.vietbevis.authentication.security.JwtTokenProvider;
import com.vietbevis.authentication.service.AuthService;
import com.vietbevis.authentication.service.EmailService;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final JwtTokenProvider jwtTokenProvider;
    private final AuthenticationManager authenticationManager;
    private final EmailService emailService;
    private final RoleRepository roleRepository;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final VerificationCodeRepository verificationCodeRepository;

    @Value("${otp.expiration.minutes:5}")
    private Long OTP_EXPIRATION_MINUTES;

    @Override
    @Transactional
    public void register(RegisterRequest request) {

        validateVerificationCode(request.getEmail(), request.getOtp(),
                VerificationCodeType.REGISTER);

        RoleEntity roleUser = roleRepository.findByName(RoleBase.USER.name())
                .orElseThrow(() -> new BadRequestException("Đã xảy ra sự cố khi tạo tài khoản. Vui lòng thử lại sau."));
        String hashPassword = passwordEncoder.encode(request.getPassword());

        try {

            UserEntity newUser = UserEntity.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                    .password(hashPassword)
                .status(UserStatus.ACTIVE)
                    .build();

            newUser.getRoles().add(roleUser);
            userRepository.save(newUser);

        } catch (Exception e) {
            throw new AlreadyExistsException("Email đã tồn tại.");
        }

        log.info("Tạo tài khoản thành công: {}", request.getEmail());
    }

    @Override
    @Transactional
    public void sendOTP(SendOtpRequest request) {

        Optional<UserEntity> user = userRepository.findByEmail(request.getEmail());

        if (request.getVerificationCodeType().equals(VerificationCodeType.REGISTER.name())
            && user.isPresent()) {
            throw new AlreadyExistsException("Email đã tồn tại.");
        }

        if (request.getVerificationCodeType().equals(VerificationCodeType.FORGOT_PASSWORD.name())
            && user.isEmpty()) {
            throw new NotFoundException("Email không tồn tại.");
        }
        String otp = Utils.generateRandomNumber(6);

        try {

            VerificationCodeEntity verificationCode = VerificationCodeEntity.builder()
                .code(otp)
                .email(request.getEmail())
                .type(VerificationCodeType.REGISTER)
                .expiresAt(
                    Date.from(Instant.now().plus(OTP_EXPIRATION_MINUTES, ChronoUnit.MINUTES)))
                .build();

            verificationCodeRepository.save(verificationCode);

        } catch (Exception e) {
            log.error("Lỗi tạo mã OTP: {}", e.getMessage());
            throw new AlreadyExistsException("Đã gửi mã OTP đến email.");
        }

        emailService.sendOTPEmail(request.getEmail(), "bạn", otp);
    }

    private void validateVerificationCode(String email, String code, VerificationCodeType type) {
        Optional<VerificationCodeEntity> verificationCode = verificationCodeRepository
                .findByEmailAndCodeAndType(email, code, type);

        if (verificationCode.isEmpty()) {
            throw new BadRequestException("Mã OTP không hợp lệ.");
        }

        if (verificationCode.get().getExpiresAt().before(new Date())) {
            throw new BadRequestException("Mã OTP đã hết hạn.");
        }

    }

    @Override
    public AuthResponse login(LoginRequest request) {
        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(),
                        request.getPassword()));

        String accessToken = jwtTokenProvider.generateAccessToken(auth);
        String refreshToken = jwtTokenProvider.generateRefreshToken(auth);

        return AuthResponse.builder()
                .accessToken(accessToken)
                .refreshToken(refreshToken)
                .build();
    }

}
