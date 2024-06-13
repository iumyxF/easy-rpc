package com.example.rpc.server.netty.tcp;

import com.example.rpc.server.NetServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @author iumyxF
 * @description: Netty Tcp 服务器
 * @date 2024/6/13 8:43
 */
public class NettyTcpServer implements NetServer {

    private static final Logger log = LoggerFactory.getLogger(NettyTcpServer.class);

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private Channel channel;

    @Override
    public void start(int port) {
        bossGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("netty-tcp-boss", true));
        workerGroup = new NioEventLoopGroup(4, new DefaultThreadFactory("netty-tcp-worker", true));
        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap
                .group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .localAddress(new InetSocketAddress(port))
                //服务端可连接队列
                .option(ChannelOption.SO_BACKLOG, 128)
                //解决端口占用问题
                .option(ChannelOption.SO_REUSEADDR, true)
                .childOption(ChannelOption.SO_KEEPALIVE, true)
                .childOption(ChannelOption.TCP_NODELAY, true)
                .childOption(ChannelOption.ALLOCATOR, PooledByteBufAllocator.DEFAULT)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        // 读取超时处理器
                        channel.pipeline().addLast(new ReadTimeoutHandler(3, TimeUnit.SECONDS));
                        channel.pipeline().addLast(new RequestDecoder());
                        channel.pipeline().addLast(new ResponseEncoder());
                        channel.pipeline().addLast(new NettyTcpHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind().sync();
            if (future.isSuccess()) {
                channel = future.channel();
                log.info("Netty TCP server started successfully, listening port is {}", port);
                future.channel().closeFuture().sync();
            }
        } catch (Exception ex) {
            log.error(ex.toString(), ex);
        }
    }

    @Override
    public void stop() {
        if (channel != null) {
            channel.close();
        }
        if (bossGroup != null) {
            bossGroup.shutdownGracefully();
        }
        if (workerGroup != null) {
            workerGroup.shutdownGracefully();
        }
    }
}
