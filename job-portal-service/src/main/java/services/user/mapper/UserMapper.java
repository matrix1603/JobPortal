package services.user.mapper;

import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;
import entity.user.User;
import org.springframework.stereotype.Component;

import java.sql.Date;
import java.text.ParseException;
import java.util.List;
import java.util.Map;
import java.util.Objects;

@Component
public class UserMapper {

    public User buildUserEntityFromToken(String authorizationToken) {
        return getUserFromToken(authorizationToken);
    }

    private User getUserFromToken(String accessToken) {
        User user = null;
        try {
            Map<String, Object> myClaims = getUserClaimSet(accessToken);
            List<String> emailList = (List<String>) myClaims.get("emails");
            String phoneNumber = myClaims.containsKey("extension_PhoneNumber") ?
                    (String) myClaims.get("extension_PhoneNumber") :
                    "";
            user = User.builder().
                    // FOR TESTING THE TOKEN DOES NOT CONTAINS EMAIL IN CLAIMS, FOR THAT WE ARE SAVING "name" FIELD OF TOKEN CLAIMS AS EMAIL
                            emailId(Objects.isNull(emailList) || emailList.isEmpty() ? myClaims.get("name").toString() : emailList.get(0)).
                    phoneNumber(phoneNumber).
                    createdDate(new Date(System.currentTimeMillis())).
                    updatedDate(new Date(System.currentTimeMillis())).
                    build();

        } catch (Exception e) {
            throw new RuntimeException("Error while extracting user information from access token");
        }

        return user;
    }

    public String extractEmailFromToken(String authorizationToken) {
        String userEmail = null;
        try {
            Map<String, Object> myClaims = getUserClaimSet(authorizationToken);
            List<String> emailList = (List<String>) myClaims.get("emails");

            // THE TOKEN FOR TEST CASES, DO NOT HAVING EMAIL IN CLAIM, THAT'S WHY WE ARE CHECKING IF NO EMAIL IS FOUND, THEN WE ARE GOING TO RETURN NAME
            userEmail = Objects.isNull(emailList) || emailList.isEmpty() ? myClaims.get("name").toString() : emailList.get(0);

        } catch (Exception e) {
            throw new RuntimeException("Error while extracting user information from access token");
        }
        return userEmail;
    }

    private Map<String, Object> getUserClaimSet(String accessToken) throws ParseException {
        String token = accessToken.substring(7);
        SignedJWT signedJWT = SignedJWT.parse(token);
        JWTClaimsSet claimsSet = signedJWT.getJWTClaimsSet();
        Map<String, Object> tokenClaims = claimsSet.getClaims();
        return tokenClaims;
    }


}
