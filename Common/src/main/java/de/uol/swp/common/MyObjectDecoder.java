package de.uol.swp.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.serialization.ClassResolver;
import io.netty.handler.codec.serialization.ObjectDecoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class MyObjectDecoder extends ObjectDecoder {

    private static final Logger LOG = LogManager.getLogger(MyObjectDecoder.class);

    public MyObjectDecoder(ClassResolver classResolver) {
        super(classResolver);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        if (LOG.isTraceEnabled()) {
            LOG.trace("Trying to decode " + in);
        }
        Object decoded = super.decode(ctx, in);
        if (LOG.isTraceEnabled()) {
            LOG.trace(in + " " + decoded);
        }
        return decoded;
    }
}
