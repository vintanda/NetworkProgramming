/**
 * @Date 2018/11/27
 * @Author LZD
 *
 * 客户端：
 *   1.创建客户端, 指定服务器主机地址及端口号
 *   2.发送 + 接受数据
 */
package base.pkt02tcp;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;

public class Client {
    public static void main(String[] args) throws IOException {
        // 创建客户端 必须指定服务器和端口, 创建同时向指定host + port建立连接
        Socket client = new Socket("localhost", 8989);

        // 接收数据
        // 使用BufferedXX在读取时使用的是readLine, 需要添加换行符否则无法读到
//        BufferedReader br = new BufferedReader(new InputStreamReader(client.getInputStream()));
//        String msg = br.readLine();
//        System.out.println(msg);

        // 使用DataInputStream
        DataInputStream dis = new DataInputStream(client.getInputStream());
        String msg = dis.readUTF();
        System.out.println(msg);

    }
}
