package netty01.pkt01test;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;

/**
 * @author lzd
 * @date 2019/01/07
 *
 * Netty创建客户端:入口
 */
public class TimeClient {
    public void connect(int port, String host) throws Exception {
        // 配置客户端线程组
        EventLoopGroup group = new NioEventLoopGroup();
        try {
            Bootstrap b = new Bootstrap();
            b.group(group).channel(NioSocketChannel.class)
                    .option(ChannelOption.TCP_NODELAY, true)    // TCP连接
                    .handler(new ChannelInitializer<SocketChannel>() {   // 处理网络I/O事件的Handler
                        @Override
                        protected void initChannel(SocketChannel socketChannel) throws Exception {
                            // 在初始化NioSocketChannel时将TimeClientHandler加入pipeline末尾
                            socketChannel.pipeline().addLast(new TimeClientHandler());
                        }
                    });

            // 同步等待异步的连接操作完成
            ChannelFuture f = b.connect(host, port).sync();

            // 等待客户端链路关闭
            f.channel().closeFuture().sync();
        }finally {
            // 优雅退出
            group.shutdownGracefully();
        }
    }

    public static void main(String[] args) throws Exception {
        int port = 9999;

        // 异步连接
        new TimeClient().connect(port, "127.0.0.1");
    }
}
