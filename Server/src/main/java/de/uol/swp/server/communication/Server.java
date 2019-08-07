package de.uol.swp.server.communication;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.message.ExceptionMessage;
import de.uol.swp.common.message.IRequestMessage;
import de.uol.swp.common.message.IResponseMessage;
import de.uol.swp.common.message.IServerMessage;
import de.uol.swp.common.user.ISession;
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
import io.netty.handler.codec.serialization.ObjectDecoder;
import io.netty.handler.codec.serialization.ObjectEncoder;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements ServerHandlerDelegate {

	static final Logger LOG = LogManager.getLogger(Server.class);

	/**
	 * Server port
	 */
	final private int port;

	/**
	 * Clients that are connected
	 */
	final private List<ChannelHandlerContext> connectedClients = new CopyOnWriteArrayList<>();

	/**
	 * Client with logged in sessions
	 */
	final private Map<ChannelHandlerContext, ISession> activeSessions = new HashMap<>();

	/**
	 * For demo reasons the eventBus as part of this class
	 */
	final private EventBus eventBus;

	/**
	 * Creates a new Server Object and start listening on given port
	 *
	 * @param port
	 *            The port the server should listen for new connection

	 */
	public Server(int port, EventBus eventBus) {
		this.port = port;
		// TODO: Ping clients
		this.eventBus = eventBus;
		eventBus.register(this);
	}

	/**
	 * Initialize netty
	 *
	 * @throws Exception
	 */
	public void start() throws Exception {
		final ServerHandler serverHandler = new ServerHandler(this);
		EventLoopGroup bossGroup = new NioEventLoopGroup(1);
		EventLoopGroup workerGroup = new NioEventLoopGroup();
		try {
			ServerBootstrap b = new ServerBootstrap();
			b.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class)
					.localAddress(new InetSocketAddress(port)).childHandler(new ChannelInitializer<SocketChannel>() {

						@Override
						protected void initChannel(SocketChannel ch) throws Exception {
							// Encoder and decoder are both needed! Send and
							// receive serializable objects
							ch.pipeline().addLast(new ObjectEncoder());
							ch.pipeline().addLast(new ObjectDecoder(ClassResolvers.cacheDisabled(null)));
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
	public void process(ChannelHandlerContext ctx, IRequestMessage msg) {

		try {
			// check if msg requires login and append session if available
			if (msg.authorizationNeeded() ) {
				if (!getSession(ctx).isPresent()) {
					throw new SecurityException("Authorization required. Client not logged in!");
				}
				msg.setSession(getSession(ctx).get());
			}else{
				// In case of exception without session, response channel is needed
				msg.setSession(new ConnectionWrapper(ctx));
			}
			eventBus.post(msg);

		} catch (Exception e) {
			LOG.error("ServerException " + e.getClass().getName() + " " + e.getMessage());
			sendToClient(ctx, new ExceptionMessage(e.getMessage()));
		}
	}

	@Subscribe
	private void onServerException(ServerExceptionMessage msg){
		Optional<ChannelHandlerContext> ctx = getCtx(msg.getSession());
		LOG.error(msg.getException());
		if (ctx.isPresent()) {
			sendToClient(ctx.get(), new ExceptionMessage(msg.getException().getMessage()));
		}
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
		System.err.println("New client " + ctx + " connected");
		connectedClients.add(ctx);
	}

	@Override
	public void clientDisconnected(ChannelHandlerContext ctx) {
		System.err.println("Client disconnected");
		ISession session = this.activeSessions.get(ctx);
		if (session != null) {
			ClientDisconnectedMessage msg = new ClientDisconnectedMessage();
			msg.setSession(session);
			eventBus.post(msg);
			removeSession(ctx);
		}
		connectedClients.remove(ctx);
	}

	// -------------------------------------------------------------------------------
	// Session Management Events (from event bus)
	// -------------------------------------------------------------------------------
	@Subscribe
	public void onClientAuthorized(ClientAuthorizedMessage msg){
		Optional<ChannelHandlerContext> ctx = getCtx(msg.getSession());
		if (ctx.isPresent()) {
			putSession(ctx.get(), msg.getSession());
			sendToClient(ctx.get(), new LoginSuccessfulMessage(msg.getUser()));
			sendToAll(new UserLoggedInMessage(msg.getUser().getUsername()));
		}else{
			// TODO: Warning
		}
	}

	@Subscribe
	public void onClientLoggedOut(UserLoggedOutMessage msg){
		// do not send Session to client!
		sendToAll(new UserLoggedOutMessage(msg.getUsername()));
	}

	// -------------------------------------------------------------------------------
	// Session Management (helper methods)
	// -------------------------------------------------------------------------------

	private void putSession(ChannelHandlerContext ctx, ISession newSession) {
		// TODO: check if session is already bound to connection
		activeSessions.put(ctx, newSession);
	}

	private void removeSession(ChannelHandlerContext ctx){
		activeSessions.remove(ctx);
	}

	private Optional<ISession> getSession(ChannelHandlerContext ctx) {
		ISession session = activeSessions.get(ctx);
		return session != null? Optional.of(session):Optional.empty();
	}

	private Optional<ChannelHandlerContext> getCtx(ISession session){
		if (session instanceof ConnectionWrapper){
			return Optional.of(((ConnectionWrapper) session).getCtx());
		}

		for(Map.Entry<ChannelHandlerContext, ISession> e : activeSessions.entrySet()){
			if (e.getValue().equals(session)){
				return Optional.of(e.getKey());
			}
		}
		return Optional.empty();
	}

	// -------------------------------------------------------------------------------
	// Help methods: Send only objects of type IMessage
	// -------------------------------------------------------------------------------

	private void sendToClient(ChannelHandlerContext ctx, IResponseMessage message) {
		ctx.writeAndFlush(message);
	}

	private void sendToAll(IServerMessage msg) {
		for (ChannelHandlerContext client : connectedClients) {
			try {
				client.writeAndFlush(msg);
			} catch (Exception e) {
				// TODO: Handle exception for unreachable clients
				e.printStackTrace();
			}
		}
	}

}
