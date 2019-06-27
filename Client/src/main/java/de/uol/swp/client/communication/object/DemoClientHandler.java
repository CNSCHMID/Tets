package communication.object;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import message.IMessage;

/**
 * Netty handler for incoming connections
 * 
 * @author Marco Grawunder
 *
 */
public class DemoClientHandler extends ChannelInboundHandlerAdapter {

	private DemoClient demoClient;

	public DemoClientHandler(DemoClient demoClient) {
		this.demoClient = demoClient;
	}

	@Override
	public void channelActive(ChannelHandlerContext ctx) throws Exception {
		System.out.println("Connected to server " + ctx);
		demoClient.fireConnectionEstablished(ctx.channel());
	}

	@Override
	public void channelRead(ChannelHandlerContext ctx, Object in) throws Exception {
		if (in instanceof IMessage) {
			demoClient.receivedMessage((IMessage) in);
		}else{
			System.err.println("Illegal Object read from channel. Ignored!");
		}
	}

	@Override
	public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
		demoClient.process(cause);
		ctx.close();
	}
}
