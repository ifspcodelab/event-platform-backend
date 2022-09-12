package br.edu.ifsp.spo.eventos.eventplatformbackend.account;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties("account")
@Getter
@Setter
public class AccountConfig {
    private Integer verificationTokenExpiresIn;
    private Integer passwordResetTokenExpiresIn;
    private Integer accountDeletionTokenExpiresIn;
}