package cn.yescallop.darkxiangqiserver.socket;

import cn.yescallop.darkxiangqiserver.Client;
import cn.yescallop.darkxiangqiserver.ClientManager;
import cn.yescallop.darkxiangqiserver.Server;
import cn.yescallop.darkxiangqiserver.packet.Packet;
import cn.yescallop.darkxiangqiserver.util.Binary;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.ByteBuf;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

@ChannelHandler.Sharable
public class TCPServerSocket extends ChannelInboundHandlerAdapter {

    protected ServerBootstrap bootstrap;
    protected EventLoopGroup bossGroup;
    protected EventLoopGroup workerGroup;
    protected Channel channel;

    public TCPServerSocket(int port, String interfaz) {
        try {
            bootstrap = new ServerBootstrap();
            bossGroup = new NioEventLoopGroup();
            workerGroup = new NioEventLoopGroup();
            bootstrap
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .option(ChannelOption.SO_BACKLOG, 128)
                    .option(ChannelOption.TCP_NODELAY, true)
                    .childOption(ChannelOption.SO_KEEPALIVE, true)
                    .childHandler(new ChannelInitializer<SocketChannel>() {
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            socketChannel.pipeline().addLast(TCPServerSocket.this);
                        }
                    });
            channel = bootstrap.bind(interfaz, port).sync().channel();
        } catch (Exception e) {
            System.out.println("Failed to bind to " + interfaz + ":" + port);
            System.exit(1);
        }
    }

    public void close() {
        this.bossGroup.shutdownGracefully();
        this.workerGroup.shutdownGracefully();
        try {
            this.channel.closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        ClientManager.getInstance().addClient(ctx.channel());
        super.channelRegistered(ctx);
    }

    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        ClientManager.getInstance().removeClient(ctx.channel());
        super.channelUnregistered(ctx);
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf byteBuf = (ByteBuf) msg;
        if (byteBuf.readableBytes() == 0) return;
        byte[] buf = new byte[byteBuf.readableBytes()];
        byteBuf.readBytes(buf);
        Packet pk = Packet.fromBuffer(buf);
        Client client = ClientManager.getInstance().getClient(ctx.channel());
        if (pk != null) {
            System.out.println(pk.getClass().getSimpleName() + " " + client.address().toString() + " <-");
            Server.getInstance().handlePacket(client, pk);
        } else {
            System.out.println("Unknown " + Binary.bytesToHexString(buf) + " " + client.address().toString() + " <-");
        }
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) {
        ctx.close();
        cause.printStackTrace();
    }
}
