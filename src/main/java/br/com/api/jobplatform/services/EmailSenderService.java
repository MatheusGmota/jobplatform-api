package br.com.api.jobplatform.services;

import br.com.api.jobplatform.domain.enums.StatusEmail;
import br.com.api.jobplatform.domain.model.EmailModel;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class EmailSenderService {

    @Autowired
    private JavaMailSender mailSender;

    @Transactional
    public void sendEmail(EmailModel emailModel) {
        try{
            SimpleMailMessage message = new SimpleMailMessage();
            message.setFrom("livematgreg@gmail.com");
            message.setTo(emailModel.getEmailTo());
            message.setSubject(emailModel.getSubject());
            message.setText(emailModel.getText());
            mailSender.send(message);

            emailModel.setStatusEmail(StatusEmail.SENT);
        } catch (MailException e){
            emailModel.setStatusEmail(StatusEmail.ERROR);
        }
//        System.out.println("Email enviado com sucesso");
    }
}
