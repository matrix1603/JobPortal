package com.jobportal.cucumberglue.utils;

import com.fasterxml.jackson.databind.JsonNode;
import org.jetbrains.annotations.NotNull;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

@Component
public class CucumberTestUtils {

    @Value("${token.grantType}")
    private String grantType;

    @Value("${token.client.id}")
    private String clientId;

    @Value("${token.client.secret}")
    private String clientSecret;

    @Value("${token.scope}")
    private String scope;

    @Value("${token.username}")
    private String username;

    @Value("${token.password}")
    private String password;

    public String getBearerToken() {
        String bearerToken = "";
        MultiValueMap<String, String> body = getTokenRequestBodyMap();
        HttpHeaders authTokenHeaders = new HttpHeaders();
        authTokenHeaders.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        HttpEntity<MultiValueMap<String, String>> requestBodyFormUrlEncoded = new HttpEntity<>(body, authTokenHeaders);
        ResponseEntity<JsonNode> responseEntity = null;

        try {
            responseEntity = new RestTemplate().postForEntity("https://login.microsoftonline.com/jobportals.onmicrosoft.com/oauth2/v2.0/token", requestBodyFormUrlEncoded, JsonNode.class);
            bearerToken = responseEntity.getBody().get("access_token").asText();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "Bearer " + bearerToken;
    }

    @NotNull
    private MultiValueMap<String, String> getTokenRequestBodyMap() {
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", grantType);
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("scope", scope);
        body.add("username", username);
        body.add("password", password);
        return body;
    }
}
