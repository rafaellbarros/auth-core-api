package com.rafaellbarros.security.dtos;

import lombok.Data;

@Data
public class LoginRequestDTO {
    private String username;
    private String password;
}
