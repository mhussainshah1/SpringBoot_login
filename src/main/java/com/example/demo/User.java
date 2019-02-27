package com.example.demo;

import org.springframework.lang.NonNull;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

public class User {
    @NotNull
    private String username;

    @NonNull
    @Size(min=3, max=20)
    @Validator //create my own annotation and put above password field
    //https://www.baeldung.com/spring-mvc-custom-validator
    private String password;

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }
}
