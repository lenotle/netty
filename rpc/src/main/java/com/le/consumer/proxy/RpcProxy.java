package com.le.consumer.proxy;

import com.le.consumer.handler.RpcConsumerHandler;
import com.le.core.RpcMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import io.netty.handler.codec.LengthFieldPrepender;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.log4j.Logger;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;

/**
 * @Auther: xll
 * @Desc:
 */
public class RpcProxy {
    private static Logger LOG = Logger.getLogger(RpcProxy.class);

    public static <T> T create(Class<?> clazz) {
        MethodProxy methodProxy = new MethodProxy(clazz);

        return (T) Proxy.newProxyInstance(clazz.getClassLoader(), new Class[]{clazz}, methodProxy);
    }

    static class MethodProxy implements InvocationHandler {
        private Class<?> clazz;

        public MethodProxy(Class<?> clazz) {
            this.clazz = clazz;
        }

        @Override
        public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
            // 如果是已经实现的类，那么直接调用本类
            if (Object.class.equals(method.getDeclaringClass())) {
                return method.invoke(this, args);
            }

            return invoke(method, args);
        }

        /**
         * 远程调用
         *
         * @param method
         * @param args
         * @return
         */
        private Object invoke(Method method, Object[] args) {
            EventLoopGroup worker = new NioEventLoopGroup();
            RpcConsumerHandler rpcConsumerHandler = new RpcConsumerHandler();

            try {
                Bootstrap b = new Bootstrap();
                b.group(worker)
                        .channel(NioSocketChannel.class)
                        .handler(new ChannelInitializer<SocketChannel>() {
                            @Override
                            protected void initChannel(SocketChannel socketChannel) throws Exception {
                                ChannelPipeline pipeline = socketChannel.pipeline();

                                /** 解决TCP通信 粘包、拆包问题 */
                                /** 解码器：读取前四个字节为消息体长度，之后舍弃4字节，即为消息体长度 */
                                pipeline.addLast(new LengthFieldBasedFrameDecoder(Integer.MAX_VALUE, 0, 4, 0, 4));
                                /** 编码器：消息长度固定为4字节--> int */
                                pipeline.addLast(new LengthFieldPrepender(4));

                                /** 对象序列化、反序列化 */
                                pipeline.addLast(new ObjectEncoder());
                                pipeline.addLast(new ObjectDecoder(Integer.MAX_VALUE, ClassResolvers.cacheDisabled(null)));

                                /** 业务处理 */
                                pipeline.addLast(rpcConsumerHandler);
                            }
                        }).option(ChannelOption.TCP_NODELAY, true);
                ChannelFuture f = b.connect("localhost", 8080).sync();
                LOG.info("client has running");

                sendMessage(f.channel(), method, args);
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } finally {
                worker.shutdownGracefully();
            }

            return rpcConsumerHandler.getResult();
        }

        private void sendMessage(Channel channel, Method method, Object[] args) {
            RpcMessage message = new RpcMessage();
            message.setClassName(this.clazz.getName());
            message.setMethodName(method.getName());
            message.setParameters(method.getParameterTypes());
            message.setValues(args);

            channel.writeAndFlush(message);
        }
    }
}
