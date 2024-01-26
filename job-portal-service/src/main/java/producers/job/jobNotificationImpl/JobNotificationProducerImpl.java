package producers.job.jobNotificationImpl;

import lombok.extern.slf4j.Slf4j;
import producers.job.models.JobApplicationCreatedNotification;
import producers.job.models.JobCreatedNotification;
import org.springframework.amqp.AmqpException;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import producers.job.jobNotification.JobNotificationProducer;

@Service
@Slf4j
public class JobNotificationProducerImpl implements JobNotificationProducer {

    @Value("${rabbitmq.exchange.name}")
    private String exchange;

    @Value("${rabbitmq.job.routing.key}")
    private String jobRoutingKey;

    @Value("${rabbitmq.jobApplication.routing.key}")
    private String jobApplicationRoutingKey;

    @Autowired
    private RabbitTemplate rabbitTemplate;

    @Override
    public void sendJobCreatedNotification(JobCreatedNotification jobCreatedNotification) throws AmqpException {
        log.info("Request received for send Create Job Notification");
        rabbitTemplate.convertAndSend(exchange, jobRoutingKey, jobCreatedNotification);
    }

    @Override
    public void sendJobApplicationCreatedNotification(JobApplicationCreatedNotification jobApplicationCreatedNotification) throws AmqpException {
        log.info("Request received for send Job Application Created Notification");
        rabbitTemplate.convertAndSend(exchange, jobApplicationRoutingKey, jobApplicationCreatedNotification);
    }

}
