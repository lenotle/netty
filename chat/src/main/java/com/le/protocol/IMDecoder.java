package com.le.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import org.msgpack.MessagePack;

import java.io.IOException;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @Auther: xll
 * @Desc: 自定义消息解码器
 */
public class IMDecoder extends ByteToMessageDecoder {
    // 解析IM写一下请求内容的正则
    private Pattern pattern = Pattern.compile("^\\[(.*)\\](\\s\\-\\s(.*))?");

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf byteBuf, List<Object> list) {
        try {
            final int length = byteBuf.readableBytes();
            final byte[] array = new byte[length];
            String content = new String(array, byteBuf.readerIndex(), length);

            // 空消息或者不是协议定义的消息 不予解析
            if (!(null == content || "".equals(content.trim()))) {
                if (!IMP.isIMP(content)) {
                    ctx.channel().pipeline().remove(this);
                    return;
                }
            }

            byteBuf.getBytes(byteBuf.readerIndex(), array, 0, length);
            try {
                list.add(new MessagePack().read(array, IMMessage.class));
            } catch (IOException e) {
                e.printStackTrace();
            }
            byteBuf.clear();
        } catch (Exception e) {
            ctx.channel().pipeline().remove(this);
        }
    }

    /**
     * 客户端发送消息解码
     *
     * @param msg 客户端发送消息
     * @return 自定义协议实体
     */
    public IMMessage decode(String msg) {
        if (null == msg || "".equals(msg.trim())) {
            return null;
        }

        try {
            Matcher matcher = pattern.matcher(msg);
            String header = "";
            String content = "";
            if (matcher.matches()) {
                header = matcher.group(1);
                content = matcher.group(3);
            }

            String[] headers = header.split("\\]\\[");
            long time = 0;
            try {
                time = Long.parseLong(headers[1]);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
            // 可以判断昵称是否合法，这儿省略
            String nickName = headers[2];

            if (msg.startsWith("[" + IMP.LOGIN.getName() + "]")) {
                return new IMMessage(headers[0], time, nickName);
            } else if (msg.startsWith("[" + IMP.CHAT.getName() + "]")) {
                return new IMMessage(headers[0], time, nickName, content);
            } else if (msg.startsWith("[" + IMP.FLOWER.getName() + "]")) {
                return new IMMessage(headers[0], time, nickName);
            } else {
                return null;
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }
    }
}
