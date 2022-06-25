package com.java.cloudStore.server.handlers;

import com.java.cloudStore.api.*;
import com.java.cloudStore.server.database.DBHandler;
import com.java.cloudStore.server.database.DataBaseAuth;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;


public class FileInboundHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private  final Path homeDir=Path.of("CloudServer/server_directory");
    private  Path currentDir;
    private DataBaseAuth dataBaseAuths;
    private List<Path> sharedFilesList;

    public FileInboundHandler() {
        currentDir = homeDir;
        dataBaseAuths = new DataBaseAuth();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        System.out.println("Hey now");


    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest){
            Path checkPath = currentDir.resolve(fileRequest.getName());
            if(Files.exists(checkPath)){
                ctx.writeAndFlush( new FileMessage(checkPath));
            }else {
                for(Path pth: sharedFilesList){
                    if (fileRequest.getName().equals(pth.getFileName().toString())){
                        ctx.writeAndFlush(new FileMessage(pth));
                    }
                }
            }



        }else if (cloudMessage instanceof FileMessage fileMessage){
            Files.write(currentDir.resolve(fileMessage.getName()),fileMessage.getData());
            ctx.writeAndFlush(refreshList(currentDir));
        }else if(cloudMessage instanceof NavigateMessage navigateMessage){
            Path newDir =currentDir.resolve(navigateMessage.getNewFolder()).normalize();
            if (Files.isDirectory(newDir)){
                if(newDir.toString().equals(homeDir.toString())){
                    System.out.println("it is home directory");
                    return;
                }
                currentDir=newDir;
                ctx.writeAndFlush(refreshList(currentDir));
            }
        }else if(cloudMessage instanceof AuthRequest authRequest){
            AuthResponse  authResponse = dataBaseAuths.tryToAuth(authRequest.getLogin(), authRequest.getPass());
            if(authResponse.isSuccess()){
                sharedFilesList=fillSharedFilesList(dataBaseAuths.getSharedList(authRequest.getLogin()));
            }
            ctx.writeAndFlush(authResponse);



        }else if (cloudMessage instanceof RegRequest regRequest){
            RegResponse registrated =  dataBaseAuths.tryToReg(regRequest.getLogin(),regRequest.getPass());
            if (registrated.isSuccess()){
                Files.createDirectory(homeDir.resolve(regRequest.getLogin()));
            }
            ctx.writeAndFlush(registrated);
        }else if(cloudMessage instanceof RenameRequest renameRequest){
            Files.move(currentDir.resolve(renameRequest.getOldFileName()),currentDir.resolve(renameRequest.getNewFileName()));
            ctx.writeAndFlush(refreshList(currentDir));
        }else if(cloudMessage instanceof StopMessage stopMessage){
            ctx.writeAndFlush(stopMessage);


        }else if(cloudMessage instanceof ShareRequest shareRequest){
            ctx.writeAndFlush(dataBaseAuths.sharingFiles(shareRequest.getFile(),shareRequest.getUserName(),
                    currentDir.resolve(shareRequest.getFile()).toString()));

        }

    }

    private ListFiles refreshList(Path path) throws IOException {
        return new ListFiles(path,sharedFilesList);
    }

    private List<Path> fillSharedFilesList(List<String> list){
        List<Path> files = new ArrayList<>();
        for(String file:list){
            files.add(Path.of(file));
        }
        return files;
    }
}
