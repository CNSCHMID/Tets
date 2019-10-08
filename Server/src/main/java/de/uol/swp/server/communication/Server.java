package de.uol.swp.server.communication;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import com.google.inject.Inject;
import de.uol.swp.common.MyObjectDecoder;
import de.uol.swp.common.MyObjectEncoder;
import de.uol.swp.common.message.*;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.message.UserLoggedInMessage;
import de.uol.swp.common.user.message.UserLoggedOutMessage;
import de.uol.swp.common.user.response.LoginSuccessfulMessage;
import de.uol.swp.server.message.ClientAuthorizedMessage;
import de.uol.swp.server.message.ClientDisconnectedMessage;
import de.uol.swp.server.message.ServerExceptionMessage;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.serialization.ClassResolvers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements ServerHandlerDelegate {

	private static final Logger LOG = LogManager.getLogger(Server.class);

	/**
	 * Clients that are connected
	 */
	final private List<ChannelHandlerContext> connectedClients = new CopyOnWriteArrayList<>();

	/**
	 * Client with logged in sessions
	 */
	final private Map<ChannelHandlerContext, Session> activeSessions = new HashMap<>();

	/**
     * Event bus (injected)
	 */
	final private EventBus eventBus;

	/**
     * Creates a new Server Object
	 */
    @Inject
    public Server(EventBus eventBus) {
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	/**
     * Start a new server on given port
	 *
	 * @throws Exception
	 */
    public void start(int port) throws Exception {
		final ServerHandler serverHandler = new ServerHandler(this);
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) {
							// Encoder and decoder are both needed! Send and
							// receive serializable objects
							ch.pipeline().addLast(new MyObjectEncoder());
							ch.pipeline().addLast(new MyObjectDecoder(ClassResolvers.cacheDisabled(null)));
							// must be last in the pipeline else they will not
							// get encoded/decoded objects but ByteBuf
							ch.pipeline().addLast(serverHandler);
						}

					});
			// Just wait for server shutdown
			ChannelFuture f = b.bind().sync();
			f.channel().closeFuture().sync();
		} finally {
			bossGroup.shutdownGracefully().sync();
			workerGroup.shutdownGracefully().sync();
		}
	}

	// Called from ServerHandler
	@Override
	public void process(ChannelHandlerContext ctx, RequestMessage msg) {
		LOG.debug("Received new message from client "+msg);
		try {
			msg.setMessageContext(new NettyMessageContext(ctx));
			checkIfMessageNeedsAuthorization(ctx, msg);
			eventBus.post(msg);
		} catch (Exception e) {
			LOG.error("ServerException " + e.getClass().getName() + " " + e.getMessage());
			sendToClient(ctx, new ExceptionMessage(e.getMessage()));
		}
	}

	private void checkIfMessageNeedsAuthorization(ChannelHandlerContext ctx, RequestMessage msg) {
		if (msg.authorizationNeeded() ) {
			if (getSession(ctx).isEmpty()) {
				throw new SecurityException("Authorization required. Client not logged in!");
			}
			msg.setSession(getSession(ctx).get());
		}
	}

	@Subscribe
	private void onServerException(ServerExceptionMessage msg){
		Optional<ChannelHandlerContext> ctx = getCtx(msg);
		LOG.error(msg.getException());
		ctx.ifPresent(channelHandlerContext -> sendToClient(channelHandlerContext, new ExceptionMessage(msg.getException().getMessage())));
	}

	@Subscribe
	private void handleEventBusError(DeadEvent deadEvent){
		LOG.error("DeadEvent detected "+deadEvent);
	}


	// -------------------------------------------------------------------------------
	// Handling of connected clients (from netty)
	// -------------------------------------------------------------------------------
	@Override
	public void newClientConnected(ChannelHandlerContext ctx) {
		LOG.debug("New client " + ctx + " connected");
		connectedClients.add(ctx);
	}

	@Override
	public void clientDisconnected(ChannelHandlerContext ctx) {
		LOG.debug("Client disconnected");
		Session session = this.activeSessions.get(ctx);
		if (session != null) {
			ClientDisconnectedMessage msg = new ClientDisconnectedMessage();
			msg.setSession(session);
			eventBus.post(msg);
			removeSession(ctx);
		}
		connectedClients.remove(ctx);
	}

	// -------------------------------------------------------------------------------
	// User Management Events (from event bus)
	// -------------------------------------------------------------------------------
	@Subscribe
	private void onClientAuthorized(ClientAuthorizedMessage msg){
		Optional<ChannelHandlerContext> ctx = getCtx(msg);
		if (ctx.isPresent()) {
			putSession(ctx.get(), msg.getSession().get());
			sendToClient(ctx.get(), new LoginSuccessfulMessage(msg.getUser()));
			sendMessage(new UserLoggedInMessage(msg.getUser().getUsername()));
		}else{
			LOG.warn("No context for "+msg);
		}
	}

	@Subscribe
	private void onUserLoggedOutMessage(UserLoggedOutMessage msg){
		Optional<ChannelHandlerContext> ctx = getCtx(msg);
		if (ctx.isPresent()){
			removeSession(ctx.get());
		}
		sendMessage(msg);
	}

	// -------------------------------------------------------------------------------
	// ResponseEvents
	// -------------------------------------------------------------------------------

	@Subscribe
	private void onResponseMessage(ResponseMessage msg){
		Optional<ChannelHandlerContext> ctx = getCtx(msg);
		if (ctx.isPresent()) {
			msg.setSession(null);
			msg.setMessageContext(null);
			LOG.debug("Send to client "+ctx.get()+" message "+ msg);
			sendToClient(ctx.get(), msg);
		}
	}

	// -------------------------------------------------------------------------------
	// ServerMessages
	// -------------------------------------------------------------------------------

	@Subscribe
	private void onServerMessage(ServerMessage msg){
		msg.setSession(null);
		msg.setMessageContext(null);
		if (LOG.isDebugEnabled()) {
			LOG.debug("Send " + msg + " to " + (msg.getReceiver().isEmpty() ? "all" : msg.getReceiver()));
		}
		sendMessage(msg);
	}


	// -------------------------------------------------------------------------------
	// Session Management (helper methods)
	// -------------------------------------------------------------------------------

	private void putSession(ChannelHandlerContext ctx, Session newSession) {

		// TODO: check if session is already bound to connection
		activeSessions.put(ctx, newSession);
	}

	private void removeSession(ChannelHandlerContext ctx){
		activeSessions.remove(ctx);
	}

	private Optional<Session> getSession(ChannelHandlerContext ctx) {
		Session session = activeSessions.get(ctx);
		return session != null? Optional.of(session):Optional.empty();
	}

	private Optional<ChannelHandlerContext> getCtx(Message message){
		if (message.getMessageContext().isPresent() && message.getMessageContext().get() instanceof NettyMessageContext){
			return Optional.of(((NettyMessageContext) message.getMessageContext().get()).getCtx());
		}
		if (message.getSession().isPresent()){
			return getCtx(message.getSession().get());
		}
		return Optional.empty();
	}

	private Optional<ChannelHandlerContext> getCtx(Session session){
		for(Map.Entry<ChannelHandlerContext, Session> e : activeSessions.entrySet()){
			if (e.getValue().equals(session)){
				return Optional.of(e.getKey());
			}
		}
		return Optional.empty();
	}

	private List<ChannelHandlerContext> getCtx(List<Session> receiver) {
		List<ChannelHandlerContext> ctxs = new ArrayList<>();
		receiver.forEach(r -> {
			Optional<ChannelHandlerContext> s = getCtx(r);
			if (s.isPresent()) {
				ctxs.add(s.get());
			}
		});
		return ctxs;
	}


	// -------------------------------------------------------------------------------
	// Help methods: Send only objects of type Message
	// -------------------------------------------------------------------------------

	private void sendToClient(ChannelHandlerContext ctx, ResponseMessage message) {
		LOG.trace("Trying to sendMessage to client: " + ctx + " " + message);
		ctx.writeAndFlush(message);
	}

	private void sendMessage(ServerMessage msg) {
		if (msg.getReceiver().isEmpty()) {
			sendToMany(connectedClients, msg);
		} else {
			sendToMany(getCtx(msg.getReceiver()), msg);
		}
	}

	private void sendToMany(List<ChannelHandlerContext> sendTo, ServerMessage msg){
		for (ChannelHandlerContext client : sendTo) {
			try {
				client.writeAndFlush(msg);
			} catch (Exception e) {
				// TODO: Handle exception for unreachable clients
				e.printStackTrace();
			}
		}
	}


}
