package com.jobportal;

import org.springframework.amqp.core.AmqpTemplate;
import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.amqp.support.converter.MessageConverter;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.web.SecurityFilterChain;

@TestConfiguration
@Import({config.jwt.JwtAuthenticationConfig.class, config.rabbitmq.job.RabbitmqJobConfig.class})
public class ApplicationTestConfiguration {
    @MockBean
    private RabbitTemplate rabbitTemplate;

    @MockBean
    private SecurityFilterChain securityFilterChain;

    @MockBean(name = "queue")
    private Queue queue;

    @MockBean(name = "exchange")
    private TopicExchange topicExchange;

    @MockBean(name = "binding")
    private Binding binding;

    @MockBean
    private MessageConverter convertor;

    @MockBean(name = "amqpTemplate")
    private AmqpTemplate ampqTemplate;

}
