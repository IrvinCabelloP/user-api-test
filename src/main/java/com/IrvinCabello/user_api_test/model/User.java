package com.IrvinCabello.user_api_test.model;

import lombok.Data;
import java.util.List;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

@Data
public class User {
    private UUID id;
    private String email;
    private String name;
    private String phone;
    @JsonProperty(access = JsonProperty.Access.WRITE_ONLY)
    private String password;
    private String tax_id;
    private String created_at;
    private List<Address> addresses;
}