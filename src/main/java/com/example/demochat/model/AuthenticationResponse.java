package com.example.demochat.model;

import com.fasterxml.jackson.annotation.*;

public class AuthenticationResponse {

    @JsonView(AppViews.Public.class)
    private final String jwt;

    @JsonView(AppViews.Public.class)
    private User user;

    public AuthenticationResponse(String jwt) {
        this.jwt = jwt;
    }

    public AuthenticationResponse(String jwt, User user) {
        this.jwt = jwt;
        this.user = user;
    }

    public String getJwt() {
        return jwt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}