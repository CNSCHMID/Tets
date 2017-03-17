package echo.client;

import java.net.InetSocketAddress;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

public class EchoClient {
	private final String host;
	private final int port;
	
	public EchoClient(String host, int port) {
		this.host = host;
		this.port = port;
	}
	
	public void start() throws Exception{
		EventLoopGroup group = new NioEventLoopGroup();
		try{
			Bootstrap b = new Bootstrap();
			b.group(group)
				.channel(NioSocketChannel.class)
				.remoteAddress(new InetSocketAddress(host, port))
				.handler(new ChannelInitializer<SocketChannel>() {

					@Override
					protected void initChannel(SocketChannel ch) throws Exception {
						ch.pipeline().addLast(new EchoClientHandler());						
					}
				});
			ChannelFuture f = b.connect().sync();
			f.channel().closeFuture().sync();			
		}finally{
			group.shutdownGracefully().sync();
		}	
	}
	
	public static void main(String[] args) throws Exception {
		String host = "localhost";
		int port = 8889;
		if (args.length != 2){
			System.err.println("Usage: "+EchoClient.class.getSimpleName()+" host port");
			System.err.println("Using default port "+port+" on "+host);
		}else{
			host = args[0];
			port = Integer.parseInt(args[1]);
		}
		new EchoClient(host, port).start();
	}
	

}
