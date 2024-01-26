package producers.user.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserRegisteredNotification {
    private int userId;
    private String emailId;
    private String phoneNumber;
    private String message;
}
