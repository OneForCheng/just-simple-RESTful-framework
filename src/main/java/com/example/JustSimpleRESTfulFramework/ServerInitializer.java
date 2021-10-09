package com.example.JustSimpleRESTfulFramework;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.ssl.SslContext;

public class ServerInitializer extends ChannelInitializer<SocketChannel> {
    private final SslContext sslCtx;

    public ServerInitializer(SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }

    @Override
    public void initChannel(SocketChannel channel) {
        ChannelPipeline pipeline = channel.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(channel.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());// HTTP 服务的解码器
        pipeline.addLast(new HttpObjectAggregator(2048));// HTTP 消息的合并处理
        pipeline.addLast(new CustomizedHttpHandler()); // 自定义服务器逻辑处理
    }
}
