package com.java.cloudStore.api;

import lombok.Data;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;
import java.util.stream.Collectors;

@Data
public class ListFiles implements CloudMessage {

   private final List<String> fileList;

    public ListFiles (Path path) throws IOException {
        fileList= Files.list(path).map(p->p.getFileName().toString())
                .collect(Collectors.toList());
    }

    public ListFiles (Path path,List<Path> addList) throws IOException {
        List<String> ownFiles= Files.list(path).map(p->p.getFileName().toString())
                .collect(Collectors.toList());
        for(Path pth : addList){
            ownFiles.add(pth.getFileName().toString());
        }
        fileList=ownFiles;
    }
}
