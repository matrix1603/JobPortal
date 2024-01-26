package producers.job.jobNotification;

import producers.job.models.JobApplicationCreatedNotification;
import producers.job.models.JobCreatedNotification;
import org.springframework.amqp.AmqpException;

public interface JobNotificationProducer {
    void sendJobCreatedNotification(JobCreatedNotification jobCreatedNotification) throws AmqpException;

    void sendJobApplicationCreatedNotification(JobApplicationCreatedNotification jobApplicationCreatedNotification) throws AmqpException;
}
