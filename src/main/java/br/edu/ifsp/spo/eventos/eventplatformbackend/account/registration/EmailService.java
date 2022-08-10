package br.edu.ifsp.spo.eventos.eventplatformbackend.account.registration;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

@Service
public class EmailService {
    @Autowired
    private JavaMailSender mailSender;

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
        return "<p>Ol&aacute;, "+ name + "!</p>\n" +
                "<p>Voc&ecirc; fez cadastro no sistema de registro do IFSP SPO.</p>\n" +
                "<p>Por favor, confirme o seu e-mail neste <a href=\"" + url +
                "\" target=\"_blank\" rel=\"noreferrer noopener\">link</a>.</p>";
    }
}
