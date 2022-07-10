package com.le.server.handler;

import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.RandomAccessFile;
import java.net.URL;

/**
 * @Auther: xll
 * @Desc:
 */
public class HttpHandler extends SimpleChannelInboundHandler<FullHttpRequest> {
    private static Logger log = Logger.getLogger(HttpHandler.class);

    // 获取Class路径
    private URL baseURL = HttpHandler.class.getProtectionDomain().getCodeSource().getLocation();
    private final String Default_Root = "public";

    // 根据请求URI返回对应资源句柄
    private File getResource(FullHttpRequest request) throws Exception {
        String uri = request.uri();
        // 访问根目录直接返回首页
        String fileName = "/".equals(uri) ? "chat.html" : uri;
        String path = baseURL.toURI() + Default_Root + "/" + fileName;
        path = path.startsWith("file:") ? path.substring(5) : path;
        path.replaceAll("//", "/");

        return new File(path);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        Channel client = ctx.channel();
        log.info(client.remoteAddress() + " :error");
        cause.printStackTrace();
        ctx.close();
    }

    // 响应 HTTP请求
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        /** 响应静态页面 */
        RandomAccessFile ac = null;

        try {
            ac = new RandomAccessFile(getResource(request), "r");
        } catch (Exception e) {
            ctx.fireChannelRead(request.retain());
            return;
        }
        // 构建HTTP头部
        HttpResponse response = new DefaultHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.OK);
        String uri = request.uri();
//        log.info("uri: " + uri);

        String contentType = "text/html;";

        if (uri.endsWith(".css")) {
            contentType = "text/css;";
        } else if (uri.endsWith(".js")) {
            contentType = "text/javascript;";
        } else if (uri.matches("(jpg|png|gif)$")) {
            contentType = "image/" + (uri.substring(uri.lastIndexOf(".")) + ";");
        }
        response.headers().set(HttpHeaders.Names.CONTENT_TYPE, contentType + "charset=utf-8;");

        boolean keepLived = HttpHeaders.isKeepAlive(request);

        if (keepLived) {
            response.headers().set(HttpHeaders.Names.CONTENT_LENGTH, ac.length());
            response.headers().set(HttpHeaders.Names.CONNECTION, HttpHeaders.Values.KEEP_ALIVE);
        }

        // 响应
        ctx.write(response);
        ctx.write(new DefaultFileRegion(ac.getChannel(), 0, ac.length()));

        ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
        if (!keepLived) {
            future.addListener(ChannelFutureListener.CLOSE);
        }

        ac.close();
    }
}
