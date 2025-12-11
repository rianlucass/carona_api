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

    public void sendVerificationCode(String toEmail, String verificationCode) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject("Código de Verificação - Sua Aplicação");
        message.setText(
                "Olá!\n\n" +
                        "Seu código de verificação é: " + verificationCode + "\n\n" +
                        "Este código expira em 10 minutos.\n\n" +
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

}
