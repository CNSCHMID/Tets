package de.uol.swp.server.communication;

import de.uol.swp.common.message.MessageContext;
import io.netty.channel.ChannelHandlerContext;

class NettyMessageContext implements MessageContext {

    private final ChannelHandlerContext ctx;

    public NettyMessageContext(ChannelHandlerContext ctx) {
        this.ctx = ctx;
    }

    ChannelHandlerContext getCtx() {
        return ctx;
    }
}
