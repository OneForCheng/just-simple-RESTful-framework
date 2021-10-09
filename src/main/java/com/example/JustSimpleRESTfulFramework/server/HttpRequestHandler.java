package com.example.JustSimpleRESTfulFramework.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.JustSimpleRESTfulFramework.config.ResponseConfig;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

public class HttpRequestHandler extends ChannelInboundHandlerAdapter {
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            String uri = req.uri();
            String result = uri;
            populateResponse(ctx, req, result);
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    private void populateResponse(ChannelHandlerContext ctx, FullHttpRequest req, Object content) {
        boolean isKeepAlive = HttpUtil.isKeepAlive(req);
        byte[] bytes = JSON.toJSONBytes(content, SerializerFeature.EMPTY);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(bytes));
        response.headers().set(ResponseConfig.CONTENT_TYPE, "text/json");
        response.headers().setInt(ResponseConfig.CONTENT_LENGTH, response.content().readableBytes());
        if (isKeepAlive) {
            response.headers().set(ResponseConfig.CONNECTION, ResponseConfig.KEEP_ALIVE);
            ctx.write(response);
        } else {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
