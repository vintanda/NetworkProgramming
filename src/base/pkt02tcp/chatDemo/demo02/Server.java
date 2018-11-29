/**
 * @Date 2018/11/27
 * @Author LZD
 */
package base.pkt02tcp.chatDemo.demo02;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

/*
    客户端：发送 + 接收数据
 */
public class Server {
    public static void main(String[] args) throws IOException {
        ServerSocket server = new ServerSocket(9999);

        // 死循环为每一个客户建立一个连接, 但是服务器对用户的处理只能顺序处理
        // 改进：应采用多线程进行处理
        while (true) {
            Socket socket = server.accept();

            // 输入流 接收数据
            DataInputStream dis = new DataInputStream(socket.getInputStream());

            // 输出流 发送数据
            DataOutputStream dos = new DataOutputStream(socket.getOutputStream());

            while (true) {
                String msg = dis.readUTF();
                System.out.println(msg);

                dos.writeUTF("服务器收到来自客户端的信息：" + msg);
                dos.flush();
            }
        }
    }
}
