/**
 * @Date 2018/11/27
 * @Author LZD
 */
package base.pkt02tcp.chatDemo.demo02;

import java.io.DataInputStream;
import java.io.IOException;
import java.net.Socket;

/*
    接收线程
 */
public class Receive implements Runnable {
    // 输入流
    private DataInputStream dis;

    // 线程运行标志
    private boolean isRunning = true;

    // 初始化
    public Receive() {
    }

    public Receive(Socket client) {
        try {
            dis = new DataInputStream(client.getInputStream());
        } catch (IOException e) {
//            e.printStackTrace();
            isRunning = false;
            CloseUtil.CloseAll(dis);
        }
    }

    // 接受数据
    public String receive() {
        String msg = "";
        try {
            msg = dis.readUTF();
        } catch (IOException e) {
//            e.printStackTrace();
            isRunning = false;
            CloseUtil.CloseAll(dis);
        }
        return msg;
    }

    // 线程体
    @Override
    public void run() {
        while (isRunning) {
            System.out.println(receive());
        }
    }
}
