package de.uol.swp.client;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import com.google.inject.assistedinject.Assisted;
import de.uol.swp.common.MyObjectDecoder;
import de.uol.swp.common.message.*;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

/**
 * The ClientConnection Connection class
 * @author Marco Grawunder
 *
 */

public class ClientConnection {

	private static final Logger LOG = LogManager.getLogger(ClientConnection.class);

	private final String host;
	private final int port;
	private final List<ConnectionListener> connectionListener = new CopyOnWriteArrayList<>();
	private EventLoopGroup group;
	private EventBus eventBus;
	private Channel channel;

	/**
	 * Create a new connection to a specific port on the given host
	 * @param host The server name to connect to
	 * @param port The server port to connect to
	 */
	@Inject
	public ClientConnection(@Assisted String host, @Assisted int port, EventBus eventBus) {
		this.host = host;
		this.port = port;
		setEventBus(eventBus);
	}

	public void setEventBus(EventBus eventBus) {
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
						protected void initChannel(SocketChannel ch) {
							// Add both Encoder and Decoder to send and receive serializable objects
							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline().addLast(new MyObjectDecoder(ClassResolvers.cacheDisabled(null)));
							// Add a client handler
							ch.pipeline().addLast(new ClientHandler(ClientConnection.this));
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


	void fireConnectionEstablished(Channel channel) {
		for (ConnectionListener listener : connectionListener) {
			listener.connectionEstablished(channel);
		}
		this.channel = channel;
	}

	public void addConnectionListener(ConnectionListener listener) {
		this.connectionListener.add(listener);
	}


	public void receivedMessage(Message in) {
		if (in instanceof ServerMessage || in instanceof ResponseMessage) {
			LOG.debug("Received message. Post on event bus " + in);
			eventBus.post(in);
		}else{
			LOG.warn("Can only process ServerMessage and ResponseMessage. Received "+in);
		}
	}

	@Subscribe
	public void onRequestMessage(RequestMessage message){
		if (channel != null){
			channel.writeAndFlush(message);
		}else{
			LOG.warn("Some tries to send a message, but server is not connected");
			// TODO: may create stack trace?
		}
	}

	@Subscribe
	public void onExceptionMessage(ExceptionMessage message) {
		for (ConnectionListener l : connectionListener) {
			l.exceptionOccured(message.getException());
		}
	}

	@Subscribe
	public void onDeadEvent(DeadEvent deadEvent){
		LOG.warn("DeadEvent detected "+deadEvent);
	}

	public void process(Throwable message) {
		for (ConnectionListener l : connectionListener) {
			l.exceptionOccured(message.getMessage());
		}
	}


}
