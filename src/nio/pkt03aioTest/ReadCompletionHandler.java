package nio.pkt03aioTest;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author lzd
 * @date 2019/01/04
 *
 * 服务端 异步连接客户端 执行异步读操作
 */
public class ReadCompletionHandler implements CompletionHandler<Integer, ByteBuffer> {
    private AsynchronousSocketChannel channel;

    public ReadCompletionHandler(AsynchronousSocketChannel channel) {
        if (this.channel == null) {
            this.channel = channel;
        }
    }

    @Override
    public void completed(Integer result, ByteBuffer attachment) {
        // 将管道中的消息读到缓冲区
        attachment.flip();
        byte[] body = new byte[attachment.remaining()];
        attachment.put(body);
        try {
            String req = new String(body, "UTF-8");
            System.out.println("The time server receive order:" + req);
            String currentTime = "QUERY TIME ORDER".equalsIgnoreCase("req") ?
                    new java.util.Date(System.currentTimeMillis()).toString() : "BAD ORDER";

            // 向管道中写数据
            doWrite(currentTime);
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }

    }

    private void doWrite(String currentTime) {
        if (currentTime != null && currentTime.trim().length() > 0) {
            byte[] bytes = currentTime.getBytes();
            ByteBuffer writeBuffer = ByteBuffer.allocate(bytes.length);
            writeBuffer.put(bytes);
            writeBuffer.flip();

            // 异步写
            channel.write(writeBuffer, writeBuffer,
                    new CompletionHandler<Integer, ByteBuffer>() {
                        // 没写完回调然后继续写
                        @Override
                        public void completed(Integer result, ByteBuffer buffer) {
                            if (buffer.hasRemaining())
                                channel.write(buffer, buffer, this);
                        }

                        @Override
                        public void failed(Throwable exc, ByteBuffer attachment) {
                            try {
                                channel.close();
                            } catch (IOException e) {
                                e.printStackTrace();
                            }
                        }
                    });
        }
    }

    // failed方法就是出现异常之后的处理方式
    @Override
    public void failed(Throwable exc, ByteBuffer attachment) {
        try {
            this.channel.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
