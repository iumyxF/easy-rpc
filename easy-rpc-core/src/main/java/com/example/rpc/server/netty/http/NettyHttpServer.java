package com.example.rpc.server.netty.http;

import com.example.rpc.server.NetServer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpRequestDecoder;
import io.netty.handler.codec.http.HttpResponseEncoder;
import io.netty.util.concurrent.DefaultThreadFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.net.InetSocketAddress;

/**
 * Netty Http 服务器
 *
 * @author iumyxF
 * @date 2024/5/13 21:01
 */
public class NettyHttpServer implements NetServer {

    private static final Logger log = LoggerFactory.getLogger(NettyHttpServer.class);

    private EventLoopGroup bossGroup;

    private EventLoopGroup workerGroup;

    private Channel channel;

    @Override
    public void start(int port) {
        bossGroup = new NioEventLoopGroup(2, new DefaultThreadFactory("netty-http-boss", true));
        workerGroup = new NioEventLoopGroup(4, new DefaultThreadFactory("netty-http-worker", true));
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
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel channel) {
                        // http 编解码
                        channel.pipeline().addLast(new HttpRequestDecoder());
                        channel.pipeline().addLast(new HttpResponseEncoder());
                        // http 消息聚合器 内容长度不超过512kb
                        channel.pipeline().addLast("httpAggregator", new HttpObjectAggregator(512 * 1024));
                        // 自定义处理器
                        channel.pipeline().addLast(new NettyServerHandler());
                    }
                });
        try {
            ChannelFuture future = bootstrap.bind().sync();
            if (future.isSuccess()) {
                channel = future.channel();
                log.info("Netty HTTP server started successfully, listening port is {}", port);
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