package com.le.server;

import com.le.protocol.IMDecoder;
import com.le.protocol.IMEncoder;
import com.le.server.handler.HttpHandler;
import com.le.server.handler.SocketHandler;
import com.le.server.handler.WebSocketHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import org.apache.log4j.Logger;

/**
 * @Auther: xll
 * @Desc:
 */
public class ChatServer {
    private final Logger log = Logger.getLogger(ChatServer.class);

    public void start(int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG,1024)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            ChannelPipeline line = socketChannel.pipeline();
                            /** 解析自定义协议 */
                            line.addLast(new IMDecoder());
                            line.addLast(new IMEncoder());
                            line.addLast(new SocketHandler());

                            /** 支持HTTP协议 */
                            line.addLast(new HttpServerCodec());
                            // 主要是将同一个http请求或响应的多个消息对象变成一个 fullHttpRequest完整的消息对象
                            line.addLast(new HttpObjectAggregator(64 * 1024));
                            // 主要用于处理大数据流,比如一个1G大小的文件如果你直接传输肯定会撑暴jvm内存的 ,加上这个handler我们就不用考虑这个问题了
                            line.addLast(new ChunkedWriteHandler());
                            line.addLast(new HttpHandler());

                            /** 支持WebSocket协议 */
                            line.addLast(new WebSocketServerProtocolHandler("/im"));
                            line.addLast(new WebSocketHandler());


                        }
                    });
            ChannelFuture cf = server.bind(port).sync();
            log.info("server has running, listen on：" + port);
            cf.channel().closeFuture().sync();
        }catch (Exception e) {
            e.printStackTrace();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

    public static void main(String[] args) {
        new ChatServer().start(80);
    }
}
