package com.IrvinCabello.user_api_test.model;

import lombok.Data;

@Data 
public class Address {
    private Integer id;
    private String name;
    private String street;
    private String country_code;
}