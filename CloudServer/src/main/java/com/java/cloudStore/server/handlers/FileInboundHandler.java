package com.java.cloudStore.server.handlers;

import com.java.cloudStore.api.*;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Path;


public class FileInboundHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private  final Path homeDir=Path.of("CloudServer/server_directory");
    private  Path currentDir;

    public FileInboundHandler() {
        currentDir = homeDir;
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        if (Files.isDirectory(currentDir)) {
            ctx.writeAndFlush(new ListFiles(currentDir));
        }else{
            System.out.println("Nothing");
        }

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
                if(newDir.toString().equals(homeDir.getParent().toString())){
                    System.out.println("it is home directory");
                    return;
                }
                currentDir=newDir;
                ctx.writeAndFlush(new ListFiles(currentDir));
            }
        }

    }
}
