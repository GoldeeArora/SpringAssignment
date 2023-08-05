package com.example.assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthenticationResponse {
    private String authenticatedUser;
    private ExternalApiResponse externalData;

   

    // Getters and setters for authenticatedUser and externalData
    // ...
}

