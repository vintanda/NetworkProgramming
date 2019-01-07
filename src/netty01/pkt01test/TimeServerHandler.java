package netty01.pkt01test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerAdapter;
import io.netty.channel.ChannelHandlerContext;

import java.io.UnsupportedEncodingException;

/**
 * @author lzd
 * @date 2019/01/07
 *
 * 服务端:用于处理网络I/O事件的Handler
 */
public class TimeServerHandler extends ChannelHandlerAdapter {

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws UnsupportedEncodingException {
        // 解码 msg(Object) -> buf(ByteBuf) -> body(String)
        // ByreBuf相当于java.nio.ByteBuffer
        ByteBuf buf = (ByteBuf) msg;
        byte[] req = new byte[buf.readableBytes()];    // 按照缓冲区有的字节数创建数组大小
        buf.readBytes(req);    // 读数据到数组中
        String body = new String(req, "UTF-8");
        System.out.println("The time server receive order:" + body);

        // 判断发送请求的内容
        String currentTime = "QUERY TIME ORDER".equalsIgnoreCase(body) ? new java.util.Date(
                System.currentTimeMillis()).toString() : "BAD ORDER";
        ByteBuf resp = Unpooled.copiedBuffer(currentTime.getBytes());

        // 异步发送消息给客户端
        ctx.write(resp);
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        ctx.close();
    }
}
