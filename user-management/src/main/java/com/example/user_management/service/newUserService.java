package com.example.user_management.service;

import com.example.user_management.dto.CreateUserRequest;
import com.example.user_management.model.User;
import com.example.user_management.repository.UserRepository;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import javax.ws.rs.client.AsyncInvoker;
import java.util.List;
import java.util.Map;

@Service
public class newUserService {

    private final String keycloakUrl = "http://localhost:9990/auth/realms/master/protocol/openid-connect/token";
    private final String clientId = "admin-cli";
    private final String adminUsername = "admin";  // Your Keycloak admin username
    private final String adminPassword = "admin";  // Your Keycloak admin password
    private final String clientSecret = "b0fe862c-0c1d-49bb-b595-7941abf6a5ee";
    private final String realm = "spring-user-test";
    private final WebClient webClient;
    private final UserRepository userRepository;

    public newUserService(WebClient.Builder webClientBuilder, UserRepository userRepository) {
        this.webClient = webClientBuilder.baseUrl(keycloakUrl).build();
        this.userRepository = userRepository;
    }

    // Method to get Admin Access Token
    public String getAdminAccessToken() {
        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        // Prepare the body of the POST request
        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("grant_type", "password");
        body.add("client_id", clientId);
        body.add("username", adminUsername);
        body.add("password", adminPassword);

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.postForEntity(keycloakUrl, request, Map.class);

        // Extract and return the access token
        return response.getBody().get("access_token").toString();
    }

    // Method to create a new user in Keycloak
    public String createUserInKeycloak(CreateUserRequest userRequest) {
        String accessToken = getAdminAccessToken();
        String createUserUrl = "http://localhost:9990/auth/admin/realms/spring-user-test/users";

        RestTemplate restTemplate = new RestTemplate();

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(accessToken);

        // Prepare the user details
        Map<String, Object> user = Map.of(
                "username", userRequest.getUsername(),
                "enabled", true,
                "credentials", List.of(Map.of(
                        "type", "password",
                        "value", userRequest.getPassword(),
                        "temporary", false
                ))
        );

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(user, headers);
        ResponseEntity<String> response = restTemplate.postForEntity(createUserUrl, request, String.class);

        User user1 = new User();
        user1.setUsername(userRequest.getUsername());
        user1.setPassword(userRequest.getPassword());
        user1.setEmail(userRequest.getEmail());
        user1.setFirstname(userRequest.getFirstname());
        user1.setLastname(userRequest.getLastname());


        userRepository.save(user1);


        return response.getBody();
    }

    public Mono<String> authenticate(String username, String password) {
        String url = String.format("http://localhost:9990/auth/realms/spring-user-test/protocol/openid-connect/token", realm);


        return webClient.post()
                .uri(url)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .bodyValue(String.format("grant_type=password&client_id=%s&scop=%s&username=%s&password=%s",
                        "test-client", "openid api", username, password))
                .retrieve()
                .bodyToMono(String.class);
    }
}
