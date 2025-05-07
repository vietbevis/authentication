package com.vietbevis.authentication.service.impl;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.Optional;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.vietbevis.authentication.common.UserStatus;
import com.vietbevis.authentication.common.Utils;
import com.vietbevis.authentication.common.VerificationCodeType;
import com.vietbevis.authentication.dto.request.RegisterRequest;
import com.vietbevis.authentication.dto.request.SendOtpRequest;
import com.vietbevis.authentication.entity.UserEntity;
import com.vietbevis.authentication.entity.VerificationCodeEntity;
import com.vietbevis.authentication.exception.AlreadyExistsException;
import com.vietbevis.authentication.exception.BadRequestException;
import com.vietbevis.authentication.exception.NotFoundException;
import com.vietbevis.authentication.repository.UserRepository;
import com.vietbevis.authentication.repository.VerificationCodeRepository;
import com.vietbevis.authentication.service.AuthService;
import com.vietbevis.authentication.service.EmailService;

import lombok.RequiredArgsConstructor;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthServiceImpl implements AuthService {

    private final EmailService emailService;
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

        try {

            UserEntity newUser = UserEntity.builder()
                .fullName(request.getFullName())
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .status(UserStatus.ACTIVE)
                .build();

            userRepository.save(newUser);

            verificationCodeRepository.deleteByEmailAndCodeAndType(
                request.getEmail(),
                request.getOtp(),
                VerificationCodeType.REGISTER);

        } catch (Exception e) {
            throw new AlreadyExistsException("Email đã tồn tại.");
        }

        log.info("Registration OTP sent to email: {} with code: {}", request.getEmail(),
            request.getOtp());
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
            verificationCodeRepository.deleteByEmailAndCodeAndType(email, code, type);
            throw new BadRequestException("Mã OTP đã hết hạn.");
        }
    }
}
