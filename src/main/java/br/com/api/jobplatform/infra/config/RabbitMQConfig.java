package br.com.api.jobplatform.infra.config;


import org.springframework.amqp.core.Queue;
import org.springframework.amqp.support.converter.Jackson2JsonMessageConverter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    //Configuração da fila
    @Value("${spring.rabbitmq.queue}")
    private String queue;

    //Em caso de perda de conexão não perde as mensagens da fila (durable = true)
    @Bean
    public Queue queue() {
        return new Queue(queue, true);
    }

    //Criando um conversor global para receber corretamente a mensagem de EmailDto
    //Deixando de utilizar o conversor padrão do AMQP por estarmos usando um EmailDto
    @Bean
    public Jackson2JsonMessageConverter messageConverter() {
        return new Jackson2JsonMessageConverter();
    }
}
