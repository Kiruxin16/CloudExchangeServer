package com.java.cloudStore.api;

import lombok.Data;

@Data
public class AuthResponse implements CloudMessage{
    private final boolean success;
    private final String message;
}
