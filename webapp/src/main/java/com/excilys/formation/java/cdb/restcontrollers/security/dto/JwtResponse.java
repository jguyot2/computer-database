package com.excilys.formation.java.cdb.restcontrollers.security.dto;

public class JwtResponse {
    private final String jwt;
    private final String role;

    public JwtResponse(String jwt, String role) {
        this.jwt = jwt;
        this.role = role;
    }

    public String getRole() {
        return role;
    }

    public String getJwt() {
        return this.jwt;
    }
}
