package services.user.userService;

import entity.user.User;
import models.response.ServiceResponse;

public interface UserService {

    ServiceResponse<User> registerUser(String authorizationToken);
}
