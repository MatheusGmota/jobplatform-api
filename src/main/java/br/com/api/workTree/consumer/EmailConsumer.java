package br.com.api.workTree.consumer;

import br.com.api.workTree.domain.model.EmailModel;
import br.com.api.workTree.services.EmailSenderService;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Component;

@Component
public class EmailConsumer {

    @Autowired
    EmailSenderService emailService;

    //Método que escuta a fila definida ("${spring.rabbitmq.queue}")
    //Usando exchange default, pois não foi definido nenhum aqui
    @RabbitListener(queues = "${spring.rabbitmq.queue}")
    public void listen(@Payload EmailModel emailModel) {
        //Chamando o método sendEmail criado no package services
        emailService.sendEmail(emailModel);
        System.out.println("Email Status: " + emailModel.getStatusEmail().toString());
    }
}
