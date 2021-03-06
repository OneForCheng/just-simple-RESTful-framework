package com.example.JustSimpleRESTfulFramework.server;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import com.example.JustSimpleRESTfulFramework.model.ResponseResult;
import com.example.JustSimpleRESTfulFramework.resource.RequestResolver;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.*;

import static io.netty.handler.codec.http.HttpVersion.*;

public class HttpRequestHandler extends ChannelInboundHandlerAdapter {
    private final RequestResolver requestResolver;

    public HttpRequestHandler(RequestResolver requestResolver) {
        this.requestResolver = requestResolver;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;
            ResponseResult responseResult = requestResolver.resolve(req);
            populateResponse(ctx, responseResult, HttpUtil.isKeepAlive(req));
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

    private void populateResponse(ChannelHandlerContext ctx, ResponseResult res, boolean isKeepAlive) {
        byte[] bytes = JSON.toJSONBytes(res.getResult(), SerializerFeature.EMPTY);
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, res.getStatus(), Unpooled.wrappedBuffer(bytes));
        response.headers().set(HttpHeaderNames.CONTENT_TYPE, "text/json");
        response.headers().setInt(HttpHeaderNames.CONTENT_LENGTH, response.content().readableBytes());
        if (isKeepAlive) {
            response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderNames.KEEP_ALIVE);
            ctx.write(response);
        } else {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        }
    }

}
