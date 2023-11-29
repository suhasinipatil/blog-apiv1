package com.example.blogapi.commons.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class ErrorMessage {
    String message;

    public ErrorMessage(String message) {
        this.message = message;
    }
}
