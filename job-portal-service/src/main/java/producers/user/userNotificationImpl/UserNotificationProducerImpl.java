package producers.user.userNotificationImpl;


import lombok.extern.slf4j.Slf4j;
import producers.user.models.UserRegisteredNotification;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import producers.user.userNotification.UserNotificationProducer;

@Service
@Slf4j
public class UserNotificationProducerImpl implements UserNotificationProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.user.routing.key}")
    private String routingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendUserRegisteredNotification(UserRegisteredNotification userRegisteredNotification) throws AmqpException {
        log.info("Request received for send User Registered Notification");
        rabbitTemplate.convertAndSend(exchange, routingKey, userRegisteredNotification);
    }
}
