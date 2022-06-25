package com.java.cloudStore.api;

import lombok.Data;

@Data
public class RegResponse implements CloudMessage{
    private final boolean success;
    private final String message;
}
