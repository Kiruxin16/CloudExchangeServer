package com.java.cloudStore;

import lombok.Data;
import lombok.Value;

@Data
public class FileRequest implements CloudMessage {

    private final String name;

}
