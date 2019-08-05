package de.uol.swp.client.communication.object;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;

import de.uol.swp.client.demo.IConnectionListener;
import de.uol.swp.common.exception.ExceptionMessage;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import de.uol.swp.common.message.IMessage;

/**
 * The Client Connection class
 * @author Marco Grawunder
 *
 */

public class Client {
	private final String host;
	private final int port;
	private List<IConnectionListener> connectionListener = new CopyOnWriteArrayList<>();
	private EventLoopGroup group;
	private final EventBus eventBus;

	/**
	 * Create a new connection to a specific port on the given host
	 * @param host The server name to connect to
	 * @param port The server port to connect to
	 * @param eventBus for handling messages
	 */
	public Client(String host, int port, EventBus eventBus) {
		this.host = host;
		this.port = port;
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	/**
	 * The netty init method
	 * @throws Exception
	 */
	public void start() throws Exception {
		group = new NioEventLoopGroup();
		try {
			Bootstrap b = new Bootstrap();
			b.group(group).channel(NioSocketChannel.class).remoteAddress(new InetSocketAddress(host, port))
					.handler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// Add both Encoder and Decoder to send and receive serializable objects
							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
							// Add a client handler
							ch.pipeline().addLast(new ClientHandler(Client.this));
						}
					});
			ChannelFuture f = b.connect().sync();
			f.channel().closeFuture().sync();
		} finally {
			group.shutdownGracefully().sync();
		}
	}

	public void close() {
		try {
			group.shutdownGracefully().sync();
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}


	protected void fireConnectionEstablished(Channel channel) {
		for (IConnectionListener listener : connectionListener) {
			listener.connectionEstablished(channel);
		}
	}

	public void addConnectionListener(IConnectionListener listener) {
		this.connectionListener.add(listener);
	}


	public void receivedMessage(IMessage in) {
		eventBus.post(in);
	}

	@Subscribe
	public void process(ExceptionMessage message) {
		for (IConnectionListener l:connectionListener){
			l.exceptionOccured(message.getException());
		}
	}

	@Subscribe
	private void handleEventBusError(DeadEvent deadEvent){
		System.err.println("DeadEvent detected "+deadEvent);
	}

	public void process(Throwable message) {
		for (IConnectionListener l:connectionListener){
			l.exceptionOccured(message);
		}
	}


}
