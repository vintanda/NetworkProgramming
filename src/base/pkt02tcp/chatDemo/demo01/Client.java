/**
 * @Date 2018/11/27
 * @Author LZD
 */
package base.pkt02tcp.chatDemo.demo01;

import java.io.*;
import java.net.Socket;

/*
    客户端：发送 + 接收数据
    问题：
        输入流和输出流在同一个线程内, 应该独立处理, 使用两个线程
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket socket = new Socket("localhost", 9999);

        // 控制台输入内容作为输入流
        BufferedReader console = new BufferedReader(new InputStreamReader(System.in));
        DataOutputStream dos = new DataOutputStream(socket.getOutputStream());
        DataInputStream dis = new DataInputStream(socket.getInputStream());

        // 死循环发送多条数据
        while (true) {
            String info = console.readLine();

            // 输出流 发送数据
            dos.writeUTF(info);
            dos.flush();

            // 输入流 接受数据
            String msg = dis.readUTF();
            System.out.println(msg);
        }
    }
}
