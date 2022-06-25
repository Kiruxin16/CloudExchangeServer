package com.java.cloudStore.api;

import lombok.Data;

@Data
public class NavigateMessage implements CloudMessage{
    final private String newFolder;
}
