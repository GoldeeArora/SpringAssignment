package com.example.assignment.entity;

import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Getter;


@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class ExternalApiRequest {
private String login_id;
private String password;
}
