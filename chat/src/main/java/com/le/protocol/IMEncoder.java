package com.le.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.msgpack.MessagePack;

/**
 * @Auther: xll
 * @Desc: IM协议编码器
 */
public class IMEncoder extends MessageToByteEncoder<IMMessage> {
    @Override
    protected void encode(ChannelHandlerContext ctx, IMMessage message,
                          ByteBuf buffer) throws Exception {
        buffer.writeBytes(new MessagePack().write(message));
    }

    /**
     * 对服务端发送内容进行编码
     *
     * @param message 自定义协议实体
     * @return 协议格式 [SYSTEM][][]...
     */
    public String encode(IMMessage message) {
        if (null == message) {
            return "";
        }

        String prex = "[" + message.getCmd() + "]" + "[" + message.getTime() + "]";
        if (IMP.LOGIN.getName().equals(message.getCmd()) ||
                IMP.CHAT.getName().equals(message.getCmd()) ||
                IMP.FLOWER.getName().equals(message.getCmd())) {
            prex += ("[" + message.getSender() + "]");
        } else if (IMP.SYSTEM.getName().equals(message.getCmd())) {
            prex += ("[" + message.getOnline() + "]");
        }

        if (!(null == message.getContent() || "".equals(message.getContent()))) {
            prex += (" - " + message.getContent());
        }

        return prex;
    }
}
