package com.example.JustSimpleRESTfulFramework.server;

import com.example.JustSimpleRESTfulFramework.config.BaseHttpServerConfig;
import com.example.JustSimpleRESTfulFramework.exception.HttpServerException;
import com.example.JustSimpleRESTfulFramework.resource.RequestResolver;
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
    private final BaseHttpServerConfig httpServerConfig;
    private final EventLoopGroup bossGroup = new NioEventLoopGroup(1);
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public HttpServer(BaseHttpServerConfig httpServerConfig) {
        this.httpServerConfig = httpServerConfig;
    }

    public void run(RequestResolver requestResolver) {
        SslContext sslContext = getSslContext();

        try {
            ServerBootstrap serverBootstrap = new ServerBootstrap();
            serverBootstrap.option(ChannelOption.SO_BACKLOG, 1024);
            serverBootstrap.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .handler(new LoggingHandler(LogLevel.INFO))
                    .childHandler(new HttpServerHandler(httpServerConfig, sslContext, requestResolver));
            Channel channel = serverBootstrap.bind(httpServerConfig.getPort()).sync().channel();
            System.out.printf("Open your web browser and navigate to %s://127.0.0.1:%s%n", httpServerConfig.getProtocol(), httpServerConfig.getPort());
        } catch (InterruptedException e) {
            throw new HttpServerException("bootstrap server failed.", e);
        }
    }

    public void close() {
        bossGroup.shutdownGracefully();
        workerGroup.shutdownGracefully();
    }

    private SslContext getSslContext() {
        if (!httpServerConfig.isSSL()) return null;
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
