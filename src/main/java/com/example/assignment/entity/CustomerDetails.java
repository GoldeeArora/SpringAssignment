package com.example.assignment.entity;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class CustomerDetails {
private String first_name;
private String last_name;
private String street;
private String address;
private String city;
private String state;
private String email;
private String phone;
}
