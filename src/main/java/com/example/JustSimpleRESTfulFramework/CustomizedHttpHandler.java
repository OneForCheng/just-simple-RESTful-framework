package com.example.JustSimpleRESTfulFramework;

import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.handler.codec.http.DefaultFullHttpResponse;
import io.netty.handler.codec.http.FullHttpRequest;
import io.netty.handler.codec.http.FullHttpResponse;
import io.netty.handler.codec.http.HttpUtil;
import io.netty.util.AsciiString;
import io.netty.util.CharsetUtil;

import static io.netty.handler.codec.http.HttpResponseStatus.*;
import static io.netty.handler.codec.http.HttpVersion.*;

import org.json.JSONObject;

public class CustomizedHttpHandler extends ChannelInboundHandlerAdapter {
    private static final AsciiString CONTENT_TYPE = new AsciiString("Content-Type");
    private static final AsciiString CONTENT_LENGTH = new AsciiString("Content-Length");
    private static final AsciiString CONNECTION = new AsciiString("Connection");
    private static final AsciiString KEEP_ALIVE = new AsciiString("keep-alive");

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        cause.printStackTrace();
        ctx.close();
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) {
        if (msg instanceof FullHttpRequest) {
            FullHttpRequest req = (FullHttpRequest) msg;//客户端的请求对象
            JSONObject responseJson = new JSONObject();//新建一个返回消息的Json对象
            //把客户端的请求数据格式化为Json对象
            JSONObject requestJson = null;
            try {
                requestJson = getJsonRequestContent(req);
            } catch (Exception e) {
                populateJsonResponse(ctx, req, "error json");
                return;
            }
            String uri = req.uri();//获取客户端的URL
            //TODO: 根据不同的请求API做不同的处理(路由分发)

            //向客户端发送结果
            populateJsonResponse(ctx, req, responseJson.toString());
        }
    }

    private void populateJsonResponse(ChannelHandlerContext ctx, FullHttpRequest req, String jsonStr) {
        boolean keepAlive = HttpUtil.isKeepAlive(req);
        byte[] jsonByteByte = jsonStr.getBytes();
        FullHttpResponse response = new DefaultFullHttpResponse(HTTP_1_1, OK, Unpooled.wrappedBuffer(jsonByteByte));
        response.headers().set(CONTENT_TYPE, "text/json");
        response.headers().setInt(CONTENT_LENGTH, response.content().readableBytes());
        if (!keepAlive) {
            ctx.write(response).addListener(ChannelFutureListener.CLOSE);
        } else {
            response.headers().set(CONNECTION, KEEP_ALIVE);
            ctx.write(response);
        }
    }

    private JSONObject getJsonRequestContent(FullHttpRequest request) {
        String content = request.content().toString(CharsetUtil.UTF_8);
        return new JSONObject(content);
    }
}
