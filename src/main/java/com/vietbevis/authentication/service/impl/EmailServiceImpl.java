package com.vietbevis.authentication.service.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.context.Context;
import org.thymeleaf.spring6.SpringTemplateEngine;

import com.vietbevis.authentication.exception.BadRequestException;
import com.vietbevis.authentication.service.EmailService;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {

    private final JavaMailSender mailSender;
    private final SpringTemplateEngine templateEngine;

    @Value("${spring.mail.username}")
    private String senderEmail;

    @Value("${spring.application.name}")
    private String appName;

    @Override
    public void sendWithHtml(String to, String subject, String html) throws MessagingException {

        MimeMessage mimeMessage = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, "utf-8");

        helper.setFrom(appName + " <" + senderEmail + ">");
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(html, true);

        mailSender.send(mimeMessage);
    }

    @Override
    public void sendOTPEmail(String email, String name, String otp) {
        try {
            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("otp", otp);

            String htmlContent = templateEngine.process("otp-email", context);

            helper.setFrom(appName + " <" + senderEmail + ">");
            helper.setTo(email);
            helper.setSubject("Xác thực đăng ký tài khoản");
            helper.setText(htmlContent, true);

            mailSender.send(mimeMessage);
            log.info("Gửi OTP email thành công đến: {}", email);
        } catch (MessagingException e) {
            log.error("Gửi OTP email thất bại đến: {}", email, e);
            throw new BadRequestException("Gửi OTP email thất bại");
        }
    }
}