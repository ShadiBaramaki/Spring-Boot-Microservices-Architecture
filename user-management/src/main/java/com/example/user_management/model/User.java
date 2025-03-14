package com.example.user_management.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "t_user")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    String username;
    String password;
    String email;
    String firstname;
    String lastname;


}
