package com.java.cloudStore.server.handlers;

import com.java.cloudStore.api.*;
import com.java.cloudStore.server.database.DBHandler;
import com.java.cloudStore.server.database.DataBaseAuth;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Path;


public class FileInboundHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private  final Path homeDir=Path.of("CloudServer/server_directory");
    private  Path currentDir;
    private DataBaseAuth dataBaseAuths;

    public FileInboundHandler() {
        currentDir = homeDir;
        dataBaseAuths = new DataBaseAuth();
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
/*        if (Files.isDirectory(currentDir)) {
            ctx.writeAndFlush(new ListFiles(currentDir));
        }else{
            System.out.println("Nothing");
        }*/

    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest){
            ctx.writeAndFlush(new FileMessage(currentDir.resolve(fileRequest.getName())) );
        }else if (cloudMessage instanceof FileMessage fileMessage){
            Files.write(currentDir.resolve(fileMessage.getName()),fileMessage.getData());
            ctx.writeAndFlush(new ListFiles(currentDir));
        }else if(cloudMessage instanceof NavigateMessage navigateMessage){
            Path newDir =currentDir.resolve(navigateMessage.getNewFolder()).normalize();
            if (Files.isDirectory(newDir)){
                if(newDir.toString().equals(homeDir.toString())){
                    System.out.println("it is home directory");
                    return;
                }
                currentDir=newDir;
                ctx.writeAndFlush(new ListFiles(currentDir));
            }
        }else if(cloudMessage instanceof AuthRequest authRequest){
            ctx.writeAndFlush(dataBaseAuths.tryToAuth(authRequest.getLogin(), authRequest.getPass()));

        }else if (cloudMessage instanceof RegRequest regRequest){
            RegResponse registrated =  dataBaseAuths.tryToReg(regRequest.getLogin(),regRequest.getPass());
            if (registrated.isSuccess()){
                Files.createDirectory(homeDir.resolve(regRequest.getLogin()));
            }
            ctx.writeAndFlush(registrated);
        }

    }
}
