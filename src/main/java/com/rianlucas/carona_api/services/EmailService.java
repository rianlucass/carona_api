package com.rianlucas.carona_api.services;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.rianlucas.carona_api.infra.exceptions.verification.EmailSendingException;

import lombok.RequiredArgsConstructor;

@Service
@RequiredArgsConstructor
public class EmailService {

    private final JavaMailSender mailSender;

    private String link = "${app.base.url.prod}/auth/reset-password?token=";

    public void sendVerificationCode(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Código de Verificação - Sua Aplicação");
        message.setText(
                "Olá!\n\n" +
                        "Seu código de verificação é: " + verificationCode + "\n\n" +
                        "Este código expira em 2 minutos.\n\n" +
                        "Se você não solicitou este código, ignore este email.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Via Carona ;)");

        try {
            mailSender.send(message);
            System.out.println("Email enviado para: " + toEmail);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
            throw new EmailSendingException(e);
        }
    }

    public void sendPasswordResetEmail(String toEmail, String resetToken) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Solicitação de Redefinição de Senha");
        message.setText(
                "Olá!\n\n" +
                        "Você solicitou uma redefinição de senha. Use o link abaixo para redefinir sua senha:\n\n" +
                        "Link: " + link + resetToken + "\n\n" +
                        "Este token expira em 15 minutos.\n\n" +
                        "Se você não solicitou esta alteração, ignore este email.\n\n" +
                        "Atenciosamente,\n" +
                        "Equipe Via Carona ;)");
        try {
            mailSender.send(message);
            System.out.println("Email enviado para: " + toEmail);
        } catch (Exception e) {
            System.err.println("Erro ao enviar email: " + e.getMessage());
            throw new EmailSendingException(e);
        }
    }

}
