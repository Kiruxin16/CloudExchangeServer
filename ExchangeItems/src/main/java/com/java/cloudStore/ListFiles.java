package com.java.cloudStore;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ListFiles implements CloudMessage {

    List<String> fileList;

    public ListFiles (Path path) throws IOException {
        fileList= Files.list(path).map(p->p.getFileName().toString())
                .collect(Collectors.toList());
    }
}
