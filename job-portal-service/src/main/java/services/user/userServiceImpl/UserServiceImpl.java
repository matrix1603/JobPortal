package services.user.userServiceImpl;

import entity.user.User;
import lombok.extern.slf4j.Slf4j;
import models.response.ServiceResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import producers.user.mapper.UserNotificationMapper;
import producers.user.userNotification.UserNotificationProducer;
import repository.user.UserRepository;
import services.user.userService.UserService;
import services.user.mapper.UserMapper;

import java.util.Objects;

@Service
@Slf4j
public class UserServiceImpl implements UserService {
    @Autowired
    private UserMapper userMapper;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private UserNotificationProducer userNotificationProducer;

    @Override
    public ServiceResponse<User> registerUser(String authorizationToken) {

        ServiceResponse<User> serviceResponse = new ServiceResponse<>();
        log.info("Request received to register user from authorization token");
        try {
            User user = userMapper.buildUserEntityFromToken(authorizationToken);

            if (!Objects.isNull(userRepository.findByEmailId(user.getEmailId()))) {
                serviceResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
                serviceResponse.setErrorMessage(String.format("User with email %s is already registered", user.getEmailId()));
                return serviceResponse;
            }

            User savedUser = userRepository.save(user);
            if (Objects.isNull(savedUser)) {
                serviceResponse.setHttpStatus(HttpStatus.INTERNAL_SERVER_ERROR);
                serviceResponse.setErrorMessage("Internal server error while saving user");
                return serviceResponse;
            }

            log.info("Registered user successfully");

            var userRegisteredNotification = UserNotificationMapper.getUserRegisteredNotification(savedUser);
            userNotificationProducer.sendUserRegisteredNotification(userRegisteredNotification);
            serviceResponse.setHttpStatus(HttpStatus.OK);
            serviceResponse.setData(savedUser);

        } catch (RuntimeException ex) {
            log.info(String.format("Error while registering user, Error message is : %s", ex.getMessage()));
            serviceResponse.setHttpStatus(HttpStatus.BAD_REQUEST);
            serviceResponse.setErrorMessage(ex.getMessage());
        }

        return serviceResponse;
    }

}
