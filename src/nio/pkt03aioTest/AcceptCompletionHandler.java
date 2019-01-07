package nio.pkt03aioTest;

import java.nio.ByteBuffer;
import java.nio.channels.AsynchronousSocketChannel;
import java.nio.channels.CompletionHandler;

/**
 * @author lzd
 * @date 2019/01/04
 *
 * 服务端 异步连接客户端 执行异步读操作
 */
public class AcceptCompletionHandler implements CompletionHandler
        <AsynchronousSocketChannel, AsyncTimeServerHandler> {

    // 连接上客户端后新的客户端连接, 回调该方法
    @Override
    public void completed(AsynchronousSocketChannel result,
                          AsyncTimeServerHandler attachment) {
        attachment.asynchronousServerSocketChannel.accept(attachment, this);

        ByteBuffer buffer = ByteBuffer.allocate(1024);

        // 异步读操作
        result.read(buffer, buffer, new ReadCompletionHandler(result));
    }

    @Override
    public void failed(Throwable exc, AsyncTimeServerHandler attachment) {
        exc.printStackTrace();
        attachment.countDownLatch.countDown();
    }
}
