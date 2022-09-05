package br.edu.ifsp.spo.eventos.eventplatformbackend.account.signup;

import br.edu.ifsp.spo.eventos.eventplatformbackend.account.Account;
import br.edu.ifsp.spo.eventos.eventplatformbackend.account.password.PasswordResetToken;
import br.edu.ifsp.spo.eventos.eventplatformbackend.registration.Registration;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;
import java.time.format.DateTimeFormatter;
import java.util.Date;

@Service
@RequiredArgsConstructor
public class EmailService {
    private final JavaMailSender mailSender;

    @Value("${support.mail}")
    private String supportMail;

    @Value("${support.name}")
    private String supportName;

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

    public void sendEmailToConfirmRegistration(Account account, Registration registration) throws MessagingException {
        var acceptRegistrationUrl = url +"minhas-inscricoes/aceitar-vaga/"+ registration.getId();
        var denyRegistrationUrl = url +"minhas-inscricoes/recusar-vaga/"+ registration.getId();

        var name = account.getName().split(" ")[0];
        var sessionSchedules = registration.getSession().getSessionsSchedules();
        String sessionSchedulesString = "";

        for (var s : sessionSchedules) {
            sessionSchedulesString += s.getExecutionStart().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:MM")) + " às " + s.getExecutionEnd().format(DateTimeFormatter.ofPattern("dd/MM/yyyy - HH:MM")) + "<br>";
        }

        var content = "<div style=\"text-align: center;\"><div style=\"padding: 10px; text-align: left\"><h1>Confirme seu cadastro na atividade " + registration.getSession().getActivity().getTitle() + "</h1>\n" +
                "<p>Ol&aacute;, "+ name + ".</p>\n" +
                "<p>Voc&ecirc; saiu da lista de espera e conseguiu uma vaga na sess&atilde;o " + registration.getSession().getTitle() + ", para os hor&aacute;rios:<br>" +
                sessionSchedulesString +
                "</p>\n" +
                "<p>Ao confirmar sua presen&ccedil;a na sess&atilde;o, voc&ecirc; ser&aacute; removido automaticamente de todas as listas de espera no hor&aacute;rio dessa sess&atilde;o. Voc&ecirc; possui 12 horas para confirmar. Para confirmar sua presen&ccedil;a, selecione o bot&atilde;o abaixo.</p>\n" +
                "<a href=\"" + acceptRegistrationUrl +"\" target=\"_blank\" style=\"max-width: 280px; text-decoration: none; display: inline-block; background-color: #4caf50; color: #ffffff; height: 36px; border-radius: 5px; font-weight: bold; font-size: 18px; margin: 20px 0; width: 100%; text-align: center; padding-top: 10px; \">"  +
                "Confirmar" +
                "</a>" +
                "<p>Caso n&atilde;o consiga participar, cancele sua inscri&ccedil;&atilde;o agora, permitindo que o pr&oacute;ximo da lista de espera seja chamado. Ao cancelar sua inscri&ccedil;&atilde;o, voc&ecirc; n&atilde;o ser&aacute; removido de outras listas de espera no hor&aacute;rio dessa sess&atilde;o. Caso voc&ecirc; n&atilde;o cancele a sua inscri&ccedil;&atilde;o, o sistema far&aacute; isso automaticamente ap&oacute;s 12 horas a partir do envio deste e-mail.</p>\n" +
                "<a href=\"" + denyRegistrationUrl +"\" target=\"_blank\" style=\"max-width: 280px; text-decoration: none; display: inline-block; background-color: #FF0000; color: #ffffff; height: 36px; border-radius: 5px; font-weight: bold; font-size: 18px; margin: 20px 0; width: 100%; text-align: center; padding-top: 10px; \">" +
                "Cancelar" +
                "</a>" +
                "<p>Atenciosamente,</p>\n" +
                "<p>Organiza&ccedil;&atilde;o Eventos IFSP SPO</p></div></div>";

        sendEmailToClient("Confirmação de Presença em Sessão da Plataforma de Eventos IFSP SPO", account.getEmail(), content);
    }

    public void sendEmailToClient(String subject, String email, String content) throws MessagingException {
        MimeMessage mail = mailSender.createMimeMessage();

        MimeMessageHelper message = new MimeMessageHelper(mail);
        message.setSubject(subject);
        message.setText(content, true);
        message.setTo(email);

        try {
            message.setFrom(new InternetAddress(supportMail, supportName));
        } catch (UnsupportedEncodingException ex) {
            ex.printStackTrace();
            message.setFrom(supportMail);
        }

        mailSender.send(mail);
    }
}
