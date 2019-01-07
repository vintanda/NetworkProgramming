/**
 * @Date 2018/11/26
 * @Author LZD
 *
 * 客户端
 *  1.创建客户端 + 端口
 *  2.准备数据
 *  3.打包, 要指定目的地的主机地址和端口号
 *  4.发送数据
 *  5.释放资源
 */
package base.pkt01udp;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetSocketAddress;

public class MyClient {
    public static void main(String[] args) throws IOException {
        // 1.创建客户端 + 端口
        DatagramSocket client = new DatagramSocket(8888);

        // 2.准备数据
//        String msg = "udp编程练习";

        double msg = 12.19;
        byte[] data = convert(msg);

        // 3.打包
        DatagramPacket datagramPacket = new DatagramPacket(data, data.length,
                new InetSocketAddress("localhost", 9999));

        // 4.发送数据
        client.send(datagramPacket);

        // 5.释放资源
        client.close();
    }

    // 将double类型转换成字节数组
    public static byte[] convert(double num) throws IOException {
        byte[] data = null;
        ByteArrayOutputStream bos = new ByteArrayOutputStream();
        DataOutputStream dos = new DataOutputStream(bos);
        dos.writeDouble(num);
        dos.flush();

        // 获取数据
        data = bos.toByteArray();

        dos.close();

        return data;
    }
}
