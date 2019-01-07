/**
 * @Date 2018/12/14
 * @Author LZD
 *
 * NIO客户端test
 */
package nio.pkt01test;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.nio.ByteBuffer;
import java.nio.channels.SocketChannel;
import java.util.Scanner;

public class NIOClient {
    public static void main(String[] args) {
        // 远程地址创建
        InetSocketAddress remote = new InetSocketAddress("localhost", 9999);
        SocketChannel channel = null;

        // 定义缓存
        ByteBuffer buffer = ByteBuffer.allocate(1024);

        try {
            // 开启通道
            channel = SocketChannel.open();

            // 连接远程服务器
            channel.connect(remote);
            Scanner console = new Scanner(System.in);
            while (true) {
                System.out.println("请输入信息：");
                String line = console.nextLine();

                // 退出
                if (line.equals("exit")) {
                    break;
                }

                // 将控制台输入的数据写入缓存
                buffer.put(line.getBytes("UTF-8"));

                // 重置缓存游标
                buffer.flip();

                // 将数据发送给服务器
                channel.write(buffer);

                // 读取从服务器返回的数据
                int readLength = channel.read(buffer);
                if (readLength == -1) {
                    break;
                }

                // 重置缓存游标
                buffer.flip();

                // 创建有效数据大小的字节数组
                byte[] data = new byte[buffer.remaining()];

                // 读取数据到字节数组
                buffer.get(data);

                // 输出收到的消息到控制台
                System.out.println("from server:" + new String(data, "UTF-8"));

                // 清空缓存
                buffer.clear();
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            // 关闭资源
            if (null != channel) {
                try {
                    channel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}
