/**
 * @Date 2018/12/18
 * @Author LZD
 *
 * buffer的三个标记：
 *   position:游标, 读/写开始的位置
 *   limit:限制, 初始值是capacity, 游标重置 => limit = position
 *   capacity:buffer的容量
 *
 * Buffer的应用固定逻辑：
 *   写逻辑：
 *     1.buffer.clear();
 *     2.buffer.put();
 *     3.buffer.flip();
 *     4.SocketChannel.write(buffer);
 *     5.buffer.clear();
 *
 *   读逻辑：
 *     1.buffer.clear();
 *     2.SocketChannel.read(buffer);
 *     3.buffer.flip();
 *     4.buffer.get();
 *     5.buffer.clear();
 */
/*
copy from jdk：
    public final Buffer flip() {
        limit = position;
        position = 0;
        mark = -1;
        return this;
    }
    public final int remaining() {
        return limit - position;
    }
    public final Buffer clear() {
        position = 0;
        limit = capacity;
        mark = -1;
        return this;
    }
 */
package nio.pkt02buffer;

import java.nio.ByteBuffer;

public class BufferTest {
    public static void main(String[] args) {

        ByteBuffer buffer = ByteBuffer.allocate(8);

        byte[] tmp = new byte[]{3, 2, 1};

        // 写入数据之前：java.nio.HeapByteBuffer[pos=0 lim=8 cap=8]
        System.out.println("写入数据之前：" + buffer);

        buffer.put(tmp);

        // 写入数据之后：java.nio.HeapByteBuffer[pos=3 lim=8 cap=8]
        System.out.println("写入数据之后：" + buffer);

        // 重置游标
        buffer.flip();

        // 重置游标之后：java.nio.HeapByteBuffer[pos=0 lim=3 cap=8]
        System.out.println("重置游标之后：" + buffer);

        /*
            0 - 3
            1 - 2
            2 - 1
         */
        for (int i = 0;i < buffer.remaining();i++) {
            int data = buffer.get(i);
            System.out.println(i + " - " + data);
        }
    }
}
