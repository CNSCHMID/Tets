package de.uol.swp.server.communication;

import de.uol.swp.common.message.MessageContext;
import io.netty.channel.ChannelHandlerContext;

import java.util.ArrayList;
import java.util.List;

class NettyMessageContext implements MessageContext {

    private final List<ChannelHandlerContext> ctx = new ArrayList<>();

    public NettyMessageContext(ChannelHandlerContext ctx) {
        this.ctx.add(ctx);
    }

    ChannelHandlerContext getCtx() {
        return ctx.get(0);
    }

    List<ChannelHandlerContext> getAllCtx() {
        return ctx;
    }

    public NettyMessageContext(List<ChannelHandlerContext> ctx) {
        this.ctx.addAll(ctx);
    }
}
