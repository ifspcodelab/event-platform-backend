package br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration;

import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${support.mail}")
    private String supportMail;

    public void sendEmailToClient(String subject, String email, String content) throws MessagingException {
        MimeMessage mail = mailSender.createMimeMessage();

        MimeMessageHelper message = new MimeMessageHelper(mail);
        message.setSubject(subject);
        message.setText(content, true);
        message.setFrom(supportMail);
        message.setTo(email);

        mailSender.send(mail);
    }

    public String getContentMailVerification(String name, String url) {
        return  "<div style=\"text-align: center; height: 100%;\"><div style=\"min-width: 300px; padding: 10px; text-align: left\"><h1>Verifique seu e-mail</h1>\n" +
                "<p>Ol&aacute;, "+ name + "!</p>\n" +
                "<p>Voc&ecirc; fez cadastro no sistema de registro do IFSP SPO.</p>\n" +
                "<p>Por favor, verifique seu e-mail.</p>\n" +
                " <a href=\"" + url +"\" target=\"_blank\" rel=\"noreferrer noopener\">" +
                "<button style=\"background-color: #4caf50; color: #ffffff; height: 45px; border-radius: 5px; font-weight: bold; font-size: 18px; margin: 0 20px;\">Verificar</button></a>" +
                "<p>Caso n&atilde;o consiga usar o bot&atilde;o, copie e cole o seguinte link no seu navegador:</p>\n" +
                "<p>"+ url + "</p>\n" +
                "<p>Atenciosamente,</p>\n" +
                "<p>Organiza&ccedil;&atilde;o</p></div></div>";
    }
}
