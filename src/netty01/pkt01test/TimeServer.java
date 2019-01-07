package netty01.pkt01test;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;

/**
 * @author lzd
 * @date 2019/01/07
 *
 * Netty创建服务端:服务端入口
 */
public class TimeServer {
    public void bind(int port) throws Exception {
        // 创建NIO线程组
        // 一个用于处理客户端连接
        // 另一个用于进行SocketChannel的网络读写
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        try {
            // 创建ServerBootstrap对象 -> 是Netty用于启动NIO服务端的辅助启动类, 目的是降低服务端开发难度
            ServerBootstrap b = new ServerBootstrap();
            b.group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)     // 设置channel为NioServerSocketChannel
                    .option(ChannelOption.SO_BACKLOG, 1024)     // 设置backlog数为1024
                    .childHandler(new ChildChannelHandler());    // 绑定I/O事件处理类

            // 绑定端口, 同步等待成功
            // 使用sync方法保证同步等待绑定方法结束后返回一个ChannelFuture对象
            ChannelFuture f = b.bind(port).sync();

            // 等待服务端监听端口关闭 -> 等待服务端链路
            f.channel().closeFuture().sync();
        }finally {
            // 优雅退出, 释放线程组资源
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }

    private class ChildChannelHandler extends ChannelInitializer<SocketChannel> {

        @Override
        protected void initChannel(SocketChannel arg0) throws Exception {
            arg0.pipeline().addLast(new TimeServerHandler());
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 9999;

        // 异步监听
        new TimeServer().bind(port);
    }
}
