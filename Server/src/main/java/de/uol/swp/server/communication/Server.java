package de.uol.swp.server.communication;

import com.google.common.eventbus.DeadEvent;
import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.Subscribe;
import de.uol.swp.common.command.GenericCommand;
import de.uol.swp.common.exception.ExceptionMessage;
import de.uol.swp.common.message.IMessage;
import de.uol.swp.common.user.IUserService;
import de.uol.swp.common.user.Session;
import de.uol.swp.common.user.message.UsersListMessage;
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

import java.net.InetSocketAddress;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CopyOnWriteArrayList;

public class Server implements ServerHandlerDelegate {
	/**
	 * Server port
	 */
	final private int port;

	/**
	 * User Service
	 */
	final private IUserService userService;

	/**
	 * Clients that are connected
	 */
	final private List<ChannelHandlerContext> connectedClients = new CopyOnWriteArrayList<>();

	/**
	 * Client with logged in sessions
	 */
	final private Map<ChannelHandlerContext, Session> activeSessions = new HashMap<>();

	/**
	 * For demo reasons the eventBus as part of this class
	 */
	final private EventBus eventBus;

	/**
	 * Creates a new Server Object and start listening on given port
	 *
	 * @param port
	 *            The port the server should listen for new connection
	 * @param userService
	 *            The userService that should be used for the server
	 */
	public Server(int port, IUserService userService, EventBus eventBus) {
		this.port = port;
		this.userService = userService;
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
	public void process(ChannelHandlerContext ctx, IMessage msg) {

		try {
			// Bind msg to ctx
			msg.setInfo(ctx);
			eventBus.post(msg);

		} catch (Exception e) {
			System.err.println("ServerException " + e.getClass().getName() + " " + e.getMessage());
			sendToClient(ctx, new ExceptionMessage(e));
		}
	}

	@Subscribe
	private void handleEventBusError(DeadEvent deadEvent){
		System.err.println("DeadEvent detected "+deadEvent);
	}



	@Subscribe
	private void processGenericCommand(GenericCommand msg) {
		if (msg.getInfo() instanceof ChannelHandlerContext) {
			ChannelHandlerContext ctx = (ChannelHandlerContext) msg.getInfo();
			System.out.println("Got a command " + msg.getCommand() + " for session " + msg.getSession());
			checkLogin(ctx, msg);
			switch (msg.getCommand()) {
			case RETRIEVE_USERS_LIST:
				UsersListMessage user = new UsersListMessage(userService.retrieveAllUsers(msg.getSession()));
				sendToClient(ctx, user);
				break;
			default:
				System.err.println("Unknown command!");
			}
		}
	}



	// -------------------------------------------------------------------------------
	// Handling of connected clients
	// -------------------------------------------------------------------------------
	@Override
	public void newClientConnected(ChannelHandlerContext ctx) {
		System.err.println("New client " + ctx + " connected");
		connectedClients.add(ctx);
	}

	@Override
	public void clientDisconnected(ChannelHandlerContext ctx) {
		System.err.println("Client disconnected");
		removeUser(ctx, getSession(ctx));
		connectedClients.remove(ctx);
	}

	// -------------------------------------------------------------------------------
	// Session Management
	// -------------------------------------------------------------------------------
	private void putSession(ChannelHandlerContext ctx, Session newSession) {
		// TODO: check if session is already bound to connection
		activeSessions.put(ctx, newSession);
	}

	private Session getSession(ChannelHandlerContext ctx) {
		return activeSessions.get(ctx);
	}

	// -------------------------------------------------------------------------------
	// Help methods: Send only objects of type IMessage
	// -------------------------------------------------------------------------------

	private void sendToClient(ChannelHandlerContext ctx, IMessage message) {
		ctx.writeAndFlush(message);
	}

	private void sendToAll(IMessage msg) {
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
