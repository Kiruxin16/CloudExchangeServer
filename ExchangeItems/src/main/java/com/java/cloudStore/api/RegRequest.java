package com.java.cloudStore.api;

import lombok.Data;

@Data
public class RegRequest implements  CloudMessage{
    private final String login;
    private final String pass;
}
