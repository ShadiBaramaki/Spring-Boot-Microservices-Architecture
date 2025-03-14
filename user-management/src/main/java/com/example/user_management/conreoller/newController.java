package com.example.user_management.conreoller;

import com.example.user_management.dto.CreateUserRequest;
import com.example.user_management.dto.LoginRequest;
import com.example.user_management.service.newUserService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/keycloak-users")
public class newController {

    private final newUserService keycloakAdminService;

    public newController(newUserService keycloakAdminService) {
        this.keycloakAdminService = keycloakAdminService;
    }


    @PostMapping("/create")
    public ResponseEntity<String> createUser(@RequestBody CreateUserRequest user) {
        String response = keycloakAdminService.createUserInKeycloak(user);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/login")
    public Mono<ResponseEntity<String>> login(@RequestBody LoginRequest user) {
        return keycloakAdminService.authenticate(user.getUsername(), user.getPassword())
                .map(token -> ResponseEntity.ok(token))
                .onErrorResume(e -> Mono.just(ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Authentication failed")));
    }

}
