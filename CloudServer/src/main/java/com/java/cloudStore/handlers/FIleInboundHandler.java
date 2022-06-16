package com.java.cloudStore.handlers;

import com.java.cloudStore.CloudMessage;
import com.java.cloudStore.FileMessage;
import com.java.cloudStore.FileRequest;
import com.java.cloudStore.ListFiles;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.nio.file.Files;
import java.nio.file.Path;


public class FIleInboundHandler extends SimpleChannelInboundHandler<CloudMessage> {

    private final Path currentDir;

    public FIleInboundHandler() {
        currentDir = Path.of("server_directory");
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(new ListFiles(currentDir));


    }



    @Override
    protected void channelRead0(ChannelHandlerContext ctx, CloudMessage cloudMessage) throws Exception {
        if (cloudMessage instanceof FileRequest fileRequest){
            ctx.writeAndFlush(currentDir.resolve(fileRequest.getName()));
        }else if (cloudMessage instanceof FileMessage fileMessage){
            Files.write(currentDir.resolve(fileMessage.getName()),fileMessage.getData());
            ctx.writeAndFlush(new ListFiles(currentDir));

        }

    }
}
