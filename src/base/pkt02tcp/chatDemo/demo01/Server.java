/**
 * @Date 2018/11/27
 * @Author LZD
 */
package base.pkt02tcp.chatDemo.demo01;

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
        Socket socket = server.accept();

        // 输入流 接收数据
        DataInputStream dis = new DataInputStream(socket.getInputStream());
        String msg = dis.readUTF();

        // 输出流 发送数据
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        dos.writeUTF("服务器收到来自客户端的信息：" + msg);
        dos.flush();

    }
}
