package com.example.bookbarnproject.model;


import com.example.bookbarnproject.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import javax.validation.constraints.*;


@Data
@AllArgsConstructor
@NoArgsConstructor
public class UpdateUserModel {

    private Long id;


    @Pattern(regexp="^[a-z0-9]{4,20}$", message="Username can only contains lowercase letters and numbers. And must be between 4-20 characters.")
    private String username;


    @Email
    @Size(min = 1, max = 50, message = "Email must be between 1-50 characters long")
    private String email;


    @Size(min = 0, max = 100, message = "Password must be between 6-100 characters long")
    private String password;


    @Size(min = 0, max = 100, message = "Password must be between 6-100 characters long")
    private String passwordRepeat;

    @Pattern(regexp = "^[a-zA-Z]{2,50}$",message="First name can only contains lowercase letters and numbers. And must be between 2-20 characters.")
    private String firstName;


    @Pattern(regexp = "^[a-zA-Z]{2,50}$",message="Last name can only contains lowercase letters and numbers. And must be between 2-20 characters.")
    private String lastName;


    @Size(min = 10, max = 100, message = "Address must be between 10-100 characters long")
    private String address;


    @NotNull(message="numericField: positive number value is required")
    @Max(150)
    @Min(0)
    private int age;

    private User.Sex sex;

    private User.Role role;


}
