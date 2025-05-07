package com.vietbevis.authentication.service;

import jakarta.mail.MessagingException;

public interface EmailService {
    void sendWithHtml(String to, String subject, String html) throws MessagingException;

    void sendOTPEmail(String email, String name, String otp);
}
