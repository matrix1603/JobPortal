package producers.user.userNotification;

import producers.user.models.UserRegisteredNotification;

public interface UserNotificationProducer {
    void sendUserRegisteredNotification(UserRegisteredNotification userRegisteredNotification);
}
