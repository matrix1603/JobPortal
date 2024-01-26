package config.rabbitmq.user;


import org.springframework.amqp.core.Binding;
import org.springframework.amqp.core.BindingBuilder;
import org.springframework.amqp.core.Queue;
import org.springframework.amqp.core.TopicExchange;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitmqUserConfig {

    @Value("${rabbitmq.user.queue.name}")
    private String userQueue;


    @Bean
    public TopicExchange userQueueExchange() {
        return new TopicExchange(exchange);
    }

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.user.routing.key}")
    private String userRoutingKey;

    @Bean
    public Queue userQueue() {
        return new Queue(userQueue);
    }


    @Bean
    public Binding userQueueBinding() {
        return BindingBuilder
                .bind(userQueue())
                .to(userQueueExchange())
                .with(userRoutingKey);
    }

}
