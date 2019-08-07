package de.uol.swp.server.communication;

import io.netty.channel.ChannelHandlerContext;

public class ConnectionWrapper extends Session {

    private final ChannelHandlerContext ctx;

    public ConnectionWrapper(ChannelHandlerContext ctx){
        super(false);
        this.ctx = ctx;
    }

    public ChannelHandlerContext getCtx() {
        return ctx;
    }

}
