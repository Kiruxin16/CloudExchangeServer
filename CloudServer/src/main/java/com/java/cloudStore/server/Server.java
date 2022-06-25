package com.java.cloudStore.server;


import com.java.cloudStore.server.handlers.FileInboundHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;

public class Server {
    public Server() {
        EventLoopGroup auth = new NioEventLoopGroup();
        EventLoopGroup work = new NioEventLoopGroup();

        try{


            ServerBootstrap serv = new ServerBootstrap();
            serv.group(auth,work)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(
                                    new ObjectDecoder(ClassResolvers.cacheDisabled(null)),
                                    new ObjectEncoder(),
                                    new FileInboundHandler()
                            );

                        }
                    });
                    ChannelFuture future =serv.bind(8189).sync();
                    future.channel().closeFuture().addListener((cf)-> System.out.println("Client  disconnected"));

                    future.channel().closeFuture().sync();

        }catch (Exception exception){
            exception.printStackTrace();

        }finally {
            auth.shutdownGracefully();
            work.shutdownGracefully();
        }
    }
}
