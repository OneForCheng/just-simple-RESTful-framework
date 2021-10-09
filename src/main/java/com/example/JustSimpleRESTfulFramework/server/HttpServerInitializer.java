package com.example.JustSimpleRESTfulFramework.server;

import com.example.JustSimpleRESTfulFramework.config.ServerConfig;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class HttpServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public HttpServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(channel.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());// HTTP 服务的解码器
        pipeline.addLast(new HttpObjectAggregator(ServerConfig.getMaxContentLength()));// HTTP 消息的合并处理
        pipeline.addLast(new HttpRequestHandler()); // 自定义服务器逻辑处理
    }
}
