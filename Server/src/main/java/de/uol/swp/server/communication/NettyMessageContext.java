package de.uol.swp.server.communication;

import de.uol.swp.common.message.IMessageContext;
import io.netty.channel.ChannelHandlerContext;

public class NettyMessageContext implements IMessageContext {


    private final ChannelHandlerContext ctx;

    public NettyMessageContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }
}
