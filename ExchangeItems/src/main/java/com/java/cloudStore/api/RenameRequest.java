package com.java.cloudStore.api;

import lombok.Data;

@Data
public class RenameRequest implements CloudMessage {
    private final String oldFileName;
    private final String newFileName;

}
