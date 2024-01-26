package config.rabbitmq.jobApplication;

import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqJobApplicationConfig {

    @Value("${rabbitmq.jobApplication.queue.name}")
    private String jobApplicationQueue;

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.jobApplication.routing.key}")
    private String jobApplicationRoutingKey;

    @Bean
    public Queue jobApplicationQueue() {
        return new Queue(jobApplicationQueue);
    }


    @Bean
    public TopicExchange jobApplicationExchange() {
        return new TopicExchange(exchange);
    }

    @Bean
    public Binding jobApplicationQueueBinding() {
        return BindingBuilder
                .bind(jobApplicationQueue())
                .to(jobApplicationExchange())
                .with(jobApplicationRoutingKey);
    }

}
