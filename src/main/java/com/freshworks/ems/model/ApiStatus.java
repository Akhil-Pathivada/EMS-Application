package com.freshworks.ems.model;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ApiStatus {
    @JsonProperty
    private Integer code;

    @JsonProperty
    private String message;

    public ApiStatus(Integer code, String message){
        this.code = code;
        this.message = message;
    }
}