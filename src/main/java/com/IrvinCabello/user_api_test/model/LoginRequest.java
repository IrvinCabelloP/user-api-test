package com.IrvinCabello.user_api_test.model;

import lombok.Data;

@Data
public class LoginRequest {
    private String tax_id;
    private String password;
}