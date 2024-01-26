package controllers.user.userControllersImpl;

import controllers.user.userControllers.UserController;
import controllers.utils.BaseControllerUtils;
import lombok.extern.slf4j.Slf4j;
import models.response.ApplicationResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import services.user.userService.UserService;

@Slf4j
@RestController
@RequestMapping("/user")
public class UserControllerImpl extends BaseControllerUtils implements UserController {

    @Autowired
    private UserService userService;

    @Override
    public ResponseEntity<ApplicationResponse> registerUser(String authorizationToken) {

        var serviceResponse = userService.registerUser(authorizationToken);

        return applicationResponse(serviceResponse);
    }

}
