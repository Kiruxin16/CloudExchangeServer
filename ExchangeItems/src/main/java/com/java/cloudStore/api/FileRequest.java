package com.java.cloudStore.api;

import lombok.Data;

@Data
public class FileRequest implements CloudMessage {

    private final String name;

}
