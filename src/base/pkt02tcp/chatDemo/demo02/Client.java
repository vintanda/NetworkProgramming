/**
 * @Date 2018/11/27
 * @Author LZD
 */
package base.pkt02tcp.chatDemo.demo02;

import java.io.*;
import java.net.Socket;

/*
    客户端：发送 + 接收数据
    输入流和输出流使用两个独立线程处理接收和发送数据的功能
 */
public class Client {
    public static void main(String[] args) throws IOException {
        Socket client = new Socket("localhost", 9999);

        // 发送数据线程和接收数据线程
        new Thread(new Send(client)).start();
        new Thread(new Receive(client)).start();
    }
}
