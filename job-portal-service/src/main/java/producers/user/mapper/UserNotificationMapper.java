package producers.user.mapper;

import entity.user.User;
import producers.user.models.UserRegisteredNotification;

public class UserNotificationMapper {
    public static UserRegisteredNotification getUserRegisteredNotification(User user) {
        return UserRegisteredNotification.builder().
                userId(user.getId()).
                emailId(user.getEmailId()).
                phoneNumber(user.getPhoneNumber()).
                message("User successfully registered in job portal service").
                build();
    }
}
