package com.example.JustSimpleRESTfulFramework.server;

import com.example.JustSimpleRESTfulFramework.config.BaseServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class HttpServerHandler extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;
    private final BaseServerConfig serverConfig;

    public HttpServerHandler(BaseServerConfig serverConfig, SslContext sslCtx) {
        this.serverConfig = serverConfig;
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(channel.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(serverConfig.getMaxContentLength()));
        pipeline.addLast(new HttpRequestHandler());
    }
}
