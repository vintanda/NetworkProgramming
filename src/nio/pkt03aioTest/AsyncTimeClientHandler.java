package nio.pkt03aioTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;
import java.util.concurrent.CountDownLatch;

/**
 * @author lzd
 * @date 2019/01/05
 *
 * 客户端发送/接受数据的逻辑部分
 */
public class AsyncTimeClientHandler implements Runnable,
        CompletionHandler<Void, AsyncTimeClientHandler> {

    private AsynchronousSocketChannel client;
    private String host;
    private int port;
    private CountDownLatch latch;

    public AsyncTimeClientHandler(String host, int port) {
        this.host = host;
        this.port = port;

        try {
            // 打开连接
            client = AsynchronousSocketChannel.open();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {
        // 保证异步操作完成
        latch = new CountDownLatch(1);

        // connect是异步操作, 进行连接
        client.connect(new InetSocketAddress(host, port), this, this);

        try {
            latch.wait();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void completed(Void result, AsyncTimeClientHandler attachment) {
        byte[] req = "QUERY TIME ORDER".getBytes();
        ByteBuffer writeBuffer = ByteBuffer.allocate(req.length);
        writeBuffer.put(req);
        writeBuffer.flip();

        // 异步写, 将向服务器发送数据的数据写入通道
        client.write(writeBuffer, writeBuffer,
                new CompletionHandler<Integer, ByteBuffer>() {
                    // 回调函数
                    @Override
                    public void completed(Integer result, ByteBuffer buffer) {
                        if (buffer.hasRemaining()) {
                            client.write(buffer, buffer, this);
                        }else {
                            ByteBuffer readBuffer = ByteBuffer.allocate(1024);

                            // 读取通道中从服务器返回的数据
                            client.read(readBuffer, readBuffer,
                                    new CompletionHandler<Integer, ByteBuffer>() {

                                        // 回调函数
                                        @Override
                                        public void completed(Integer result, ByteBuffer buffer) {
                                            buffer.flip();
                                            byte[] bytes = new byte[buffer.remaining()];
                                            buffer.get(bytes);
                                            String body;
                                            try {
                                                body = new String(bytes, "UTF-8");
                                                System.out.println("Now is:" + body);
                                                latch.countDown();
                                            } catch (UnsupportedEncodingException e) {
                                                e.printStackTrace();
                                            }
                                        }

                                        @Override
                                        public void failed(Throwable exc, ByteBuffer attachment) {
                                            try {
                                                client.close();
                                                latch.countDown();
                                            } catch (IOException e) {
                                                e.printStackTrace();
                                            }
                                        }
                                    });
                        }
                    }

                    @Override
                    public void failed(Throwable exc, ByteBuffer attachment) {
                        try {
                            client.close();
                            latch.countDown();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
    }

    @Override
    public void failed(Throwable exc, AsyncTimeClientHandler attachment) {
        exc.printStackTrace();
        try {
            client.close();
            latch.countDown();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
