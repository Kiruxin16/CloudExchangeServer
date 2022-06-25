package com.java.cloudStore.api;

import lombok.Data;

@Data
public class ShareRequest implements CloudMessage{
    private final String userName;
    private final String file;
}
