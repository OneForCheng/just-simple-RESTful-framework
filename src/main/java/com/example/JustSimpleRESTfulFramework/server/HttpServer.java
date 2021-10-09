package com.example.JustSimpleRESTfulFramework.server;

import com.example.JustSimpleRESTfulFramework.config.ServerConfig;
import com.example.JustSimpleRESTfulFramework.exception.HttpServerException;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;
import io.netty.handler.ssl.util.SelfSignedCertificate;

public final class HttpServer {
    public void run() {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        SslContext sslContext = getSslContext();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpServerInitializer(sslContext));
            Channel channel = serverBootstrap.bind(ServerConfig.getPort()).sync().channel();
            channel.closeFuture().sync();
            System.out.printf("Open your web browser and navigate to %s://127.0.0.1:%s%n", ServerConfig.getProtocol(), ServerConfig.getPort());
        } catch (InterruptedException e) {
            throw new HttpServerException("bootstrap server failed.", e);
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    private SslContext getSslContext() {
        if (ServerConfig.isSSL()) return null;
        SslContext sslCtx;
        try {
            SelfSignedCertificate ssc = new SelfSignedCertificate();
            sslCtx = SslContextBuilder.forServer(ssc.certificate(), ssc.privateKey()).build();
        } catch (Exception e) {
            throw new HttpServerException("create ssl context failed.", e);
        }
        return sslCtx;
    }
}
