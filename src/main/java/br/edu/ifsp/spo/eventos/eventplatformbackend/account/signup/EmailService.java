package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetToken;
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

    @Value("${frontend.url}/")
    private String url;

    public void sendVerificationEmail(Account account, VerificationToken verificationToken) throws MessagingException {
        var verificationUrl = url + "cadastro/verificacao/" + verificationToken.getToken().toString();
        var name = account.getName().split(" ")[0];

        var content = "<div style=\"text-align: center;\"><div style=\"padding: 10px; text-align: left\"><h1>Verifique seu e-mail</h1>\n" +
                "<p>Ol&aacute;, "+ name + ".</p>\n" +
                "<p>Voc&ecirc; realizou o cadastro na Plataforma de Eventos do IFSP SPO.</p>\n" +
                "<p>Utilize o bot&atilde;o abaixo para verificar seu e-mail.</p>\n" +
                "<a href=\"" + verificationUrl +"\" target=\"_blank\" style=\"max-width: 280px; text-decoration: none; display: inline-block; background-color: #4caf50; color: #ffffff; height: 36px; border-radius: 5px; font-weight: bold; font-size: 18px; margin: 20px 0; width: 100%; text-align: center; padding-top: 10px; \">" +
                "  Verificar E-mail" +
                "</a>" +
                "<p>Caso n&atilde;o consiga utilizar o bot&atilde;o, copie e cole o seguinte link no seu navegador:</p>\n" +
                "<p>"+ verificationUrl + "</p>\n" +
                "<p>Atenciosamente,</p>\n" +
                "<p>Organiza&ccedil;&atilde;o Eventos IFSP SPO</p></div></div>";

        sendEmailToClient("Verificação de E-mail da Plataforma de Eventos IFSP SPO", account.getEmail(), content);
    }

    public void sendPasswordResetEmail(Account account, PasswordResetToken token) throws MessagingException {
        var passwordResetUrl = url + "redefinir-minha-senha/" + token.getToken().toString();
        var name = account.getName().split(" ")[0];

        var content = "<div style=\"text-align: center;\"><div style=\"padding: 10px; text-align: left\"><h1>Pedido de altera&ccedil;&atilde;o de senha</h1>\n" +
                "<p>Ol&aacute;, "+ name + ".</p>\n" +
                "<p>Utilize o bot&atilde;o abaixo para alterar a sua senha.</p>\n" +
                "<a href=\"" + passwordResetUrl +"\" target=\"_blank\" style=\"max-width: 280px; text-decoration: none; display: inline-block; background-color: #4caf50; color: #ffffff; height: 36px; border-radius: 5px; font-weight: bold; font-size: 18px; margin: 20px 0; width: 100%; text-align: center; padding-top: 10px; \">" +
                "  Alterar Senha" +
                "</a>" +
                "<p>Caso n&atilde;o consiga utilizar o bot&atilde;o, copie e cole o seguinte link no seu navegador:</p>\n" +
                "<p>"+ passwordResetUrl + "</p>\n" +
                "<p>Atenciosamente,</p>\n" +
                "<p>Organiza&ccedil;&atilde;o Eventos IFSP SPO</p></div></div>";

        sendEmailToClient("Alteração de Senha de Conta da Plataforma de Eventos IFSP SPO", account.getEmail(), content);
    }

    public void sendEmailToClient(String subject, String email, String content) throws MessagingException {
        MimeMessage mail = mailSender.createMimeMessage();

        MimeMessageHelper message = new MimeMessageHelper(mail);
        message.setSubject(subject);
        message.setText(content, true);
        message.setFrom(supportMail);
        message.setTo(email);

        mailSender.send(mail);
    }
}