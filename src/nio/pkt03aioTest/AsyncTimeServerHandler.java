package nio.pkt03aioTest;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.channels.AsynchronousServerSocketChannel;
import java.util.concurrent.CountDownLatch;

/**
 * @author lzd
 * @date 2019/01/04
 *
 * 服务端执行客户端连接
 */
public class AsyncTimeServerHandler implements Runnable {

    private int port;

    CountDownLatch countDownLatch;
    AsynchronousServerSocketChannel asynchronousServerSocketChannel;

    public AsyncTimeServerHandler(int port) {
        this.port = port;

        try {
            asynchronousServerSocketChannel = AsynchronousServerSocketChannel.open();
            asynchronousServerSocketChannel.bind(new InetSocketAddress(port));
            System.out.println("The time server is start in port:" + port);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        countDownLatch = new CountDownLatch(1);
        doAccept();

        try {
            countDownLatch.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    // 连接 AcceptCompletionHandler中的completed方法用于处理连接客户端后新的客户端连接
    public void doAccept() {
        asynchronousServerSocketChannel.accept(this,
                new AcceptCompletionHandler());
    }
}
