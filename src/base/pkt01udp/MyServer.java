/**
 * @Date 2018/11/26
 * @Author LZD
 *
 * 服务器端
 *  1.创建服务器 + 端口
 *  2.准备接受容器
 *  3.构造DatagramPacket, 用于接受数据包
 *  4.接受数据
 *  5.分析数据
 *  6.释放资源
 */
package base.pkt01udp;

import java.io.*;
import java.net.DatagramPacket;
import java.net.DatagramSocket;

public class MyServer {
    public static void main(String[] args) throws IOException {
        // 1.创建服务器 + 端口
        DatagramSocket server = new DatagramSocket(9999);

        // 2.准备接受容器
        byte[] container = new byte[1024];

        // 3.构造DatagramPacket, 创建一个接受容器大小的包
        DatagramPacket datagramPacket = new DatagramPacket(container, container.length);

        // 4.接受数据
        server.receive(datagramPacket);

        // 5.分析数据
//        byte[] data = datagramPacket.getData();
//        int len = datagramPacket.getLength();
//        System.out.println(new String(data, 0, len));
        double data = convert(datagramPacket.getData());
        System.out.println(data);

        // 6.释放资源
        server.close();
    }

    // 将字节数组转换成double类型
    public static double convert(byte[] data) throws IOException {
        double num;
        DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
        num = dis.readDouble();
        dis.close();

        return num;
    }
}
