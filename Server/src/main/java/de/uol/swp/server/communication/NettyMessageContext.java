package de.uol.swp.server.communication;

import de.uol.swp.common.message.MessageContext;
import io.netty.channel.ChannelHandlerContext;

public class NettyMessageContext implements MessageContext {


    private final ChannelHandlerContext ctx;

    public NettyMessageContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }
}
