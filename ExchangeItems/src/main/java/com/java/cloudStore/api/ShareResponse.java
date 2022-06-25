package com.java.cloudStore.api;

import lombok.Data;

@Data
public class ShareResponse implements CloudMessage{
    private final boolean success;
    private final String msg;
}
